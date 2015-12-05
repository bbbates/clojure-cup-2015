(ns ikea-clojure-cup.search
  (:require [org.httpkit.client :as http-kit]
            [hickory.core :as hick]
            [hickory.select :as s]
            [clojure.string :as cs]
            [clojure.pprint :refer [pprint]]))

(def ikea-domain "http://www.ikea.com")

(defn- get-product-id [m]
  (-> m :attrs :href (cs/split #"/") last))

(defn- get-img-src [content]
  (str ikea-domain
       (-> (filter (fn [{:keys [type tag]}] (and (= :element type) (= :img tag))) content)
           first
           :attrs
           :src)))

(defn- get-product-name [content]
  (-> (filter (fn [{:keys [type tag attrs]}] (and (= :element type)
                                                  (= :span tag)
                                                  (= "prodName prodNameTro" (:class attrs)))) content)
      first
      :content
      first))

(defn- get-product-desc [content]
  (let [product-desc-divs (mapcat :content (filter (fn [{:keys [type tag attrs]}] (and (= :element type)
                                                                                       (= :div tag)))
                                                   content))
        product-desc (filter (fn [{:keys [type attrs]}] (= type :element) (= "prodDesc" (:class attrs))) product-desc-divs)
        product-desc (-> product-desc first :content first)]
    (when product-desc
      (cs/trim product-desc))))

(defn search [region query]
  (when-not (cs/blank? query)
    (let [url (format "%s/%s/en/search/?query=%s" ikea-domain region query)
          response1 (http-kit/get url)
          site-htree (-> @response1 :body hick/parse hick/as-hickory)
          hick-products (s/select (s/child (s/class "productContainer")
                                           (s/class "parentContainer")
                                           (s/class "productPadding")
                                           (s/tag :a)) site-htree)]
      (reduce (fn [v m]
                (let [content (:content m)]
                  (conj v {:id (get-product-id m)
                           :image-src (get-img-src content)
                           :name (get-product-name content)
                           :desc (get-product-desc content)})))
              []
              hick-products))))

(comment
  (search "au" "billy"))

(def products-sample
[{:type :element,
  :attrs
  {:href "/au/en/catalog/products/80279814/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct1",
     :src "/PIAimages/0252388_PE391190_S2.JPG",
     :border "0",
     :alt
     "BILLY Height extension unit Width: 80 cm Depth: 28 cm Height: 35 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct1", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct1",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Height extension unit \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct1", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$40\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/90263855/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct2",
     :src "/PIAimages/0252390_PE391192_S2.JPG",
     :border "0",
     :alt
     "BILLY Height extension unit Width: 80 cm Depth: 28 cm Height: 35 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct2", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct2",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Height extension unit \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct2", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$40\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/20263854/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct3",
     :src "/PIAimages/0252393_PE391191_S2.JPG",
     :border "0",
     :alt
     "BILLY Height extension unit Width: 80 cm Depth: 28 cm Height: 35 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct3", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct3",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Height extension unit \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct3", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$30\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/80279791/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct4",
     :src "/PIAimages/0252265_PE391057_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 40 cm Depth: 28 cm Height: 106 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct4", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct4",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct4", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$49\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/00263831/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct5",
     :src "/PIAimages/0252260_PE391060_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 40 cm Depth: 28 cm Height: 106 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct5", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct5",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct5", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$49\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/60263833/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct6",
     :src "/PIAimages/0252255_PE391058_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 40 cm Depth: 28 cm Height: 106 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct6", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct6",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct6", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$39\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/00279785/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct7",
     :src "/PIAimages/0252339_PE391166_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 40 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct7", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct7",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct7", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$69\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/70263837/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct8",
     :src "/PIAimages/0252338_PE391165_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 40 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct8", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct8",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct8", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$69\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/30263839/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct9",
     :src "/PIAimages/0252341_PE391168_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 40 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct9", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct9",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct9", :class "prodPrice", :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$59\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/20279789/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct10",
     :src "/PIAimages/0252362_PE391155_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 80 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct10", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct10",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct10",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$95\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/20263849/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct11",
     :src "/PIAimages/0252347_PE391171_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 80 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct11", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct11",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct11",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$95\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/80263851/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct12",
     :src "/PIAimages/0252367_PE391149_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 80 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct12", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct12",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct12",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$69\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/40279806/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct13",
     :src "/PIAimages/0252430_PE391215_S2.JPG",
     :border "0",
     :alt
     "BILLY Extra shelf Width: 76 cm Depth: 26 cm Thickness: 2 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct13", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct13",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Extra shelf \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct13",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$10\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/80265299/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct14",
     :src "/PIAimages/0252423_PE391213_S2.JPG",
     :border "0",
     :alt
     "BILLY Extra shelf Width: 76 cm Depth: 26 cm Thickness: 2 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct14", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct14",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Extra shelf \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct14",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$10\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/90286753/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct15",
     :src "/PIAimages/20466_PE105614_S2.JPG",
     :border "0",
     :alt
     "BILLY Extra shelf Width: 76 cm Depth: 26 cm Max. load/shelf: 10 kg",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct15", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct15",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Extra shelf \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct15",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$10\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/00265302/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct16",
     :src "/PIAimages/0252420_PE391212_S2.JPG",
     :border "0",
     :alt
     "BILLY Extra shelf Width: 76 cm Depth: 26 cm Thickness: 2 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct16", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct16",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Extra shelf \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct16",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$10\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/S49020501/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct17",
     :src "/PIAimages/0255285_PE399413_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 160 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct17", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct17",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct17",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$138\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/S19020550/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct18",
     :src "/PIAimages/0255321_PE399434_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 160 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct18", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct18",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct18",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$190\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/S79023414/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct19",
     :src "/PIAimages/0255409_PE399478_S2.JPG",
     :border "0",
     :alt "BILLY Bookcase Width: 160 cm Depth: 28 cm Height: 202 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct19", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct19",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct19",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$190\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/S79020496/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct20",
     :src "/PIAimages/0255304_PE399419_S2.JPG",
     :border "0",
     :alt
     "BILLY Bookcase Depth: 28 cm Width right: 215 cm Width left: 135 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct20", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct20",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct20",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$386\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/S19020545/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct21",
     :src "/PIAimages/0255343_PE399441_S2.JPG",
     :border "0",
     :alt
     "BILLY Bookcase Depth: 28 cm Width right: 215 cm Width left: 135 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct21", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct21",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct21",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$509\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/S59023410/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct22",
     :src "/PIAimages/0255416_PE399485_S2.JPG",
     :border "0",
     :alt
     "BILLY Bookcase Depth: 28 cm Width right: 215 cm Width left: 135 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct22", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct22",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Bookcase \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct22",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$509\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/10279817/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct23",
     :src "/PIAimages/0252372_PE391182_S2.JPG",
     :border "0",
     :alt
     "BILLY Height extension unit Width: 40 cm Depth: 28 cm Height: 35 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct23", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct23",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Height extension unit \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct23",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$25\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/70263861/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct24",
     :src "/PIAimages/0252373_PE391184_S2.JPG",
     :border "0",
     :alt
     "BILLY Height extension unit Width: 40 cm Depth: 28 cm Height: 35 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct24", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct24",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Height extension unit \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct24",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$25\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}
 {:type :element,
  :attrs
  {:href "/au/en/catalog/products/10263859/",
   :onclick "irwStatTopProductClicked();"},
  :tag :a,
  :content
  ["\r\n\t\t\t\r\n\t\t"
   {:type :element,
    :attrs
    {:id "imgThmbProduct25",
     :src "/PIAimages/0306654_PE427146_S2.JPG",
     :border "0",
     :alt
     "BILLY Height extension unit Width: 40 cm Depth: 28 cm Height: 35 cm",
     :class "prodImg"},
    :tag :img,
    :content nil}
   "\r\n\t\t\r\n\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "txtNameProduct25", :class "prodName prodNameTro"},
    :tag :span,
    :content ["BILLY"]}
   "\r\n\t\t"
   {:type :element,
    :attrs nil,
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs
      {:id "txtDescrProduct25",
       :style "display:inline;",
       :class "prodDesc"},
      :tag :span,
      :content ["Height extension unit \r\n\t\t\t\t\r\n\t\t\t"]}
     "\r\n\t\t\t\r\n\t\t"]}
   "\r\n\t\t\r\n\t\t\t"
   {:type :element,
    :attrs
    {:id "txtPriceProduct25",
     :class "prodPrice",
     :style "clear:both;"},
    :tag :span,
    :content ["\r\n\t\t\t\t$20\t\t\t\t\r\n\t\t\t"]}
   "\r\n\t\t\t\t\r\n\t\t"
   {:type :element,
    :attrs {:id "comparisonContainer", :style "display: none"},
    :tag :div,
    :content
    ["\r\n\t\t\t"
     {:type :element,
      :attrs nil,
      :tag :span,
      :content ["\r\n\t\t\t\tUnit price\r\n\t\t\t"]}
     "\r\n\t\t\t"
     {:type :element,
      :attrs {:class "packagepricevaluebold", :id "comparisonPrice"},
      :tag :span,
      :content nil}
     "\r\n\t\t"]}
   "\r\n\t\t \r\n\t\t"]}])
