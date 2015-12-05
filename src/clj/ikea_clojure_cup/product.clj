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
                         :packages (-> (filter-fn (first (s/select (s/class "measure-quantity") m)))
                                       first
                                       (cs/split #" ")
                                       first
                                       Integer/parseInt)
                         :dimension-unit (-> units first last)
                         :weight-unit (-> units last last)})))
            []
            table-rows)))

(defn- filter-string-contains [v string]
  (filter #(.contains % string) v))

(defn- dimension->int [coll]
  (Integer/parseInt (nth (cs/split (first coll) #"\s") 2)))

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

(comment
(def single
  {:type :document,
   :content
   [{:type :document-type,
     :attrs {:name "html", :publicid "", :systemid ""}}
    {:type :element,
     :attrs nil,
     :tag :html,
     :content
     [{:type :element,
       :attrs nil,
       :tag :head,
       :content
       ["\r\n"
        {:type :comment, :content [" config "]}
        "\r\n"
        {:type :element,
         :attrs {:charset "utf-8"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:name "viewport",
          :id "viewport",
          :content
          "width=device-width,minimum-scale=1.0,maximum-scale=1.0,initial-scale=1.0"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs {:name "format-detection", :content "telephone=no"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:name "title",
          :content "BILLY - Package measurement and weight - IKEA"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs {:name "ROBOTS", :content "NOINDEX, FOLLOW"},
         :tag :meta,
         :content nil}
        "\n"
        {:type :comment, :content [" libraries "]}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "stylesheet",
          :href "/irmw-resources/external/jquery.mobile-1.2.0.min.css"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:type "text/javascript",
          :src "/irmw-resources/external/jquery-1.8.2.min.js"},
         :tag :script,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:type "text/javascript",
          :src
          "/irmw-resources/external/jquery.mobile.custom-1.2.0.min.js"},
         :tag :script,
         :content nil}
        "\r\n"
        {:type :comment, :content [" custom stylesheets and scripts "]}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "stylesheet",
          :href "/irmw-resources/css/ikea.mobile.min.css?v=5.20"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:type "text/javascript",
          :charset "utf-8",
          :src "/irmw-resources/js/ikea.mobile.min.js?v=5.20"},
         :tag :script,
         :content nil}
        "\r\n"
        {:type :comment, :content [" page specifc scripts "]}
        "\r\n"
        {:type :element,
         :attrs nil,
         :tag :script,
         :content
         ["\r\n(function($){\r\nfunction loadSettings(){\r\n//Initialize dynamic configuration.\r\n$.mobile.retailUnit = 'au';\r\n$.mobile.languageCode = 'en';\r\n$.mobile.loadingMessage = \"Loading\";\r\n$.mobile.defaultShoppingBagName = \"Shopping list\";\r\n$.mobile.offlineLoadError = \"The page you tried to go to can\\'t be shown right now. You can still view your shopping list.\";\r\n$.mobile.loggedInAsMessage =\"Logged in as {1}\";\r\n$.mobile.currencySettings = {symbol: \"$\", decimalSymbol: \".\", positiveFormat: \"%s%n\", negativeFormat: \"-%s%n\", groupSymbol: \",\", fractionDigits: \"2\", showFractionDigitsOnInteger: \"false\", integerSymbol: \"\", currencyCode: \"AUD\"};\r\n$.mobile.showLocalPrices = false;\r\n$.mobile.baseUrl = '/au/en/';\r\n$.mobile.isProduction = true;\r\n$.mobile.cookieDomain = '.ikea.com';\r\n$.mobile.fullSiteUrl = 'www.ikea.com';\r\n$.mobile.secureFullSiteUrl = 'secure.ikea.com';\r\n$.mobile.enableTealium =false;\r\n}\r\nloadSettings();\r\n}(jQuery));\r\n"]}
        "\r\n"
        {:type :element,
         :attrs {:id "txtHtmlTitle"},
         :tag :title,
         :content ["BILLY - Package measurement and weight - IKEA"]}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "shortcut icon",
          :type "image/ico",
          :href "/irmw-resources/img/favicon.ico"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :href
          "/irmw-resources/img/apple-touch-icon-57x57-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "114x114",
          :href
          "/irmw-resources/img/apple-touch-icon-114x114-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "144x144",
          :href
          "/irmw-resources/img/apple-touch-icon-144x144-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "32x32",
          :href
          "/irmw-resources/img/apple-touch-icon-32x32-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "57x57",
          :href
          "/irmw-resources/img/apple-touch-icon-57x57-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "72x72",
          :href
          "/irmw-resources/img/apple-touch-icon-72x72-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :comment,
         :content [" For Windows Phone (only 144x144 used)"]}
        "\r\n"
        {:type :element,
         :attrs
         {:name "msapplication-TileImage",
          :content
          "/irmw-resources/img/apple-touch-icon-144x144-precomposed.png"},
         :tag :meta,
         :content nil}
        "\r\n"]}
      "\r\n"
      {:type :element,
       :attrs {:itemscope "", :itemtype "http://schema.org/WebPage"},
       :tag :body,
       :content
       ["\r\n"
        {:type :element,
         :attrs
         {:data-role "page",
          :data-theme "d",
          :id "mls1449292019259",
          :data-dom-cache "false"},
         :tag :div,
         :content
         ["\r\n"
          {:type :element,
           :attrs
           {:data-role "header",
            :id "ikea-homeheader",
            :data-backbtn "false"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:id "ikea-home-topbar"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:id "ikea-topbar-logocell"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:tabindex "-1", :href "/au/en/", :rel "external"},
                 :tag :a,
                 :content
                 [{:type :element,
                   :attrs {:class "ikea-logo", :title ""},
                   :tag :span,
                   :content nil}]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs {:id "ikea-topbar-iconcell"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:id "search-icon",
                  :onclick
                  "$(this).parent().siblings('#ikea-topbar-searchfield').first().searchdecorate('toggle');"},
                 :tag :div,
                 :content nil}
                "\r\n"
                {:type :element,
                 :attrs {:id "list-icon"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs
                   {:href "http://m.ikea.com/au/en/shoppinglist/",
                    :rel "external"},
                   :tag :a,
                   :content nil}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "list-icon"},
                   :tag :div,
                   :content nil}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs
               {:data-role "searchdecorate",
                :id "ikea-topbar-searchfield"},
               :tag :div,
               :content nil}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs
             {:id "ikea-navbar",
              :data-role "headerbar",
              :data-morelistid "#ikea-more-list"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs nil,
               :tag :ul,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:class "ikea-navbar-item ikea-navbar-item-active",
                  :data-icon "none"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs
                   {:href "http://m.ikea.com/au/en/catalog/functional/"},
                   :tag :a,
                   :content ["Products"]}
                  "\r\n"]}
                "\r\n"
                {:type :element,
                 :attrs {:class "ikea-navbar-item ", :data-icon "none"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:href "http://m.ikea.com/au/en/stores/"},
                   :tag :a,
                   :content ["Stores"]}
                  "\r\n"]}
                "\r\n"
                {:type :element,
                 :attrs {:class "ikea-navbar-item", :id "ikea-nav-li"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:id "footer-login", :style "display: none"},
                   :tag :div,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs
                     {:id "login1",
                      :href "/au/en/login/",
                      :rel "nofollow",
                      :data-ajax "false"},
                     :tag :a,
                     :content ["Log in"]}
                    "\r\n"]}
                  "\r\n"
                  {:type :element,
                   :attrs {:id "footer-logout", :style "display: none"},
                   :tag :div,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs {:id "logged-in-as"},
                     :tag :span,
                     :content nil}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "header-logout"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:style "cursor:pointer;",
                        :onclick "Login.logout();return false;",
                        :data-ajax "false",
                        :rel "nofollow"},
                       :tag :a,
                       :content ["Log out"]}
                      "\r\n"]}
                    "\r\n"]}
                  "\r\n"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:class "ui-headerbar-more-item",
                  :data-role "headerbar-more-item"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:data-role "popupbutton", :id "buttonMenu"},
                   :tag :a,
                   :content
                   [{:type :element,
                     :attrs {:class "ikea-navbar-more-image"},
                     :tag :div,
                     :content nil}]}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-role "selectlist", :id "more-item-popup"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs
               {:data-role "listview",
                :data-theme "d",
                :id "ikea-more-list"},
               :tag :ul,
               :content ["\r\n"]}
              "\r\n"]}
            "\r\n"]}
          "\r\n"
          {:type :comment, :content [" page content"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-breadcrumbs", :itemprop "breadcrumb"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:onclick "history.back();", :href "#"},
             :tag :a,
             :content ["Back"]}
            {:type :element,
             :attrs {:class "ikea-breadcrumb-divider-slash"},
             :tag :span,
             :content [" / "]}
            "Package measurement ...\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:data-role "content"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:data-role "listview",
              :data-inset "true",
              :data-theme "d"},
             :tag :ul,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "productRow"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:class "ikea-product-list-wrapper"},
                 :tag :div,
                 :content
                 ["\n"
                  {:type :element,
                   :attrs {:class "ikea-product-pricetag-imageCell"},
                   :tag :div,
                   :content
                   ["\n"
                    {:type :element,
                     :attrs
                     {:class "ikea-product-img",
                      :align "left",
                      :src
                      "http://www.ikea.com/PIAimages/0252390_PE391192_S2.JPG",
                      :alt "BILLY, Height extension unit, black-brown"},
                     :tag :img,
                     :content nil}
                    "\n"]}
                  "\n"
                  {:type :element,
                   :attrs {:class "ikea-product-pricetag-cell"},
                   :tag :div,
                   :content
                   ["\n"
                    {:type :element,
                     :attrs {:class "ikea-product-tag"},
                     :tag :div,
                     :content
                     ["\n"
                      {:type :element,
                       :attrs
                       {:class "ikea-product-pricetag-name",
                        :itemprop "itemListElement"},
                       :tag :div,
                       :content ["BILLY"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Height extension unit,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["80x28x35 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$40"]}]}
                          "\n"]}]}]}
                    "\n"
                    {:type :element,
                     :attrs {:class "ikeaproduct-package-count"},
                     :tag :span,
                     :content ["\n1 packages in total\n"]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\r\n"
              {:type :element,
               :attrs {:class "product-measurements"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:class "ikea-measurements"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs nil,
                   :tag :h2,
                   :content ["BILLY"]}
                  "\r\n"
                  {:type :element,
                   :attrs nil,
                   :tag :p,
                   :content
                   ["\r\nHeight extension unit"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nArticle Number: 902.638.55"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\n"]}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "ikea-package-multiple-disclaimer"},
                   :tag :div,
                   :content [" Article consists of several packages"]}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "ikea-measurements-table-row"},
                   :tag :div,
                   :content
                   ["\r\nWidth:\r\n37 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nHeight:\r\n6 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nLength:\r\n78 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nWeight:\r\n7.84 kg\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "measure-quantity"},
                     :tag :div,
                     :content [" 1 packages "]}
                    "\r\n"]}]}]}
              "\r\n"]}]}
          "\r\n"
          "\r\n"]}
        "\r\n"
        {:type :comment, :content [" /page content"]}
        "\r\n"
        {:type :comment, :content [" common footer"]}
        "\r\n"
        {:type :element,
         :attrs {:class "ikea-footer"},
         :tag :div,
         :content
         ["\r\n"
          {:type :element,
           :attrs {:class "ikea-right-link-new"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:onclick "$.mobile.silentScroll(1);"},
             :tag :a,
             :content ["Go to top"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-footer-divider"},
           :tag :div,
           :content nil}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-footer-label"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-label-inner"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "linksRow"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :label,
                 :content ["Follow us on"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://www.facebook.com/ikea.au",
                  :target "_blank"},
                 :tag :a,
                 :content ["Facebook"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://www.pinterest.com/IKEA_Australia/",
                  :target "_blank"},
                 :tag :a,
                 :content ["Pinterest"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "http://everyday.ikea.com/",
                  :target "_blank"},
                 :tag :a,
                 :content ["Tumblr"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://instagram.com/ikea_australia",
                  :target "_blank"},
                 :tag :a,
                 :content ["Instagram"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "http://www.youtube.com/user/IKEAAUSTRALIA",
                  :target "_blank"},
                 :tag :a,
                 :content ["YouTube"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-label-inner"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "linksRow"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :label,
                 :content ["IKEA Catalogue App"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://itunes.apple.com/au/app/ikea-catalogue/id386592716?mt=8"},
                 :tag :a,
                 :content ["iOS"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://play.google.com/store/apps/details?id=com.ikea.catalogue.android"},
                 :tag :a,
                 :content ["Android"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-label-inner"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "linksRow"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :label,
                 :content ["IKEA Store App"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://itunes.apple.com/au/app/ikea-store/id976577934?mt=8",
                  :target "_blank"},
                 :tag :a,
                 :content ["iOS"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://play.google.com/store/apps/details?id=com.ikea.kompis&hl=en",
                  :target "_blank"},
                 :tag :a,
                 :content ["Android"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-full-site"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:href "http://www.ikea.com/au/en/?preferedui=desktop",
              :data-location "footer",
              :onclick "return waMLS.appendIRWURL(this)"},
             :tag :a,
             :content ["Full site"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-footer-copyright"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-legal"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "ikea-footer-label"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:class "ikea-footer-label-inner"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:class "linksRow"},
                   :tag :div,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href "http://www.ikea.com.au/privacy"},
                     :tag :a,
                     :content ["Privacy policy"]}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :span,
                     :content ["|"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href "http://www.ikea.com.au/cookies"},
                     :tag :a,
                     :content ["Cookies privacy"]}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :span,
                     :content ["|"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href
                      "http://www.ikea.com/au/en/about_ikea/newsroom/product_recalls"},
                     :tag :a,
                     :content ["Product recalls"]}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :span,
                     :content ["|"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href
                      "http://m.ikea.com/au/en/pages/customer_service/contact/"},
                     :tag :a,
                     :content ["Contact us"]}
                    "\r\n"]}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n© Inter IKEA Systems B.V. 1999-2015\r\n"]}
          "\r\n"]}
        "\r\n"
        "\r\n"
        {:type :element,
         :attrs nil,
         :tag :noscript,
         :content
         ["\r\n"
          {:type :element,
           :attrs {:style "display: none;"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:src
              "https://smetrics.ikea.com/b/ss/ikeaallmlsnojavascriptprod/5/?c8=au&v7=au&pageName=nojavascript",
              :alt "",
              :width "5",
              :height "5"},
             :tag :img,
             :content nil}
            "\r\n"]}
          "\r\n"]}
        "\r\n"
        "\r\n"]}]}]}
  )

(def multi

  {:type :document,
   :content
   [{:type :document-type,
     :attrs {:name "html", :publicid "", :systemid ""}}
    {:type :element,
     :attrs nil,
     :tag :html,
     :content
     [{:type :element,
       :attrs nil,
       :tag :head,
       :content
       ["\r\n"
        {:type :comment, :content [" config "]}
        "\r\n"
        {:type :element,
         :attrs {:charset "utf-8"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:name "viewport",
          :id "viewport",
          :content
          "width=device-width,minimum-scale=1.0,maximum-scale=1.0,initial-scale=1.0"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs {:name "format-detection", :content "telephone=no"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:name "title",
          :content "HEMNES - Package measurement and weight - IKEA"},
         :tag :meta,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs {:name "ROBOTS", :content "NOINDEX, FOLLOW"},
         :tag :meta,
         :content nil}
        "\n"
        {:type :comment, :content [" libraries "]}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "stylesheet",
          :href "/irmw-resources/external/jquery.mobile-1.2.0.min.css"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:type "text/javascript",
          :src "/irmw-resources/external/jquery-1.8.2.min.js"},
         :tag :script,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:type "text/javascript",
          :src
          "/irmw-resources/external/jquery.mobile.custom-1.2.0.min.js"},
         :tag :script,
         :content nil}
        "\r\n"
        {:type :comment, :content [" custom stylesheets and scripts "]}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "stylesheet",
          :href "/irmw-resources/css/ikea.mobile.min.css?v=5.20"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:type "text/javascript",
          :charset "utf-8",
          :src "/irmw-resources/js/ikea.mobile.min.js?v=5.20"},
         :tag :script,
         :content nil}
        "\r\n"
        {:type :comment, :content [" page specifc scripts "]}
        "\r\n"
        {:type :element,
         :attrs nil,
         :tag :script,
         :content
         ["\r\n(function($){\r\nfunction loadSettings(){\r\n//Initialize dynamic configuration.\r\n$.mobile.retailUnit = 'au';\r\n$.mobile.languageCode = 'en';\r\n$.mobile.loadingMessage = \"Loading\";\r\n$.mobile.defaultShoppingBagName = \"Shopping list\";\r\n$.mobile.offlineLoadError = \"The page you tried to go to can\\'t be shown right now. You can still view your shopping list.\";\r\n$.mobile.loggedInAsMessage =\"Logged in as {1}\";\r\n$.mobile.currencySettings = {symbol: \"$\", decimalSymbol: \".\", positiveFormat: \"%s%n\", negativeFormat: \"-%s%n\", groupSymbol: \",\", fractionDigits: \"2\", showFractionDigitsOnInteger: \"false\", integerSymbol: \"\", currencyCode: \"AUD\"};\r\n$.mobile.showLocalPrices = false;\r\n$.mobile.baseUrl = '/au/en/';\r\n$.mobile.isProduction = true;\r\n$.mobile.cookieDomain = '.ikea.com';\r\n$.mobile.fullSiteUrl = 'www.ikea.com';\r\n$.mobile.secureFullSiteUrl = 'secure.ikea.com';\r\n$.mobile.enableTealium =false;\r\n}\r\nloadSettings();\r\n}(jQuery));\r\n"]}
        "\r\n"
        {:type :element,
         :attrs {:id "txtHtmlTitle"},
         :tag :title,
         :content ["HEMNES - Package measurement and weight - IKEA"]}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "shortcut icon",
          :type "image/ico",
          :href "/irmw-resources/img/favicon.ico"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :href
          "/irmw-resources/img/apple-touch-icon-57x57-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "114x114",
          :href
          "/irmw-resources/img/apple-touch-icon-114x114-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "144x144",
          :href
          "/irmw-resources/img/apple-touch-icon-144x144-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "32x32",
          :href
          "/irmw-resources/img/apple-touch-icon-32x32-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "57x57",
          :href
          "/irmw-resources/img/apple-touch-icon-57x57-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :element,
         :attrs
         {:rel "apple-touch-icon-precomposed",
          :sizes "72x72",
          :href
          "/irmw-resources/img/apple-touch-icon-72x72-precomposed.png"},
         :tag :link,
         :content nil}
        "\r\n"
        {:type :comment,
         :content [" For Windows Phone (only 144x144 used)"]}
        "\r\n"
        {:type :element,
         :attrs
         {:name "msapplication-TileImage",
          :content
          "/irmw-resources/img/apple-touch-icon-144x144-precomposed.png"},
         :tag :meta,
         :content nil}
        "\r\n"]}
      "\r\n"
      {:type :element,
       :attrs {:itemscope "", :itemtype "http://schema.org/WebPage"},
       :tag :body,
       :content
       ["\r\n"
        {:type :element,
         :attrs
         {:data-role "page",
          :data-theme "d",
          :id "mls1449292401974",
          :data-dom-cache "false"},
         :tag :div,
         :content
         ["\r\n"
          {:type :element,
           :attrs
           {:data-role "header",
            :id "ikea-homeheader",
            :data-backbtn "false"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:id "ikea-home-topbar"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:id "ikea-topbar-logocell"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:tabindex "-1", :href "/au/en/", :rel "external"},
                 :tag :a,
                 :content
                 [{:type :element,
                   :attrs {:class "ikea-logo", :title ""},
                   :tag :span,
                   :content nil}]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs {:id "ikea-topbar-iconcell"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:id "search-icon",
                  :onclick
                  "$(this).parent().siblings('#ikea-topbar-searchfield').first().searchdecorate('toggle');"},
                 :tag :div,
                 :content nil}
                "\r\n"
                {:type :element,
                 :attrs {:id "list-icon"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs
                   {:href "http://m.ikea.com/au/en/shoppinglist/",
                    :rel "external"},
                   :tag :a,
                   :content nil}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "list-icon"},
                   :tag :div,
                   :content nil}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs
               {:data-role "searchdecorate",
                :id "ikea-topbar-searchfield"},
               :tag :div,
               :content nil}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs
             {:id "ikea-navbar",
              :data-role "headerbar",
              :data-morelistid "#ikea-more-list"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs nil,
               :tag :ul,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:class "ikea-navbar-item ikea-navbar-item-active",
                  :data-icon "none"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs
                   {:href "http://m.ikea.com/au/en/catalog/functional/"},
                   :tag :a,
                   :content ["Products"]}
                  "\r\n"]}
                "\r\n"
                {:type :element,
                 :attrs {:class "ikea-navbar-item ", :data-icon "none"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:href "http://m.ikea.com/au/en/stores/"},
                   :tag :a,
                   :content ["Stores"]}
                  "\r\n"]}
                "\r\n"
                {:type :element,
                 :attrs {:class "ikea-navbar-item", :id "ikea-nav-li"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:id "footer-login", :style "display: none"},
                   :tag :div,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs
                     {:id "login1",
                      :href "/au/en/login/",
                      :rel "nofollow",
                      :data-ajax "false"},
                     :tag :a,
                     :content ["Log in"]}
                    "\r\n"]}
                  "\r\n"
                  {:type :element,
                   :attrs {:id "footer-logout", :style "display: none"},
                   :tag :div,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs {:id "logged-in-as"},
                     :tag :span,
                     :content nil}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "header-logout"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:style "cursor:pointer;",
                        :onclick "Login.logout();return false;",
                        :data-ajax "false",
                        :rel "nofollow"},
                       :tag :a,
                       :content ["Log out"]}
                      "\r\n"]}
                    "\r\n"]}
                  "\r\n"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:class "ui-headerbar-more-item",
                  :data-role "headerbar-more-item"},
                 :tag :li,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:data-role "popupbutton", :id "buttonMenu"},
                   :tag :a,
                   :content
                   [{:type :element,
                     :attrs {:class "ikea-navbar-more-image"},
                     :tag :div,
                     :content nil}]}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-role "selectlist", :id "more-item-popup"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs
               {:data-role "listview",
                :data-theme "d",
                :id "ikea-more-list"},
               :tag :ul,
               :content ["\r\n"]}
              "\r\n"]}
            "\r\n"]}
          "\r\n"
          {:type :comment, :content [" page content"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-breadcrumbs", :itemprop "breadcrumb"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:onclick "history.back();", :href "#"},
             :tag :a,
             :content ["Back"]}
            {:type :element,
             :attrs {:class "ikea-breadcrumb-divider-slash"},
             :tag :span,
             :content [" / "]}
            "Package measurement ...\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:data-role "content"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:data-role "listview",
              :data-inset "true",
              :data-theme "d"},
             :tag :ul,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "productRow"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:class "ikea-product-list-wrapper"},
                 :tag :div,
                 :content
                 ["\n"
                  {:type :element,
                   :attrs {:class "ikea-product-pricetag-imageCell"},
                   :tag :div,
                   :content
                   ["\n"
                    {:type :element,
                     :attrs
                     {:class "ikea-product-img",
                      :align "left",
                      :src
                      "http://www.ikea.com/PIAimages/0137908_PE296720_S2.JPG",
                      :alt
                      "HEMNES, Cabinet with panel/glass-door, black-brown"},
                     :tag :img,
                     :content nil}
                    "\n"]}
                  "\n"
                  {:type :element,
                   :attrs {:class "ikea-product-pricetag-cell"},
                   :tag :div,
                   :content
                   ["\n"
                    {:type :element,
                     :attrs {:class "ikea-product-tag"},
                     :tag :div,
                     :content
                     ["\n"
                      {:type :element,
                       :attrs
                       {:class "ikea-product-pricetag-name",
                        :itemprop "itemListElement"},
                       :tag :div,
                       :content ["HEMNES"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Cabinet with panel/glass-door,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["90x197 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$399"]}]}
                          "\n"]}]}]}
                    "\n"
                    {:type :element,
                     :attrs {:class "ikeaproduct-package-count"},
                     :tag :span,
                     :content ["\n2 packages in total\n"]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\r\n"
              {:type :element,
               :attrs {:class "product-measurements"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:class "ikea-measurements"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs nil,
                   :tag :h2,
                   :content ["HEMNES"]}
                  "\r\n"
                  {:type :element,
                   :attrs nil,
                   :tag :p,
                   :content
                   ["\r\nCabinet with panel/glass-door"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nArticle Number: 202.271.25"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\n"]}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "ikea-package-multiple-disclaimer"},
                   :tag :div,
                   :content [" Article consists of several packages"]}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "ikea-measurements-table-row"},
                   :tag :div,
                   :content
                   ["\r\nWidth:\r\n38 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nHeight:\r\n14 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nLength:\r\n100 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nWeight:\r\n20.00 kg\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "measure-quantity"},
                     :tag :div,
                     :content [" 1 packages "]}
                    " "]}
                  "\r\n"
                  {:type :element,
                   :attrs {:class "ikea-measurements-table-row-child"},
                   :tag :div,
                   :content
                   ["\r\nWidth:\r\n45 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nHeight:\r\n9 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nLength:\r\n199 cm\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\nWeight:\r\n23.00 kg\r\n"
                    {:type :element, :attrs nil, :tag :br, :content nil}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "measure-quantity"},
                     :tag :div,
                     :content [" 1 packages "]}
                    "\r\n"]}]}]}
              "\r\n"]}]}
          "\r\n"
          "\r\n"]}
        "\r\n"
        {:type :comment, :content [" /page content"]}
        "\r\n"
        {:type :comment, :content [" common footer"]}
        "\r\n"
        {:type :element,
         :attrs {:class "ikea-footer"},
         :tag :div,
         :content
         ["\r\n"
          {:type :element,
           :attrs {:class "ikea-right-link-new"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:onclick "$.mobile.silentScroll(1);"},
             :tag :a,
             :content ["Go to top"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-footer-divider"},
           :tag :div,
           :content nil}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-footer-label"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-label-inner"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "linksRow"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :label,
                 :content ["Follow us on"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://www.facebook.com/ikea.au",
                  :target "_blank"},
                 :tag :a,
                 :content ["Facebook"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://www.pinterest.com/IKEA_Australia/",
                  :target "_blank"},
                 :tag :a,
                 :content ["Pinterest"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "http://everyday.ikea.com/",
                  :target "_blank"},
                 :tag :a,
                 :content ["Tumblr"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://instagram.com/ikea_australia",
                  :target "_blank"},
                 :tag :a,
                 :content ["Instagram"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "http://www.youtube.com/user/IKEAAUSTRALIA",
                  :target "_blank"},
                 :tag :a,
                 :content ["YouTube"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-label-inner"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "linksRow"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :label,
                 :content ["IKEA Catalogue App"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://itunes.apple.com/au/app/ikea-catalogue/id386592716?mt=8"},
                 :tag :a,
                 :content ["iOS"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://play.google.com/store/apps/details?id=com.ikea.catalogue.android"},
                 :tag :a,
                 :content ["Android"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-label-inner"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "linksRow"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :label,
                 :content ["IKEA Store App"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://itunes.apple.com/au/app/ikea-store/id976577934?mt=8",
                  :target "_blank"},
                 :tag :a,
                 :content ["iOS"]}
                "\r\n"
                {:type :element, :attrs nil, :tag :span, :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href
                  "https://play.google.com/store/apps/details?id=com.ikea.kompis&hl=en",
                  :target "_blank"},
                 :tag :a,
                 :content ["Android"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-full-site"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:href "http://www.ikea.com/au/en/?preferedui=desktop",
              :data-location "footer",
              :onclick "return waMLS.appendIRWURL(this)"},
             :tag :a,
             :content ["Full site"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-footer-copyright"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:class "ikea-footer-legal"},
             :tag :div,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:class "ikea-footer-label"},
               :tag :div,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:class "ikea-footer-label-inner"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs {:class "linksRow"},
                   :tag :div,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href "http://www.ikea.com.au/privacy"},
                     :tag :a,
                     :content ["Privacy policy"]}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :span,
                     :content ["|"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href "http://www.ikea.com.au/cookies"},
                     :tag :a,
                     :content ["Cookies privacy"]}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :span,
                     :content ["|"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href
                      "http://www.ikea.com/au/en/about_ikea/newsroom/product_recalls"},
                     :tag :a,
                     :content ["Product recalls"]}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :span,
                     :content ["|"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-ajax "false",
                      :href
                      "http://m.ikea.com/au/en/pages/customer_service/contact/"},
                     :tag :a,
                     :content ["Contact us"]}
                    "\r\n"]}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"]}
            "\r\n© Inter IKEA Systems B.V. 1999-2015\r\n"]}
          "\r\n"]}
        "\r\n"
        "\r\n"
        {:type :element,
         :attrs nil,
         :tag :noscript,
         :content
         ["\r\n"
          {:type :element,
           :attrs {:style "display: none;"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:src
              "https://smetrics.ikea.com/b/ss/ikeaallmlsnojavascriptprod/5/?c8=au&v7=au&pageName=nojavascript",
              :alt "",
              :width "5",
              :height "5"},
             :tag :img,
             :content nil}
            "\r\n"]}
          "\r\n"]}
        "\r\n"
        "\r\n"]}]}]}
  ))

(def spr-sample )
