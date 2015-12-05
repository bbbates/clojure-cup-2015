(ns ikea-clojure-cup.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ikea-clojure-cup.regions :as regions]
              [ikea-clojure-cup.tool :as tool]))

;; -------------------------
;; Views

(defn home-page []
  [:div
   [regions/region-modal]
   [:span (get-in @regions/region-state [:region :name])]
   [tool/tool-view]])

(defn current-page []
  [:div
   [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
