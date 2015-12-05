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
    [:section.center
     (case (:result @result-state)
       :yes [:div
            [:img {:src "img/itFits.png"}]]
       :no [:div
            [:img {:src "img/itDoesNotFit.png"}]]
       :partial [:div
                 [:img {:src "img/itFitsMayBe.png"}]
                 [:p "It'll should fit better if you remove:"
                  [:ul
                   (map #(vector :li (-> % :name str)) (:missing @result-state))]]])]))
