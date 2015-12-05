(ns ikea-clojure-cup.tool
  (:require [reagent.core :as reagent :refer [atom]]
            [alandipert.storage-atom :refer [local-storage]]
            [ikea-clojure-cup.bootstrap :as bootstrap]
            [ikea-clojure-cup.select-items :as select-items]
            [ikea-clojure-cup.car :as car]
            [ikea-clojure-cup.calculate :as calc]
            [ikea-clojure-cup.pack-it :as pack-it]
            [ikea-clojure-cup.common :as common]))

(def default-state
  {:state :select-items
   :trolley {:items []}
   :fleet {:vehicles []}})

(defonce tool-state (local-storage (atom (assoc default-state :shown-intro? false)) :tool))

(defn start-over
  []
  (swap! tool-state merge default-state))

(defn progress!
  [tool-state]
  (let [current-state (:state @tool-state)
        next-state (case current-state
                     :select-items :enter-car-dimensions
                     :enter-car-dimensions :calculate
                     :calculate :pack-it-result)]
    (swap! tool-state assoc :state next-state)))

(defmulti tool-stage-view (fn [state _ ] state))

(defmethod tool-stage-view :select-items [_ all-state]
  [select-items/select-items-view (reagent/cursor all-state [:trolley]) (partial progress! all-state)])

(defmethod tool-stage-view :enter-car-dimensions [_ all-state]
  [car/car-info-view (reagent/cursor all-state [:fleet]) (partial progress! all-state)])

(defmethod tool-stage-view :calculate [_ all-state]
  [calc/calculate-view all-state (partial progress! all-state)])

(defmethod tool-stage-view :pack-it-result [_ all-state]
  [pack-it/pack-it-view all-state (partial progress! all-state) #(swap! all-state assoc :state :calculate) start-over])

(defn dismissable-introduction
  []
  (when-not (:shown-intro? @tool-state)
    [:div.intro
     [bootstrap/button {:class "close"
                        :data-dismiss "alert"
                        :aria-label "close"
                        :role "alert"
                        :on-click #(swap! tool-state assoc :shown-intro? true)}
      [:span {:aria-hidden true} "⨉"]]
     [:p common/welcome-note]]))

(defn tool-view
  []
  [:div
   [dismissable-introduction]
   ;; TODO: breadcrumbs
   [tool-stage-view (:state @tool-state) tool-state]])
