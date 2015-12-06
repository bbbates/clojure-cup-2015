(ns ikea-clojure-cup.pack-it
  (:require [ajax.core :refer [GET]]
            [cljs.core.async :as async]
            [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [ikea-clojure-cup.autocomplete]
            [ikea-clojure-cup.regions :refer [region-state]]
            [ikea-clojure-cup.select-items :as select-items]
            [ikea-clojure-cup.bootstrap :as bootstrap]
            [clojure.string :as cs])
  (:require-macros [ikea-clojure-cup.client-macros :refer [debounce]]))

(defn- heading [string]
  [:heading [:h2 string]])

(defn pack-it-view
  [all-state progress-fn recalc-progress-fn start-over-fn]
  (fn [_ _]
    (let [result-state (:results @all-state)]
      [:section.pack-it
       [:heading
        [:h2 "Step 3"]
        [:h3 "Results"]]
       [:main.pack-it-content
        [:div.results
         (case (:result result-state)
           :yes [:div
                 [:img {:src "img/itFits.png"}]]
           :no [:div
                [:img {:src "img/itDoesNotFit.png"}]]
           :partial [:div
                     [:img {:src "img/itFitsMayBe.png"}]
                     [:p "You'll have to remove or get creative with the following products:"
                      [:ul.list-inside
                       (map #(vector :li.list-inside (-> % :name str)) (:missing result-state))]]])
         [select-items/trolley-list-contents (reagent/cursor all-state [:trolley])]

         [bootstrap/button-toolbar
          [bootstrap/button {:bs-size :lg
                             :bs-style :primary
                             :on-click recalc-progress-fn} "Recalculate"]
          [bootstrap/button {:title "Start over" :bs-style :danger
                             :bs-size :lg
                             :href "/"
                             :on-click start-over-fn} "Start over"]]
         [:h3 "Boot size:"]
         [:p (str (cs/join "cm x " (-> @all-state :fleet :vehicles first vals)) "cm")]
<<<<<<< HEAD
         [:h3 "Flatpacks:"]
         [:ol.list-inside (map (fn [{:keys [width height length]}] [:li (str width "cm x " height "cm x " length "cm")]) (mapcat :packages (-> @all-state :trolley :items)))]
         [:h3 "Preview:"]
         (if (= :no (:result result-state))
           [:p "N/A"]
           [:div
            [:p.hidden-xs "In the below box, you can pan around and zoom in and out to see how to stack your packages."]
            [:iframe.hidden-xs {:width "555" :height "555" :src (str "/ikea/preview?bins=" (-> result-state :preview :bins) "&items=" (-> result-state :preview :items))}]
            [:p.visible-xs "3D preview showing how stack the packages is not available when viewing on small screen devices. Please try again on a larger screen."]])]]])))
=======
         [:h3 "Packages:"]
         [:ol.list-inside
          (map-indexed
           (fn [idx {:keys [width height length] :as package}]
             [:li {:key idx}
              (str width "cm x " height "cm x " length "cm")])
           (mapcat :packages (-> @all-state :trolley :items)))]]

        (when-not (= :no (:result result-state))
          [:div.preview
           [bootstrap/alert {:id :small-device-alert :bs-style :info}
            "3D preview showing how stack the packages is not available when viewing on small screen devices. Please try again on a larger screen."]
           [:div.three-dee-view
            [:div
             [:p "In the box below, you can pan around and zoom in and out to see how to stack your packages."]
             [:iframe {:width "555" :height "555" :src (str "/ikea/preview?bins=" (-> result-state :preview :bins) "&items=" (-> result-state :preview :items))}]]]])]])))
>>>>>>> 3a7247409ac3652b2f25dde0239bfaf24fd14f40
