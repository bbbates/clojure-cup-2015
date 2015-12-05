(ns ikea-clojure-cup.select-items
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
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
          :handler (fn [resp] (async/put! data-ch (take number-search-results resp)))})
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
              :input-placeholder "Search for IKEA Product by Name or Department"
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
  [bootstrap/pop-over {:id "package-popover" :title (str (count packages) " packages")
                       :placement :bottom}
   "This product comes in " (count packages) " packages"])

(defn package-total-view
  [item-container packages]
  (let [show-popover (atom false)]
    (fn [_]
      (let [package-count (count packages)]
        (if (zero? package-count)
          [bootstrap/label {:bs-style :warning}
           [bootstrap/glyph {:glyph :warning}] "No packages found!"]

          [:div
           [bootstrap/label {:on-mouse-over #(reset! show-popover true)
                             :on-mouse-leave #(reset! show-popover false)}
            package-count " package" (when (< 1 package-count) "s")
            [bootstrap/overlay {:show @show-popover
                                :container (reagent/current-component)}
             [package-total-overlay packages]]]])))))

(defn trolley-item
  [idx {:keys [desc name image-src packages id]} remove-fn add-another-fn]
  [bootstrap/list-group-item {:key idx :list-item true}
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
     [:div [package-total-view (reagent/current-component) packages]]]
    [:div.preview
     [bootstrap/thumbnail {:src image-src :responsive true}]]]])

(defn- remove-item-from-trolley
  [trolley-state idx]
  (let [items (:items @trolley-state)]
    (swap! trolley-state assoc-in [:items]
           (concat (take idx items) (last (split-at (inc idx) items))))))

(defn trolley-list-contents
  [trolley-state]
  [:div.trolley-contents
   [bootstrap/list-group {:component-class :ul}
    (if (empty? (:items @trolley-state))
      [bootstrap/list-group-item {:key 0 :list-item true}
       [:em "Nothing in your trolley, yet!"]]
      (map-indexed
       (fn [idx item]
         ^{:key idx}
         [trolley-item idx item
          (partial remove-item-from-trolley trolley-state idx)
          #(swap! trolley-state update-in [:items] conj item)])
       (:items @trolley-state)))]])

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
    [:div.preview]]
   [:footer
    [bootstrap/button-toolbar
    [bootstrap/button {:bs-size :lg
                       :bs-style :danger
                       :on-click #(swap! trolley-state assoc :items [])} "Clear trolley"]
    [bootstrap/button {:bs-size :lg
                       :bs-style :primary
                       :disabled (empty? (:items @trolley-state))
                       :on-click progress-fn} "Continue"]]]])
