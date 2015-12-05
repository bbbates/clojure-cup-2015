(ns ikea-clojure-cup.car
  (:require [ajax.core :refer [GET]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.bootstrap :as bootstrap]))

(defn- multiple-vehicle-entry
  [car-state-fn]

  )

(defn- single-vehicle-entry
  [single-state]
  [:div.single-vehicle-entry
   [:p [:em "Not much of a fleet..."]]
   [bind-fields
    [:form
     [bootstrap/input {:type :text
                       :field :numeric
                       :id :width
                       :addon-after "cm"
                       :label "Width of boot/trunk at narrowest point"}]
     [bootstrap/input {:type :text
                       :field :numeric
                       :id :height
                       :addon-after "cm"
                       :label "Height of boot/trunk at narrowest point"}]
     [bootstrap/input {:type :text
                       :field :numeric
                       :id :depth
                       :addon-after "cm"
                       :label "Depth of boot/trunk from rear of vehicle"}]]
    single-state]])

(defn- vehicle-get
  ([car-state idx]
   (get-in @car-state [:vehicles idx]))
  ([car-state idx v]
   (swap! car-state assoc-in [:vehicles idx] v)))

(defn car-info-view
  [car-state progress-fn]
  (let [mode (atom 0)]
    (fn [_ _]
      [:section.enter-car-info
       [:heading
        [:h2 "Step 2" [:small "Tell us about your transportation fleet"]]]
       [:main.select-items-content
        [bootstrap/nav {:bs-style :pills :active-key @mode :on-select #(reset! mode %)}
         [bootstrap/nav-item {:event-key 0} "Only one vehicle"]
         [bootstrap/nav-item {:event-key 1} "My fleet is vast"]]

        (case @mode
          0 [single-vehicle-entry (reagent/cursor (partial vehicle-get car-state) 0)]
          1 [multiple-vehicle-entry (partial vehicle-get car-state)])]
       [:footer
        [bootstrap/button {:bs-size :lg
                           :bs-style :danger
                           :on-click #(swap! car-state assoc :vehicles [])} "Reset fleet"]
        [bootstrap/button {:bs-size :lg
                           :bs-style :primary
                           :disabled (empty? (:vehicles @car-state))
                           :on-click #(println @car-state)} "Will it fit?"]]])))
