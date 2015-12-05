(ns ikea-clojure-cup.ikea
  (:require [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [ring.util.http-response :refer :all]
            [ikea-clojure-cup.search :as search]))


(defroutes* ikea-routes
  (GET* "/regions" []
        (ok [{:name "Australia"
              :code "au"
              :lang "en"}]))
  (GET* "/search" []
        :query-params [query :- s/Str]
        (ok (search/search "au" query))))

