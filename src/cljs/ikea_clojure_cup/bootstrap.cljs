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

(def image (reagent/adapt-react-class js/ReactBootstrap.Image))
(def thumbnail (reagent/adapt-react-class js/ReactBootstrap.Thumbnail))

(def glyph (reagent/adapt-react-class js/ReactBootstrap.Glyphicon))

(def grid (reagent/adapt-react-class js/ReactBootstrap.Grid))
(def row (reagent/adapt-react-class js/ReactBootstrap.Row))
(def col (reagent/adapt-react-class js/ReactBootstrap.Col))

(def nav (reagent/adapt-react-class js/ReactBootstrap.Nav))
(def nav-item (reagent/adapt-react-class js/ReactBootstrap.NavItem))
