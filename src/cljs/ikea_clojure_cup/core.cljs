(ns ikea-clojure-cup.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [ikea-clojure-cup.regions :as regions]
            [ikea-clojure-cup.tool :as tool]
            [ikea-clojure-cup.common :as common]
            [ikea-clojure-cup.bootstrap :as bootstrap]))

;; -------------------------
;; Views

(defn home-page []
  [:div
   [regions/region-modal]
   [tool/tool-view]])

(defn about-page []
  [:div
   [regions/region-modal]
   [:h2 (str "About the " common/long-name)]
   [:p common/welcome-note]
   [:p "There are 3 possible outcomes:"
    [:ol.list-inside
     [:li "It fits!  You'll fit ALL of the packages for your products in your car!"]
     [:li "It kinda fits!  You'll fit SOME of the packages in your car but may have to get creative or say goodbye to others."]
     [:li "It does not fit!  You'll fit NONE of the packages for your products in your car."]]]
   [bootstrap/button {:bs-size :lg :bs-style :danger :href "/" :on-click tool/start-over} "Try it!"]])

(defn current-page []
  [:div
   [:div#wrap
    [bootstrap/nav-bar
     [bootstrap/nav-bar-brand
      [:div
       [:img {:src "img/iflogs-sm.png"}]
       [:a {:title "IFLOGS" :href "/"} "IKEA Fleet Logistics System" ]]]
     [bootstrap/nav {:pull-left true :class-name :hidden-xs}
      ]
     [bootstrap/nav {:pull-right true}
      [bootstrap/nav-item {:title "Change region"
                           :on-click #(swap! regions/region-state dissoc :region)}
       (str "Region: " (get-in @regions/region-state [:region :name]))]
      [bootstrap/nav-item {:title "About" :href "/about"} "About"]
      [bootstrap/nav-item {:title "Start over" :bs-style :danger
                           :href "/"
                           :on-click tool/start-over} "Start over"]]]
    [:div.container
     [:div#main
      [(session/get :current-page)]]]]
   [:div#footer.container
    "IFLOGS is not endorsed or affiliated with IKEA."
    [:br]
    [:sup "© "]
    [:a {:href "http://www.icm-consulting.com.au/"} "ICM Consulting Pty Ltd B.V 2010"]
    [:div.serial-text.pull-right "AA-498638-1"]
    [:div.serial-number.pull-right (first (shuffle (range 10000 20000)))]]])


;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
