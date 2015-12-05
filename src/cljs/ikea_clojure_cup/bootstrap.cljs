(ns ikea-clojure-cup.bootstrap
  (:require [reagent.core :as reagent]
            [cljsjs.react-bootstrap]))

(def modal (reagent/adapt-react-class js/ReactBootstrap.Modal))
(def modal-header (reagent/adapt-react-class js/ReactBootstrap.Modal.Header))
(def modal-title (reagent/adapt-react-class js/ReactBootstrap.Modal.Title))
(def modal-body (reagent/adapt-react-class js/ReactBootstrap.Modal.Body))
(def modal-footer (reagent/adapt-react-class js/ReactBootstrap.Modal.Footer))

(def input (reagent/adapt-react-class js/ReactBootstrap.Input))

(def button (reagent/adapt-react-class js/ReactBootstrap.Button))
