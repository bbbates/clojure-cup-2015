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
      [:section.select-items
       [:heading
        [:h2 "Step 3"]
        [:h3 "Results:"]]
       [:main.select-items-content
        [:div.search
         (case (:result result-state)
           :yes [:div
                 [:img {:src "img/itFits.png"}]]
           :no [:div
                [:img {:src "img/itDoesNotFit.png"}]]
           :partial [:div
                     [:img {:src "img/itFitsMayBe.png"}]
                     [:p "You'll have to remove or get creative with these products:"
                      [:ul
                       (map #(vector :li (-> % :name str)) (:missing result-state))]]])
         [:h3 "Trolley contents:"]
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
         [:h3 "Preview:"]
         (if (= :no (:result result-state))
           [:p "N/A"]
           [:div
            [:p.hidden-xs "In the below box, you can pan around and zoom in and out to see how to stack your packages."]
            [:iframe.hidden-xs {:width "500" :height "500" :src (str "/ikea/preview?bins=" (-> result-state :preview :bins) "&items=" (-> result-state :preview :items))}]
            [:p.visible-xs "3D preview showing how stack the packages is not available when viewing on small screen devices. Please try again on a larger screen."]])]]])))
