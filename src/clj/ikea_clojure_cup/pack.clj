(ns ikea-clojure-cup.pack
  (:require [org.httpkit.client :as http-kit]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as cs]
            [clojure.data.json :as json]))

;; (comment
  (def sample-param
    {:bins [{:id "car" :depth 5 :width 5 :height 5}]
     :packages [{:id "blah" :length 10 :width 10 :height 10}
                {:id "22" :length 2 :width 2 :height 2}]})
;;   )

(defn bin->string [{:keys [id depth width height]}]
  (format "%s:0:%sx%sx%s" id depth width height))

(defn package->string [{:keys [id length width height]}]
  (format "%s:0:0:%sx%sx%s" id length width height))

(defn get-missing [requested-packages packing-details]
  (let [all (set (map #(-> % :id str) requested-packages))
        packed (set (map :id (-> packing-details first :items)))
        missing (clojure.set/difference all packed)
        id-package-map (reduce (fn [m {:keys [id] :as p}]
                                 (assoc m (str id) p))
                               {}
                               requested-packages)]
    (vals (filter (fn [[k v]] (missing k)) id-package-map))))

(defn pack [{:keys [bins products]}]
  (let [bins (cs/join "," (map bin->string bins))
        packages (flatten (reduce (fn [v {:keys [packages id name]}]
                                    (conj v (map #(assoc % :id (count v) :name name) packages)))
                                  []
                                  products))
        items (when (seq packages) (cs/join "," (map package->string packages)))
        url (format "http://www.packit4me.com/api/call/raw?bins=%s&items=%s" bins (or items "0:0:0:0x0x0"))
        response (http-kit/post url)
        packing-details (json/read-str (:body @response) :key-fn keyword)
        requested-items-count (count packages)
        items-count (-> packing-details first :items count)
        result (cond
                (= items-count 0) :no
                (= items-count requested-items-count) :yes
                (not= items-count requested-items-count) :partial)]
    (merge {:result result
            :details packing-details}
           (when (= :partial result)
             {:missing (get-missing packages packing-details)})
           (when-not (= :no result)
             {:preview {:bins bins :items items}}))))

(defn transform-preview [body]
  (-> body
      (cs/replace #"<button(.*)</button>" "")
      (cs/replace #"</head>" "<link href=\"/css/ikea-helper.css\" rel=\"stylesheet\" type=\"text/css\"></head>")
      (cs/replace #"style=\"position: absolute; top: \d+px; z-index: 100\"" "")
      (cs/replace #"font-weight: bold;" "")
      (cs/replace #"scene.fog.color, 1" "0x000000, 0")
      (cs/replace #"antialias: false" "antialias: false, alpha: true")
      (cs/replace #"legend.style.color = \"#ffffff\"" "legend.style.color =\"#000\"")
      (cs/replace #"createLegend\(container\);" "")))

(defn preview [bins items]
  (let [response (http-kit/post (format "http://www.packit4me.com/api/call/preview?bins=%s&items=%s&binId=0" bins (if (seq items) items "0:0:0:0x0x0")))]
    (transform-preview (:body @response))))


;; (pack {:bins [{:id "car" :depth 2 :width 2 :height 2}]
;;      :packages [{:id "1111" :width 2 :length 2 :height 2}
;;                 {:id "2222" :width 2 :length 2 :height 2}]})

(comment
  (pack sample-param)
  (def res-partial [{:curr_weight 0, :weight_limit 0, :item_count 1, :size_2 5, :size "5 x 5 x 5", :size_1 5, :id "0", :size_3 5, :items [{:constraints 0, :y_origin_in_bin -1.5, :x_origin_in_bin -1.5, :sp_size_3 2, :size_2 2, :sp_size_2 2, :sp_size "2 x 2 x 2", :weight 0, :size_1 2, :orig_size "2 x 2 x 2", :id "22", :size_3 2, :z_origin_in_bin 1.5, :sp_size_1 2}]}])

  (def res-yes [{:curr_weight 0, :weight_limit 0, :item_count 1, :size_2 5, :size "5 x 5 x 5", :size_1 5, :id "0", :size_3 5, :items [{:constraints 0, :y_origin_in_bin -1.5, :x_origin_in_bin -1.5, :sp_size_3 2, :size_2 2, :sp_size_2 2, :sp_size "2 x 2 x 2", :weight 0, :size_1 2, :orig_size "2 x 2 x 2", :id "22", :size_3 2, :z_origin_in_bin 1.5, :sp_size_1 2}]}])

  (def resp-preview (let [response (http-kit/post "http://www.packit4me.com/api/call/preview?bins=0:50:5x5x5&items=0:0:15:1x1x1&binId=0")]
    @response))

  (def resp-yes (let [response (http-kit/post "http://www.packit4me.com/api/call/raw?bins=0:0:5x5x5&items=0:0:0:1x1x1,1:0:0:2x2x2")]
    @response))

  (def resp-no (let [response (http-kit/post "http://www.packit4me.com/api/call/raw?bins=0:0:5x5x5&items=0:0:0:10x10x10")]
    @response)))
