(ns ikea-clojure-cup.car
  (:require [ajax.core :refer [GET]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.bootstrap :as bootstrap]))

(defn- multiple-vehicle-entry
  [car-state]

  )

(defn- single-vehicle-entry
  [car-state]

  )

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
          0 [single-vehicle-entry car-state]
          1 [multiple-vehicle-entry car-state])]
       [:footer
        [bootstrap/button {:bs-size :lg
                           :bs-style :danger
                           :on-click #(swap! car-state assoc :vehicles [])} "Reset fleet"]
        [bootstrap/button {:bs-size :lg
                           :bs-style :primary
                           :disabled (empty? (:vehicles @car-state))
                           :on-click progress-fn} "Will it fit?"]]])))
