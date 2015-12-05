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
         (let [transformed-params {:bins [(merge {:id "car"}
                                                 (-> pack-query :fleet :vehicles first))]
                                   :packages (flatten (reduce (fn [v {:keys [packages name]}]
                                                                (conj v (map #(assoc % :id name) packages)))
                                                              []
                                                              (-> pack-query :trolley :items))) }]
           (ok (pack/pack transformed-params)))))
