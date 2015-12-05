(ns ikea-clojure-cup.calculate
  (:require [ajax.core :refer [POST]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.bootstrap :as bootstrap]))

(defn- fetch-search-results
  [progress-fn all-state]
  (POST "/ikea/pack"
        {:params @all-state
         :handler (fn [resp]
                    (swap! all-state assoc :results resp)
                    (progress-fn))}))

(defn- calculate-view*
  [all-state progress-fn]
  [:section.enter-car-info
   [:heading
    [:h2 "Calculating..."]
    [:p (str @all-state)]]])

(defn- calculate-view
  [all-state progress-fn]
  [(with-meta calculate-view* {:component-did-mount (partial fetch-search-results progress-fn all-state)})
   all-state])
