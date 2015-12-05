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
                   :query term}
          :handler (fn [resp]
                     (async/put! data-ch resp))})
    data-ch))

(defn select-items-view
  [region trolley-state]
  [:div.select-items
   [:div
    [:h2 "Step 1" [:small "Enter your IKEA shopping list"]]]
   [:div.select-items-content
    [:div.search
     [bind-fields
      [:form
       [:div {:field :autocomplete
              :id :term
              :input-placeholder "Search for IKEA Product by Name or Department"
              :data-source (partial fetch-search-results region)
              :input-class "form-control"
              :list-class "typeahead-list"
              :item-class "typeahead-item"
              :highlight-class "highlighted"
              :result-fn :name
              :choice-fn :name}]]
      trolley-state]

     ]
    [:div.preview]

    ]])
