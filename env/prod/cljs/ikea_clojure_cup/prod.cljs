(ns ikea-clojure-cup.prod
  (:require [ikea-clojure-cup.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
