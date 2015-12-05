(ns ikea-clojure-cup.ikea
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]))




(defroutes* ikea-routes
  (GET* "/regions" []
        (ok [{:name "Australia"
              :code "au"}]))
  (GET* "/search" []
        (ok [{:name "Test 1"
              :url "/something/454543/"
              :image "/something/45254325/small.jpg"}
             {:name "Test 2"
              :url "/something/454544/"
              :image "/something/452544/small.jpg"}])))

