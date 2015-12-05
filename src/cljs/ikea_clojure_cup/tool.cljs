(ns ikea-clojure-cup.tool
  (:require [reagent.core :as reagent :refer [atom]]
            [ikea-clojure-cup.bootstrap :as bootstrap]
            [ikea-clojure-cup.select-items :as select-items]))

(defonce tool-state (atom {:state :select-items
                           :trolley {:items []}}))

(defmulti tool-stage-view (fn [state _ _] state))

(defmethod tool-stage-view :select-items [_ selected-region all-state]
  [select-items/select-items-view selected-region (reagent/cursor all-state [:trolley])])

(defn dismissable-introduction
  []
  (when-not (:shown-intro? @tool-state)
    [:div.intro
     [bootstrap/button {:class "close" :data-dismiss "alert" :aria-label "close" :role "alert" :on-click #(swap! tool-state assoc :shown-intro? true)}
      [:span {:aria-hidden true} "⨉"]]
     [:p [:strong "Welcome!"]
      " Quod satis pecuniae sempiternum. Ut sciat oportet motum. Nunquam invenies eum. Hic de tabula. Ego vivere, ut debui, et nunc fiant. Istuc quod opus non est. Lorem ipsum occurrebat pragmaticam semper ut, si quis ita velim tibi bene recognoscere. Quorum duo te mihi videtur. Mauris a nunc occideritis me rectum. Videtur quod Ive facillimum, qui fecit vos. Potes me interficere, sine testibus et tunc manere in pauci weeks vel mensis vestigia Isai Pinkman et vos quoque illum occidere. Exercitium inutili option A. Videtur mihi quod autem est."]]))

(defn tool-view
  [selected-region]
  [:div
   [dismissable-introduction]
   ;; TODO: breadcrumbs
   [tool-stage-view (:state @tool-state) selected-region tool-state]])
