(ns ikea-clojure-cup.product
  (:require [org.httpkit.client :as http-kit]
            [clojure.pprint :refer [pprint]]
            [hickory.select :as s]
            [hickory.core :as hick]
            [clojure.string :as cs]))

(defn- get-hick-packages [site-htree]
  ;(s/or ..) doesn't work so have to use concat
  (let [table-rows (concat (s/select (s/class "ikea-measurements-table-row") site-htree)
                           (s/select (s/class "ikea-measurements-table-row-child") site-htree))
        filter-fn #(remove cs/blank? (map cs/trim (filter string? (:content %))))]
    (reduce (fn [v m]
              (let [d (filter-fn m)
                    units (map #(cs/split % #" ") d)]
                (conj v {:dimensions-and-weight (map #(-> (re-find #"\d+" %) Integer/parseInt) d)
                         :packages (let [p (first (filter-fn (first (s/select (s/class "measure-quantity") m))))]
                                     (when p
                                       (-> p
                                           (cs/split #" ")
                                           first
                                           Integer/parseInt)))
                         :dimension-unit (-> units first last)
                         :weight-unit (-> units last last)})))
            []
            table-rows)))

(defn- transform-packages [hick-packages]
  (reduce (fn [v {:keys [dimensions-and-weight packages weight-unit dimension-unit]}]
            (conj v {:width (nth dimensions-and-weight 0)
                     :height (nth dimensions-and-weight 1)
                     :length (nth dimensions-and-weight 2)
                     :weight (nth dimensions-and-weight 3)
                     :quantity packages
                     :weight-unit weight-unit
                     :dimension-unit dimension-unit}))
          []
          hick-packages))

(defn product [region lang product-context id]
  (let [url (format "http://m.ikea.com/%s/%s/catalog/products/%s/%s/measurements/" region lang product-context id)
        response (http-kit/get url)]
    (transform-packages (get-hick-packages (-> @response :body hick/parse hick/as-hickory))) ))

;; (count (get-hick-packages single))
;; (count (get-hick-packages multi))
;; (transform-packages (get-hick-packages multi))

(comment
  ;;multi
  (product "au" "en" "art" "20227125")
  ;;single
  (product "au" "en" "art" "90263855")
  ;spr
;;   (product "au" "en" "spr" "39111083")
  )

(def spr-sample )
