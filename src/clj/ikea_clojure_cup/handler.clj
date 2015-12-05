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
            [environ.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(def loading-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     (include-css (if (env :dev) "css/site.css" "css/site.min.css"))]
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
  (resources "/")
  (compojure/not-found "Not Found"))

(def app
  (api
   {:format {:formats [:transit-json]}}
   (middlewares [(wrap-gzip)
                 (wrap-exceptions)
                 (wrap-defaults site-defaults)
                 (wrap-reload)]
                ikea-routes)))
