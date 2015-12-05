(ns ikea-clojure-cup.select-items
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.autocomplete]
            [ikea-clojure-cup.regions :refer [region-state]]
            [ikea-clojure-cup.bootstrap :as bootstrap])
  (:require-macros [ikea-clojure-cup.client-macros :refer [debounce]]))

(defn- fetch-search-results
  [term]
  (let [data-ch (async/chan)
        region (:region @region-state)]
    (println "1 >>>>>>" region)
    (GET "/ikea/search"
         {:params {:region (:code region)
                   :lang (:lang region)
                   :query term}
          :handler (fn [resp] (async/put! data-ch resp))})
    data-ch))

(defn item-preview
  [{:keys [id image-src name desc]}]
  [bootstrap/thumbnail {:src image-src}
   [:h3 name]
   [:p desc]])

(defn add-item-button
  [search-state trolley-state]
  [:div.input-group-btn
   [bootstrap/button
    {:on-click #(swap! trolley-state update-in [:items] conj (::selected-item @search-state))}
    [bootstrap/glyph {:glyph :plus}] " Add to trolley"]])

(defn select-item!
  [trolley-state item]
  (swap! trolley-state assoc ::selected-item item))

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
              :highlight-class "highlighted"
              :result-fn item-preview
              :choice-fn (partial select-item! search-state)
              :addons (partial add-item-button search-state trolley-state)}]
       search-state])))

(defn trolley-list-contents
  [trolley-state]
  [:div.trolley-contents
   (if (empty? (:items @trolley-state))
     [:em "Nothing in your trolley, yet!"]
     [:ul
      (map-indexed
       (fn [idx item]
         [:li {:key idx} (:name item) (:desc item)])
       (:items @trolley-state))])])

(defn select-items-view
  [trolley-state]
  [:section.select-items
   [:heading
    [:h2 "Step 1" [:small "Enter your IKEA shopping list"]]]
   [:main.select-items-content
    [:div.search
     [item-search trolley-state]
     [trolley-list-contents trolley-state]]
    [:div.preview]]
   [:footer
    [bootstrap/button {:bs-size :lg
                       :bs-style :danger
                       :on-click #(swap! trolley-state assoc :items [])} "Clear trolley"]
    [bootstrap/button {:bs-size :lg
                       :bs-style :primary
                       :disabled (empty? (:items @trolley-state))} "Continue"]]])
