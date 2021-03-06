(ns ikea-clojure-cup.ikea
  (:require [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [ring.util.http-response :refer :all]
            [ikea-clojure-cup.search :as search]
            [ikea-clojure-cup.product :as product]
            [ikea-clojure-cup.pack :as pack]))



(defroutes* ikea-routes
  (GET* "/regions" []
        (ok [{:name "Australia"
              :code "au"
              :lang "en"}]))
  (GET* "/search" []
        :query-params [query :- s/Str
                       region :- s/Str
                       lang :- s/Str]
        (ok (search/search region lang query)))
  (GET* "/product" []
        :query-params [product-context :- s/Str
                       product-id :- s/Str
                       region :- s/Str
                       lang :- s/Str]
        (ok (product/product region lang product-context product-id)))

  (POST* "/pack" []
         :body [pack-query {s/Keyword s/Any}]
         (let [transformed-params {:bins [(merge {:id "0"}
                                                 (-> pack-query :fleet :vehicles first))]
                                   :products (-> pack-query :trolley :items)}]
           (ok (pack/pack transformed-params))))

  (GET* "/preview" []
        :query-params [bins :- s/Str
                       items :- s/Str]
        (update-in (ok (pack/preview bins items))
           [:headers] assoc "content-type" "text/html;charset=ISO-8859-1")))


