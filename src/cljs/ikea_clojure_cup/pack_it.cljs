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
    [:section
     (case (:result @result-state)
       :yes (heading "It'll fit!")
       :no (heading "It won't fix :(")
       :partial [:div
                 (heading "...kinda fits")
                 [:p "It'll should fit better if you remove: " (cs/join "," (map #(-> % :id str) (:missing @result-state)))]])]))
