(ns dnd.app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["react" :as react :refer [memo]]
            ["react-dnd" :as react-dnd :refer [DndProvider]]
            ["react-dnd-html5-backend" :as react-html5-backend :refer [HTML5Backend]]

            [dnd.app.dustbin :as dustbin]))

(defn app []
  [:div.app
   [:> DndProvider {:backend HTML5Backend}
    [dustbin/container]]])

;; --- Render ---

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:dev/after-load reload! []
  (render))

(defn ^:export main []
  (render))
