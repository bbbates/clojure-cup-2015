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
  (get-content m "ikea-product-pricetag-desc"))

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

(defn search [region query]
  (when-not (cs/blank? query)
    (let [url (format "%s/%s/en/search/?query=%s" ikea-domain region query)
          response (http-kit/get url)
          site-htree (-> @response :body hick/parse hick/as-hickory)
          hick-products (get-hick-products site-htree)]
      (transform-products hick-products))))

(comment
  (search "au" "billy"))

(def products-sample
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
       :attrs {:name "title", :content "Search results - IKEA"},
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
       :content ["Search results - IKEA"]}
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
        :id "mls1449289058557",
        :data-dom-cache "true"},
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
               :attrs {:class "ikea-navbar-item ", :data-icon "none"},
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
         :attrs nil,
         :tag :script,
         :content
         ["\r\n$('#mls1449289058557').bind('pageshow', function(){\r\n//events for filter/sorting popups\r\n$(\".price_filter\").click(function(e){\r\n$(\".ui-page-active\").find(\"#price_filter_popup\").popup(\"show\");\r\n});\r\n$(\".sorting\").click(function(e){\r\n$(\".ui-page-active\").find(\"#sorting_list_popup\").popup(\"show\");\r\n});\r\n$(\".color_filter\").click(function(e){\r\n$(\".ui-page-active\").find(\"#color_filter_popup\").popup(\"show\");\r\n});\r\n/*place price filter on filter/sorting list\r\nfunction placePriceFilter(){\r\nvar filterMinPrice = $(\"#filterMinPrice\", \".ui-page-active\").val();\r\nvar filterMaxPrice = $(\"#filterMaxPrice\", \".ui-page-active\").val();\r\n$(\"#price-filter-selected\", \".ui-page-active\").html(filterMinPrice + \" - \" + filterMaxPrice);\r\n}\r\nplacePriceFilter();*/\r\nwaMLS.initWebAnalyticsInternalSearch(\"billy\",\"33\");\r\n});\r\nvar minPrice = '2';\r\nvar maxPrice = '824';\r\n"]}
        "\r\n"
        {:type :element,
         :attrs {:class "selectListPopup"},
         :tag :div,
         :content
         ["\r\n"
          {:type :element,
           :attrs
           {:class "selectListPopupFieldset",
            :data-role "controlgroup"},
           :tag :fieldset,
           :content ["\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:data-role "popup-cancel-button"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:data-role "button", :data-theme "c"},
             :tag :div,
             :content ["Cancel"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "popup-clearFloats"},
           :tag :div,
           :content nil}
          "\r\n"]}
        "\r\n"
        {:type :element,
         :attrs {:data-role "content", :tabindex "-1"},
         :tag :div,
         :content
         ["\r\n"
          {:type :element,
           :attrs {:id "resultMatches"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:class "ui-search-matches"},
             :tag :div,
             :content
             ["\r\nSearch results for \"billy\", 33 matches\r\n"]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:name "searchform", :id "ikea-search-filter-form"},
           :tag :form,
           :content
           ["\r\n"
            {:type :element,
             :attrs
             {:class "ikea-search",
              :id "filter-list",
              :data-role "listview",
              :data-theme "d",
              :data-inset "true"},
             :tag :ul,
             :content
             ["\r\n"
              {:type :element,
               :attrs
               {:id "filters_list",
                :class "ikea-global-start-level-1 grey",
                :data-role "collapsiblelistitem",
                :data-content-id "filter-collapsible-content"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs {:data-icon "arrow-d"},
                 :tag :a,
                 :content ["Filter and sort"]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs
               {:class
                "color_filter ikea-global-start-level-2 filter-collapsible-content",
                :data-content "true",
                :data-role "button",
                :data-icon "arrow-r",
                :data-iconpos "right"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:class
                  "ikea-search-price-description ikea-font-normal"},
                 :tag :span,
                 :content ["Colour"]}
                "\r\n"
                {:type :element,
                 :attrs {:id "color-filter-selected"},
                 :tag :span,
                 :content ["All colours"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-role "popup",
                  :id "color_filter_popup",
                  :data-theme "a",
                  :class "colorFilter",
                  :data-icon "arrow-r"},
                 :tag :div,
                 :content
                 ["\r\n"
                  "\r\n"
                  {:type :element,
                   :attrs
                   {:type "hidden", :id "filterColor", :value ""},
                   :tag :input,
                   :content nil}
                  "\r\n"
                  {:type :element,
                   :attrs
                   {:data-role "controlgroup",
                    :id "sortType2",
                    :onchange
                    "javascript:Search.executeColorFilter(this);"},
                   :tag :fieldset,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs {:class "colorFilterContent"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs {:for "filterColor2"},
                       :tag :label,
                       :content ["All colours"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "filterColor2",
                        :name "filterColors",
                        :value "",
                        :checked "checked"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "color_black"},
                       :tag :label,
                       :content ["Black"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "color_black",
                        :name "filterColors",
                        :value "black"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "color_brown"},
                       :tag :label,
                       :content ["Brown"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "color_brown",
                        :name "filterColors",
                        :value "brown"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "color_green"},
                       :tag :label,
                       :content ["Green"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "color_green",
                        :name "filterColors",
                        :value "green"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "color_red"},
                       :tag :label,
                       :content ["Red"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "color_red",
                        :name "filterColors",
                        :value "red"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "color_white"},
                       :tag :label,
                       :content ["White"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "color_white",
                        :name "filterColors",
                        :value "white"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "color_other colours"},
                       :tag :label,
                       :content ["Other colours"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "color_other colours",
                        :name "filterColors",
                        :value "other colours"},
                       :tag :input,
                       :content nil}
                      "\r\n"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-role "popup-cancel-button",
                      :class "filter-popup-cancel-button"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:data-role "button",
                        :class "ikea-search-button",
                        :data-theme "c"},
                       :tag :div,
                       :content ["Cancel"]}
                      "\r\n"]}
                    "\r\n"]}
                  "\r\n"
                  "\r\n"]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs
               {:class
                "price_filter ikea-global-start-level-2 filter-collapsible-content",
                :data-content "true",
                :data-role "button",
                :data-icon "arrow-r",
                :data-iconpos "right"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:class
                  "ikea-search-price-description ikea-font-normal"},
                 :tag :span,
                 :content ["Price"]}
                "\r\n"
                {:type :element,
                 :attrs {:id "price-filter-selected"},
                 :tag :span,
                 :content ["2 - 824"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-role "popup",
                  :id "price_filter_popup",
                  :data-theme "a",
                  :class "pricePopup",
                  :data-icon "arrow-r"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs nil,
                   :tag :form,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs
                     {:class
                      "ikea-search-price-label ikea-font-normal1"},
                     :tag :div,
                     :content ["Price"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:class "ikea-search-filter-input-wrapper1"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:type "number",
                        :class "ikea-search-price-filter",
                        :id "filterMinPrice",
                        :maxlength "15",
                        :value "2",
                        :onkeypress
                        "Search.handleSearchKeyPressFilter(event)"},
                       :tag :input,
                       :content nil}
                      "\r\n"]}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "ikea-search-dash-cell"},
                     :tag :div,
                     :content ["\r\n-\r\n"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:class "ikea-search-filter-input-wrapper2"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:type "number",
                        :class "ikea-search-price-filter",
                        :id "filterMaxPrice",
                        :maxlength "15",
                        :value "824",
                        :onkeypress
                        "Search.handleSearchKeyPressFilter(event)"},
                       :tag :input,
                       :content nil}
                      "\r\n"]}
                    "\r\n"
                    {:type :element,
                     :attrs
                     {:data-role "popup-cancel-button",
                      :class "popup-cancel-button"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:data-role "button",
                        :class "ikea-search-button cancel",
                        :data-theme "c",
                        :onclick
                        "javascript:Search.restorePriceValues();"},
                       :tag :div,
                       :content ["\r\nCancel\r\n"]}
                      "\r\n"]}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "popup-filter-button"},
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs
                       {:class "ikea-search-button filter",
                        :onclick
                        "javascript:Search.executePriceFilter();",
                        :data-role "button",
                        :id "ikea-search-filter-button",
                        :data-theme "c"},
                       :tag :div,
                       :content ["\r\nRefine\r\n"]}
                      "\r\n"]}
                    "\r\n"
                    {:type :element,
                     :attrs {:class "clear"},
                     :tag :div,
                     :content nil}
                    "\r\n"
                    {:type :element,
                     :attrs nil,
                     :tag :div,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs {:class "ikea-search-cell"},
                       :tag :div,
                       :content
                       [{:type :element,
                         :attrs {:class "ikea-filter-input-message"},
                         :tag :span,
                         :content ["Invalid price range"]}]}
                      "\r\n"]}
                    "\r\n"]}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs
               {:class
                "sorting ikea-global-start-level-2 filter-collapsible-content",
                :data-content "true",
                :data-role "button",
                :data-icon "arrow-r",
                :data-iconpos "right"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:class
                  "ikea-search-price-description ikea-font-normal"},
                 :tag :span,
                 :content ["Sort by"]}
                "\r\n"
                {:type :element,
                 :attrs {:id "sorting-selected"},
                 :tag :span,
                 :content ["Relevance"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-role "popup",
                  :id "sorting_list_popup",
                  :data-theme "a",
                  :class "relevance2",
                  :data-icon "arrow-r"},
                 :tag :div,
                 :content
                 ["\r\n"
                  {:type :element,
                   :attrs nil,
                   :tag :form,
                   :content
                   ["\r\n"
                    {:type :element,
                     :attrs
                     {:data-role "controlgroup",
                      :id "sort",
                      :onchange "javascript:Search.executeSort();"},
                     :tag :fieldset,
                     :content
                     ["\r\n"
                      {:type :element,
                       :attrs {:for "relevance3"},
                       :tag :label,
                       :content ["Relevance"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "relevance3",
                        :value "relevance",
                        :name "filterInput",
                        :checked "checked"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "name"},
                       :tag :label,
                       :content ["Name"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "name",
                        :value "product_name",
                        :name "filterInput"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "price"},
                       :tag :label,
                       :content ["Price"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "price",
                        :value "price",
                        :name "filterInput"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs {:for "new"},
                       :tag :label,
                       :content ["Newest"]}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:type "radio",
                        :id "new",
                        :value "new",
                        :name "filterInput"},
                       :tag :input,
                       :content nil}
                      "\r\n"
                      {:type :element,
                       :attrs
                       {:data-role "popup-cancel-button",
                        :class "filter-popup-cancel-button"},
                       :tag :div,
                       :content
                       ["\r\n"
                        {:type :element,
                         :attrs
                         {:data-role "button",
                          :class "ikea-search-button",
                          :data-theme "c"},
                         :tag :div,
                         :content ["Cancel"]}
                        "\r\n"]}
                      "\r\n"]}
                    "\r\n"]}
                  "\r\n"]}
                "\r\n"]}
              "\r\n"
              {:type :element,
               :attrs
               {:class
                "ikea-search-clear-filters filter-collapsible-content",
                :data-content "true"},
               :tag :li,
               :content
               ["\r\n"
                {:type :element,
                 :attrs
                 {:id "clear-form",
                  :onclick "Search.clearSearch();",
                  :data-role "button",
                  :class "",
                  :data-theme "c"},
                 :tag :div,
                 :content ["Clear filter"]}
                "\r\n"
                {:type :element,
                 :attrs {:class "clearFloats"},
                 :tag :div,
                 :content nil}
                "\r\n"]}
              "\r\n"]}]}
          "\r\n"
          "\r\n"
          {:type :element,
           :attrs
           {:id "search-list",
            :data-role "listview",
            :data-theme "d",
            :data-inset "true"},
           :tag :ul,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/80279814/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252388_PE391190_S3.JPG",
                      :alt
                      "BILLY, Height extension unit, birch veneer"},
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
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["80x28x35 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$40"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/80279791/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252265_PE391057_S3.JPG",
                      :alt "BILLY, Bookcase, birch veneer"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["40x28x106 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$49"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/00279785/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252339_PE391166_S3.JPG",
                      :alt "BILLY, Bookcase, birch veneer"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["40x28x202 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$69"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/20279789/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252362_PE391155_S3.JPG",
                      :alt "BILLY, Bookcase, birch veneer"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["80x28x202 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$95"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/40279806/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252430_PE391215_S3.JPG",
                      :alt "BILLY, Extra shelf, birch veneer"},
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
                       ["Extra shelf,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["76x26 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$10"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/49020501/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255285_PE399413_S3.JPG",
                      :alt "BILLY, Bookcase, white"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["160x202x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$138"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/79020496/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255304_PE399419_S3.JPG",
                      :alt "BILLY, Bookcase, white"},
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
                       :tag :div,
                       :content ["Bookcase"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-price"},
                       :tag :div,
                       :content
                       [{:type :element,
                         :attrs {:class "inline"},
                         :tag :span,
                         :content ["$386"]}]}
                      "\n"
                      {:type :element,
                       :attrs
                       {:class "ikea-product-list-more-options"},
                       :tag :div,
                       :content ["Available in more options"]}
                      "\n"]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/10279817/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252372_PE391182_S3.JPG",
                      :alt
                      "BILLY, Height extension unit, birch veneer"},
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
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["40x28x35 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$25"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/60279787/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252328_PE391160_S3.JPG",
                      :alt "BILLY, Bookcase, birch veneer"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["80x28x106 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$59"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/20279794/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252409_PE391204_S3.JPG",
                      :alt "BILLY, Extra shelf, birch veneer"},
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
                       ["Extra shelf,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["36x26 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$10"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/99020546/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255330_PE399440_S3.JPG",
                      :alt "BILLY, Bookcase, black-brown"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["120x237x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$282"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/19020499/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255298_PE399418_S3.JPG",
                      :alt "BILLY, Bookcase, white"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["200x237x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$277"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/39020549/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255322_PE399435_S3.JPG",
                      :alt "BILLY, Bookcase, black-brown"},
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
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["240x106x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$177"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/60181955/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/56866_PE162285_S3.JPG",
                      :alt "BILLY, Corner fittings, galvanised"},
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
                       :tag :div,
                       :content ["Corner fittings"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-price"},
                       :tag :div,
                       :content
                       [{:type :element,
                         :attrs {:class "inline"},
                         :tag :span,
                         :content
                         ["$10"
                          {:type :element,
                           :attrs
                           {:class
                            "ikea-product-pricetag-unit-small inline"},
                           :tag :span,
                           :content ["/2 pack"]}]}]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-package"},
                       :tag :div,
                       :content
                       ["Price per piece "
                        {:type :element,
                         :attrs {:class "ikea-product-pricetag-unit"},
                         :tag :span,
                         :content
                         [{:type :element,
                           :attrs {:class "inline"},
                           :tag :span,
                           :content ["$5"]}
                          " "]}
                        "\n"]}
                      "\n"]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/99020551/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255324_PE399437_S3.JPG",
                      :alt "BILLY / GNEDBY, Bookcase, black-brown"},
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
                       :content ["BILLY / GNEDBY"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["200x202x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$288"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/89020504/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255448_PE399482_S3.JPG",
                      :alt "BILLY / MORLIDEN, Bookcase, white"},
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
                       :content ["BILLY / MORLIDEN"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["200x106x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$337"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/59020505/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255293_PE399415_S3.JPG",
                      :alt "BILLY / MORLIDEN, Bookcase, white"},
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
                       :content ["BILLY / MORLIDEN"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["80x202x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$189"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/19020507/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255306_PE399403_S3.JPG",
                      :alt "BILLY / OXBERG, Bookcase, white"},
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
                       :content ["BILLY / OXBERG"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["200x237x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$577"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/89023418/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255413_PE399462_S3.JPG",
                      :alt "BILLY / OXBERG, Bookcase, birch veneer"},
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
                       :content ["BILLY / OXBERG"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["80x202x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$255"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/19047738/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0300797_PE426434_S3.JPG",
                      :alt "BILLY / OXBERG, Bookcase, white"},
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
                       :content ["BILLY / OXBERG"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["160x202x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$338"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/spr/39020506/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0255305_PE399420_S3.JPG",
                      :alt "BILLY / OXBERG, Bookcase, white, glass"},
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
                       :content ["BILLY / OXBERG"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Bookcase,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["160x202x28 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$338"]}]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/70277146/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0251882_PE390717_S3.JPG",
                      :alt "GNEDBY, Shelving unit, birch veneer"},
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
                       :content ["GNEDBY"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Shelving unit,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["202 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$49"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/70279758/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252176_PE390877_S3.JPG",
                      :alt "MORLIDEN, Glass door, aluminium"},
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
                       :content ["MORLIDEN"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :div,
                       :content ["Glass door"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-price"},
                       :tag :div,
                       :content
                       [{:type :element,
                         :attrs {:class "inline"},
                         :tag :span,
                         :content ["$50"]}]}
                      "\n"
                      {:type :element,
                       :attrs
                       {:class "ikea-product-list-more-options"},
                       :tag :div,
                       :content ["Available in more options"]}
                      "\n"]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/60275609/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252184_PE390878_S3.JPG",
                      :alt "OXBERG, Door, birch veneer"},
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
                       :content ["OXBERG"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Door,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["40x97 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$50"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:data-icon "false", :class "productRow"},
             :tag :li,
             :content
             ["\r\n"
              {:type :element,
               :attrs {:href "/au/en/catalog/products/art/30275615/"},
               :tag :a,
               :content
               ["\n"
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
                      :src
                      "http://www.ikea.com/PIAimages/0252231_PE390953_S3.JPG",
                      :alt "OXBERG, Glass door, birch veneer"},
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
                       :content ["OXBERG"]}
                      "\n"
                      {:type :element,
                       :attrs {:class "ikea-product-pricetag-desc"},
                       :tag :span,
                       :content
                       ["Glass door,"
                        {:type :element,
                         :attrs nil,
                         :tag :span,
                         :content
                         ["\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-desc"},
                           :tag :span,
                           :content
                           [{:type :element,
                             :attrs nil,
                             :tag :nobr,
                             :content ["40x35 cm"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-pricetag-price"},
                           :tag :div,
                           :content
                           [{:type :element,
                             :attrs {:class "inline"},
                             :tag :span,
                             :content ["$35"]}]}
                          "\n"
                          {:type :element,
                           :attrs
                           {:class "ikea-product-list-more-options"},
                           :tag :div,
                           :content ["Available in more options"]}
                          "\n"]}]}]}
                    "\n"]}
                  "\n"]}
                "\n"]}
              "\n"]}
            "\r\n"
            {:type :element,
             :attrs {:class "morebutton", :data-role "listbutton"},
             :tag :li,
             :content
             [{:type :element,
               :attrs
               {:data-role "button",
                :data-theme "c",
                :onclick "Search.nextSearchPage()"},
               :tag :div,
               :content ["See more products (showing 25 of 33) "]}]}
            "\r\n"]}
          "\r\n"
          {:type :element,
           :attrs {:class "ikea-right-link"},
           :tag :div,
           :content
           ["\r\n"
            {:type :element,
             :attrs {:onclick "$.mobile.silentScroll(1);"},
             :tag :a,
             :content ["Back to top"]}
            "\r\n"]}
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
                {:type :element,
                 :attrs nil,
                 :tag :span,
                 :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://www.pinterest.com/IKEA_Australia/",
                  :target "_blank"},
                 :tag :a,
                 :content ["Pinterest"]}
                "\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :span,
                 :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "http://everyday.ikea.com/",
                  :target "_blank"},
                 :tag :a,
                 :content ["Tumblr"]}
                "\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :span,
                 :content ["|"]}
                "\r\n"
                {:type :element,
                 :attrs
                 {:data-ajax "false",
                  :href "https://instagram.com/ikea_australia",
                  :target "_blank"},
                 :tag :a,
                 :content ["Instagram"]}
                "\r\n"
                {:type :element,
                 :attrs nil,
                 :tag :span,
                 :content ["|"]}
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
                {:type :element,
                 :attrs nil,
                 :tag :span,
                 :content ["|"]}
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
                {:type :element,
                 :attrs nil,
                 :tag :span,
                 :content ["|"]}
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
        "\r\n"]}
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
      "\r\n"
      "\r\n"]}]}]}
)
