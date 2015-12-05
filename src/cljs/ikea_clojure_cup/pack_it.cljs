(ns ikea-clojure-cup.pack-it
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.autocomplete]
            [ikea-clojure-cup.regions :refer [region-state]]
            [ikea-clojure-cup.bootstrap :as bootstrap]
            [clojure.string :as cs])
  (:require-macros [ikea-clojure-cup.client-macros :refer [debounce]]))

(defn- heading [string]
  [:heading [:h2 string]])

(defn pack-it-view
  [result-state progress-fn]
  (fn [_ _]
<<<<<<< HEAD
    [:section.center
=======
    [:section
     [:p (str @result-state)]
>>>>>>> 34e04bae4cd0f3036e8b7f29dbdb1269f5476a10
     (case (:result @result-state)
       :yes [:div
            [:img {:src "img/itFits.png"}]]
       :no [:div
            [:img {:src "img/itDoesNotFit.png"}]]
       :partial [:div
<<<<<<< HEAD
                 [:div
            [:img {:src "img/itFitMayBe.png"}]]
                 [:p "It'll should fit better if you remove: " (cs/join "," (map #(-> % :id str) (:missing @result-state)))]])]))
=======
                 (heading "...kinda fits")
                 [:p "It'll should fit better if you remove: " (cs/join "," (map #(-> % :name str) (:missing @result-state)))]])]))
>>>>>>> 34e04bae4cd0f3036e8b7f29dbdb1269f5476a10
