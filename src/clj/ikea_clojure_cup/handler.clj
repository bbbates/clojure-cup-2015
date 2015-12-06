(ns ikea-clojure-cup.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources] :as compojure]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]
            [environ.core :refer [env]]
            [ikea-clojure-cup.ikea :as ikea]))

(def mount-target
  [:div#app
   [:div.wait.center
   [:img.rotating-image {:src "img/allanKey.png"}]]
    [:p.center "Please wait..."]])

(def loading-page
  (html
   [:html
    [:head
     [:title "IKEA Fleet Logistics System"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     (include-css (if (env :dev) "css/ikea-helper.css" "css/ikea-helper.min.css"))]
    [:body
     mount-target
     (include-js "js/app.js")]]))

(def cards-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]]
    [:body
     mount-target
     (include-js "js/app_devcards.js")]]))

(defroutes ikea-routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (GET "/cards" [] cards-page)
  (context* "/ikea" [] ikea/ikea-routes)
  (resources "/")
  (compojure/not-found "Not Found"))

(def app
  (api
   {:format {:formats [:transit-json]}}
   (middlewares [(wrap-gzip)
                 (wrap-exceptions)
                 (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                 (wrap-reload)]
                ikea-routes)))
