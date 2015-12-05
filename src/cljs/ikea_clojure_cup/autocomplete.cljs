(ns ikea-clojure-cup.autocomplete
  (:require [reagent.core :refer [atom]]
            [reagent-forms.core :refer [init-field value-of]]
            [cljs.core.async :as async]
            [clojure.string :refer [split trim]]
            [ikea-clojure-cup.bootstrap :as bootstrap])
  (:require-macros [reagent-forms.macros :refer [render-element]]
                   [cljs.core.async.macros :refer [go]]
                   [ikea-clojure-cup.client-macros :refer [debounce]]))

(defmethod init-field :autocomplete
  [[type {:keys [id data-source input-class list-class item-class highlight-class input-placeholder result-fn choice-fn clear-on-focus? addons]
          :as attrs
          :or {result-fn identity
               choice-fn identity
               clear-on-focus? true}}] {:keys [doc get save!]}]
  (let [typeahead-hidden? (atom true)
        mouse-on-list? (atom false)
        selected-index (atom -1)
        selections (atom [])
        loading? (atom false)
        choose-selected #(when (and (not-empty @selections) (> @selected-index -1))
                           (let [choice (nth @selections @selected-index)]
                             (save! id choice)
                             (choice-fn choice)
                             (reset! typeahead-hidden? true)))]
    (render-element attrs doc
                    [type
                     [:div.input-group
                      [:span.input-group-addon
                       (if @loading?
                         [:span.loading]
                         [:span.glyphicon.glyphicon-search])]
                      [:input {:type        :text
                               :placeholder input-placeholder
                               :class       input-class
                               :value       (let [v (get id)]
                                              (if-not (iterable? v)
                                                v (first v)))
                               :on-focus    #(when clear-on-focus? (save! id nil))
                               :on-blur     #(when-not @mouse-on-list?
                                               (reset! typeahead-hidden? true)
                                               (reset! selected-index -1))
                               :on-change   #(when-let [value (trim (value-of %))]
                                               (save! id (value-of %))
                                               (reset! typeahead-hidden? false)
                                               (reset! selected-index -1)
                                               (debounce 500
                                                         (let [ch (data-source (clojure.string/lower-case value))]
                                                           (reset! loading? true)
                                                           (go (reset! selections (<! ch))
                                                               (reset! loading? false)))))
                               :on-key-down #(do
                                               (case (.-which %)
                                                 38 (do
                                                      (.preventDefault %)
                                                      (when-not (= @selected-index 0)
                                                        (swap! selected-index dec)))
                                                 40 (do
                                                      (.preventDefault %)
                                                      (when-not (= @selected-index (dec (count @selections)))
                                                        (save! id (value-of %))
                                                        (swap! selected-index inc)))
                                                 9  (choose-selected)
                                                 13 (choose-selected)
                                                 27 (do (reset! typeahead-hidden? true)
                                                      (reset! selected-index 0))
                                                 "default"))}]
                      (when addons [addons])]

                     (when-not (or (empty? @selections) @typeahead-hidden?)
                       [bootstrap/pop-over {:id :search-results
                                            :placement :bottom
                                            :title "Search results"
                                            :container :body}
                        [:ul {:class list-class
                              :on-mouse-enter #(reset! mouse-on-list? true)
                              :on-mouse-leave #(reset! mouse-on-list? false)}
                         (doall
                          (map-indexed
                           (fn [index result]
                             [:li {:tab-index     index
                                   :key           index
                                   :class         (if (= @selected-index index) highlight-class item-class)
                                   :on-mouse-over #(do
                                                     (reset! selected-index (js/parseInt (.getAttribute (.-target %) "tabIndex"))))
                                   :on-click      #(do
                                                     (reset! typeahead-hidden? true)
                                                     (save! id result)
                                                     (choice-fn result))}
                              (result-fn result)])
                           @selections))]])])))
