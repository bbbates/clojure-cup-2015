(ns ikea-clojure-cup.select-items
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [clojure.string :as cs]
            [ikea-clojure-cup.autocomplete]
            [ikea-clojure-cup.regions :refer [region-state]]
            [ikea-clojure-cup.bootstrap :as bootstrap])
  (:require-macros [ikea-clojure-cup.client-macros :refer [debounce]]))

(def number-search-results 6)

(defn- fetch-search-results
  [term]
  (let [data-ch (async/chan)
        region (:region @region-state)]
    (GET "/ikea/search"
         {:params {:region (:code region)
                   :lang (:lang region)
                   :query term}
          :handler (fn [resp] (async/put! data-ch (take number-search-results resp)))
          :error-handler (fn [resp]
                           (println "*** ERROR While performing search ***")
                           (async/put! data-ch []))})
    data-ch))

(defn item-preview
  [{:keys [id image-src name desc]}]
  [bootstrap/thumbnail {:src image-src :responsive true :title desc}
   [:h3 name]])

(defn add-item-button
  [search-state trolley-state]
  [:div.input-group-btn
   [bootstrap/button
    {:on-click #(swap! trolley-state update-in [:items] conj (::selected-item @search-state))}
    [bootstrap/glyph {:glyph :plus}] " Add to trolley"]])

(defn select-item!
  [search-state trolley-state item]
  (let [region (:region @region-state)]
    (GET "/ikea/product"
         {:params {:region (:code region)
                   :lang (:lang region)
                   :product-context (:product-context item)
                   :product-id (:id item)}
          :handler (fn [resp]
                     (swap! search-state assoc ::selected-item (assoc item :packages resp))
                     (swap! trolley-state update-in [:items] conj (::selected-item @search-state)))})))

(defn item-search
  [trolley-state]
  (let [search-state (atom {})]
    (fn []
      [bind-fields
       [:div {:field :autocomplete
              :id :term
              :input-placeholder "Search for IKEA Product by Name"
              :data-source fetch-search-results
              :input-class "form-control"
              :list-class "typeahead-list"
              :item-class "typeahead-item"
              :highlight-class "selected"
              :result-fn item-preview
              :choice-fn (partial select-item! search-state trolley-state)}]
       search-state])))

(defn package-total-overlay
  [packages]
  [bootstrap/pop-over {:id "package-popover" :title (str (count packages) " flatpacks")
                       :placement :bottom}
   "This product is packaged in " (count packages) " flatpacks or boxes:"
   [:ul
    (map (fn [{:keys [width height length]}] [:li (str width "cm x " height "cm x " length "cm")]) packages)]])

(defn package-total-view
  [item-container packages on-click-fn]
  (let [show-popover? (atom false)
        toggle-popover (fn []
                         (swap! show-popover? not)
                         (on-click-fn @show-popover?))]
    (fn [_]
      (let [package-count (count packages)]
        (if (zero? package-count)
          [bootstrap/label {:bs-style :warning}
           [bootstrap/glyph {:glyph :warning}] "No flatpacks found!"]

          [:div
           [bootstrap/label {:on-click toggle-popover}
            package-count " flatpack" (when (< 1 package-count) "s")
            [bootstrap/overlay {:show @show-popover?
                                :container (reagent/current-component)}
             [package-total-overlay packages]]]])))))

(defn trolley-item
  [{:keys [desc name image-src packages id count]} remove-fn add-another-fn on-click-fn]
  [bootstrap/list-group-item {:key id :list-item true}
   [:div
    [:div.contents
     [:div
      [:h4 name]
      [:p desc]
      [:div.item-actions
       [bootstrap/button-toolbar
        [bootstrap/button {:bs-size :xs
                           :bs-style :success
                           :on-click add-another-fn}
         [bootstrap/glyph {:glyph :plus}] " Add another"]
        [bootstrap/button {:bs-size :xs
                           :bs-style :danger
                           :on-click remove-fn}
         [bootstrap/glyph {:glyph :remove}] " Remove"]]]]
     [:div
      [package-total-view (reagent/current-component) packages on-click-fn]
      (when-not (and count (>= 1 count))
        [:h2 "⨉" count])]]
    [:div.preview
     [bootstrap/thumbnail {:src image-src :responsive true}]]]])

(defn- remove-first
  "remove the first item in the coll where the value of k matches v"
  [coll k v]
  (let [first-part (take-while #(not= (k %) v) coll)]
    (concat
     first-part
     (take-last (dec (- (count coll) (count first-part))) coll))))

(defn- remove-item-from-trolley
  [trolley-state id]
  (let [items (:items @trolley-state)]
    (swap! trolley-state update-in [:items]
           remove-first :id id)))

(defn- group-items
  [items]
  (let [grouped (group-by :id items)]
    (map (fn [[id items]]
           (assoc (first items) :count (count items)))
         grouped)))

(defn trolley-list-contents
  [trolley-state]
  [:div.trolley-contents
   [bootstrap/list-group {:component-class :ul}
    (if (empty? (:items @trolley-state))
      [bootstrap/list-group-item {:key 0 :list-item true}
       [:em "Nothing in your trolley, yet!"]]
      (map
       (fn [item]
         ^{:key (:id item)}
         [trolley-item item
          (partial remove-item-from-trolley trolley-state (:id item))
          #(swap! trolley-state update-in [:items] conj item)
          #(swap! trolley-state assoc-in [::flatpack-item] (when % (:id item)))])
       (group-items (:items @trolley-state))))]])

(defn trolley-preview
  [trolley-state]
  (let [selected-flatpack-item (::flatpack-item @trolley-state)
        items (:items @trolley-state)
        packages (reduce (fn [all item]
                           (concat all (map #(assoc % :item item) (:packages item))))
                         [] items)
        scale 0.2 ;;magic number born from guesstimation
        total-height
        (* scale (+
                  (reduce + (map (fn [{:keys [width height length]}] (min width height length)) packages))
                  (* (count packages) 2)))
        max-width (* scale (or (apply max (map (fn [{:keys [width height length]}] (max width height length)) packages)) 0))]
    [:svg {:width "100%" :height "80%"
           :view-box (clojure.string/join " " [0 0 max-width total-height])
           :preserve-aspect-ratio "xMinYMax"}
     [:g {:stroke :black
          :stroke-width (* scale 1)}
      (:rects
       (reduce
        (fn [{:keys [offset] :as m} {:keys [width height length item] :as package}]
          (let [box-height (* scale (min width height length))
                box-width (* scale (max width height length))]
            (-> m
                (update :rects conj
                        [:rect {:key (hash package)
                                :x 0
                                :y (- offset box-height)
                                :height box-height
                                :width box-width
                                :fill (if (= (:id item) selected-flatpack-item) :black :transparent)}])
                (update :offset - (* scale 1) box-height))))
        {:offset total-height :rects nil}
        packages))]]))

(defn select-items-view
  [trolley-state progress-fn]
  [:section.select-items
   [:heading
    [:h2 "Step 1"]
    [:h3 "Enter your IKEA shopping list"]]
   [:main.select-items-content
    [:div.search
     [item-search trolley-state]
     [trolley-list-contents trolley-state]]
    [:div.preview
     [:div
      [trolley-preview trolley-state]]]]
   [:footer
    [bootstrap/button-toolbar
    [bootstrap/button {:bs-size :lg
                       :bs-style :primary
                       :disabled (empty? (:items @trolley-state))
                       :on-click progress-fn} "Continue"]
     [bootstrap/button {:bs-size :lg
                       :bs-style :danger
                       :on-click #(swap! trolley-state assoc :items [])} "Clear trolley"]]]])
