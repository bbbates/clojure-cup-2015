(ns ikea-clojure-cup.search
  (:require [org.httpkit.client :as http-kit]
            [hickory.core :as hick]
            [hickory.select :as s]
            [clojure.string :as cu]))

(def ikea-domain "http://www.ikea.com")

(defn search [region query]
  (let [url (format "%s/%s/en/search/?query=%s" ikea-domain region query)
        response1 (http-kit/get url)
        site-htree (-> @response1 :body hick/parse hick/as-hickory)
        hick-products (s/select (s/child (s/class "productContainer")
                                         (s/class "parentContainer")
                                         (s/class "productPadding")
                                         (s/tag :a)) site-htree)]
    (reduce (fn [v m]
          (let [product-id (-> m :attrs :href (cu/split #"/") last)
                content (:content m)
                image-src (-> (filter (fn [{:keys [type tag]}] (and (= :element type) (= :img tag))) content)
                              first
                              :attrs
                              :src)
                product-name (-> (filter (fn [{:keys [type tag attrs]}] (and (= :element type)
                                                                             (= :span tag)
                                                                             (= "prodName prodNameTro" (:class attrs)))) content)
                                 first
                                 :content
                                 first)]
            (conj v {:id product-id
                     :image-src (str ikea-domain image-src)
                     :name product-name})))
        []
        hick-products)))

(comment
  (search "au" "kitchen"))

