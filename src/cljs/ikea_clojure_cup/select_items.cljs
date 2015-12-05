(ns ikea-clojure-cup.select-items
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.autocomplete]
            [ikea-clojure-cup.bootstrap :as bootstrap])
  (:require-macros [ikea-clojure-cup.client-macros :refer [debounce]]))

(defn- fetch-search-results
  [region term]
  (let [data-ch (async/chan)]
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
  [trolley-state]
  [:div.input-group-btn
   [bootstrap/button
    {:on-click #(swap! trolley-state update-in [:items] conj (::selected-item @trolley-state))}
    [bootstrap/glyph {:glyph :plus}] " Add to trolley"]])

(defn select-item!
  [trolley-state item]
  (swap! trolley-state assoc ::selected-item item))

(defn item-search
  [region trolley-state]
  [bind-fields
      [:div {:field :autocomplete
             :id :term
             :input-placeholder "Search for IKEA Product by Name or Department"
             :data-source (partial fetch-search-results region)
             :input-class "form-control"
             :list-class "typeahead-list"
             :item-class "typeahead-item"
             :highlight-class "highlighted"
             :result-fn item-preview
             :choice-fn (partial select-item! trolley-state)
             :addons (partial add-item-button trolley-state)}]
      trolley-state])

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
  [region trolley-state]
  [:section.select-items
   [:heading
    [:h2 "Step 1" [:small "Enter your IKEA shopping list"]]]
   [:main.select-items-content
    [:div.search
     [item-search region trolley-state]
     [trolley-list-contents trolley-state]]
    [:div.preview]]
   [:footer
    [bootstrap/button {:bs-size :lg
                       :bs-style :danger
                       :on-click #(swap! trolley-state assoc :items [])} "Clear trolley"]
    [bootstrap/button {:bs-size :lg
                       :bs-style :primary
                       :disabled (empty? (:items @trolley-state))} "Continue"]]])
