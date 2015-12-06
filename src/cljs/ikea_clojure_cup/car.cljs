(ns ikea-clojure-cup.car
  (:require [ajax.core :refer [GET]]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.bootstrap :as bootstrap]))

(defn- multiple-vehicle-entry
  [car-state-fn]
  [bootstrap/panel {:header "You and which army?"}
   [:p "This feature is coming soon..."]])

(def example-vehicles
  [{:name "Mazda 3 2005 Sedan" :dims {:width 90.2 :height 56.6 :depth 96.1}}
   {:name "BMW 1Series 2011" :dims {:width 72.6 :height 47.2 :depth 90.4}}
   {:name "VW Passat Highline Estate 2010" :dims {:width 99.1 :height 76.2 :depth 113.5}}
   {:name "Nissan Pathfinder 4x4" :dims {:width 116.5 :height 88.7 :depth 44.0}}])

(defn- example-dimensions
  [single-state]
  [:div.examples
   [:p "...or use an example vehicle:"]
   [:ul
    (for [{:keys [name dims]} example-vehicles]
      [:li {:key name}
       [:a {:href "#" :on-click #(reset! single-state dims)} name]])]])

(defn- single-vehicle-entry
  [single-state]
  [:div.single-vehicle-entry
   [bootstrap/panel {:header "One vehicle is not much of a fleet..."}
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
     single-state]
    [example-dimensions single-state]]])

(defn- enough-data?
  [vehicles]
  (every?
   (fn [{:keys [width height depth] :as v}]
     (and width height depth))
   vehicles))

(defn- vehicle-get
  ([car-state idx]
   (or (get-in @car-state [:vehicles idx])
       (do
         (vehicle-get car-state idx {:width 100 :height 75 :depth 100})
         (get-in @car-state [:vehicles idx]))))
  ([car-state idx v]
   (swap! car-state assoc-in [:vehicles idx] v)))

(defn car-info-view
  [car-state progress-fn]
  (let [mode (atom 0)]
    (fn [_ _]
      [:section.enter-car-info
       [:heading
        [:h2 "Step 2"]
        [:h3 "Tell us about your transportation fleet"]]
       [:main.select-items-content
        [:div.entry
         [bootstrap/nav {:bs-style :pills :active-key @mode :on-select #(reset! mode %)}
          [bootstrap/nav-item {:event-key 0} "Only one vehicle"]
          [bootstrap/nav-item {:event-key 1} "My fleet is vast"]]

         (case @mode
           0 [single-vehicle-entry (reagent/cursor (partial vehicle-get car-state) 0)]
           1 [multiple-vehicle-entry (partial vehicle-get car-state)])]
        [:div.preview]]
       [:footer
        [bootstrap/button-toolbar
        [bootstrap/button {:bs-size :lg
                           :bs-style :primary
                           :disabled (not (enough-data? (:vehicles @car-state)))
                           :on-click progress-fn} "Will it fit?"]
        [bootstrap/button {:bs-size :lg
                           :bs-style :danger
                           :on-click #(swap! car-state assoc :vehicles [])} "Reset fleet"]]]])))
