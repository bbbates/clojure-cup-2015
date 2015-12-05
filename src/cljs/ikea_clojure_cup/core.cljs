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
   [bootstrap/button {:bs-size :lg :href "/"} "Try it!"]])

(defn current-page []
  [:div#wrap
   [bootstrap/nav-bar
    [bootstrap/nav-bar-brand
     [:a {:title "IKEA" :href "/"} "IKEA"]]
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
     [(session/get :current-page)]]
    [:div#footer
     [:sup "© "]
     [:a {:href "http://www.icm-consulting.com.au/"} "ICM Consulting Pty Ltd B.V 2010"]
     [:div.serial-text.pull-right "AA-498638-1"]
     [:div.serial-number.pull-right (first (shuffle (range 10000 20000)))]]]])


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
