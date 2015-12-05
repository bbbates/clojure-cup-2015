(ns ikea-clojure-cup.regions
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.bootstrap :as bootstrap]))

(defn- fetch-regions
  [region-state]
  (GET "/ikea/regions"
       :handler (fn [resp]
                  (swap! region-state assoc :all-regions resp))))

(defn- region-selector*
  [region-state]
  (if-let [all-regions (seq (:all-regions @region-state))]
    [:form
     [bind-fields
      [bootstrap/input {:id :sel-region :field :list :type "select"}
       (for [region all-regions]
         [:option {:key region :value region} (:name region)])]
      region-state]]
    [:span.loading "Fetching available regions..."]))

(defn- region-selector
  [region-state]
  [(with-meta region-selector* {:component-did-mount (partial fetch-regions region-state)})
   region-state])

(defn- region-confirm
  [region-state]
  [bootstrap/button {:bs-size :lg :on-click
                     #(swap! region-state assoc :region (:sel-region @region-state))} "Confirm"])

(defn- region-selection-copy
  [region-state]
  [:div
   [:h3 "Welcome to the Ikea Transport Planner" [:small [:em "(Proper name pending)"]]]
   [:p "Before you begin, select your closest Ikea region."]])

(defn region-modal
  [region-state]
  (when-not (:region @region-state)
     [bootstrap/modal {:auto-focus true
                       :on-hide (fn [& _] (println "Hiding!"))
                       :show true
                       :backdrop "static"
                       :enforce-focus true
                       :keyboard false}
      [bootstrap/modal-header [:h5 "Welcome!"]]
      [bootstrap/modal-body
       [region-selection-copy]
       [region-selector region-state]]
      [bootstrap/modal-footer
       [region-confirm region-state]]]))
