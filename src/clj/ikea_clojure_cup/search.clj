(ns ikea-clojure-cup.search
  (:require [org.httpkit.client :as http-kit]
            [hickory.core :as hick]
            [hickory.select :as s]
            [clojure.string :as cs]
            [clojure.pprint :refer [pprint]]
            [ikea-clojure-cup.common :refer [ikea-domain]]))

(defn- get-product-context [m]
  (-> (s/select (s/and (s/tag :a)
                       (s/attr :href)) m)
      first
      :attrs
      :href
      (cs/split #"/")
      reverse
      second))

(defn- get-product-id [m]
  (-> (s/select (s/and (s/tag :a)
                       (s/attr :href)) m)
      first
      :attrs
      :href
      (cs/split #"/")
      last))

(defn- get-img-src [m]
  (-> (s/select (s/class "ikea-product-img") m)
      first
      :attrs
      :src))

(defn- get-content [m class]
  (-> (s/select (s/class class) m)
      first
      :content
      first))

(defn- get-product-name [m]
  (get-content m "ikea-product-pricetag-name"))

(defn- get-product-desc [m]
  (let [d (cs/trim (get-content m "ikea-product-pricetag-desc"))]
    (subs d 0 (- (count d) 1))))

(defn- get-hick-products [site-htree]
  (s/select (s/child (s/id "search-list")
                     (s/and (s/tag :li)
                            (s/attr :class #(.contains % "productRow")))) site-htree))

(defn- transform-products [hick-products]
  (reduce (fn [v m]
            (conj v {:id (get-product-id m)
                     :image-src (get-img-src m)
                     :name (get-product-name m)
                     :desc (get-product-desc m)
                     :product-context (get-product-context m)}))
          []
          hick-products))

(defn search [region lang query]
  (when-not (cs/blank? query)
    (let [url (format "%s/%s/%s/search/?query=%s" ikea-domain region lang query)
          response (http-kit/get url)
          site-htree (-> @response :body hick/parse hick/as-hickory)
          hick-products (get-hick-products site-htree)]
      (transform-products hick-products))))

(comment
  (search "au" "billy"))
