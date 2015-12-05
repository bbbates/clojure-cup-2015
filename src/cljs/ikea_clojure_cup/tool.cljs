(ns ikea-clojure-cup.tool
  (:require [reagent.core :as reagent :refer [atom]]
            [ikea-clojure-cup.bootstrap :as bootstrap]
            [ikea-clojure-cup.select-items :as select-items]
            [ikea-clojure-cup.car :as car]))

(defonce tool-state (atom {:state :select-items
                           :trolley {:items []}
                           :fleet {:vehicles []}}))

(defn progress!
  [tool-state]
  (let [current-state (:state @tool-state)
        next-state (case current-state
                     :select-items :enter-car-dimensions)]
    (swap! tool-state assoc :state next-state)))

(defmulti tool-stage-view (fn [state _ ] state))

(defmethod tool-stage-view :select-items [_ all-state]
  [select-items/select-items-view (reagent/cursor all-state [:trolley]) (partial progress! all-state)])

(defmethod tool-stage-view :enter-car-dimensions [_ all-state]
  [car/car-info-view (reagent/cursor all-state [:fleet]) (partial progress! all-state)])

(defn dismissable-introduction
  []
  (when-not (:shown-intro? @tool-state)
    [:div.intro
     [bootstrap/button {:class "close" :data-dismiss "alert" :aria-label "close" :role "alert" :on-click #(swap! tool-state assoc :shown-intro? true)}
      [:span {:aria-hidden true} "⨉"]]
     [:p [:strong "Welcome!"]
      " Quod satis pecuniae sempiternum. Ut sciat oportet motum. Nunquam invenies eum. Hic de tabula. Ego vivere, ut debui, et nunc fiant. Istuc quod opus non est. Lorem ipsum occurrebat pragmaticam semper ut, si quis ita velim tibi bene recognoscere. Quorum duo te mihi videtur. Mauris a nunc occideritis me rectum. Videtur quod Ive facillimum, qui fecit vos. Potes me interficere, sine testibus et tunc manere in pauci weeks vel mensis vestigia Isai Pinkman et vos quoque illum occidere. Exercitium inutili option A. Videtur mihi quod autem est."]]))

(defn tool-view
  []
  [:div
   [dismissable-introduction]
   ;; TODO: breadcrumbs
   [tool-stage-view (:state @tool-state) tool-state]])
