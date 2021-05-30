(ns dnd.app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["react-dnd" :as react-dnd :refer [DndProvider useDrag useDrop]]
            ["react-dnd-html5-backend" :as react-html5-backend :refer [HTML5Backend]]))

(def ItemTypes {:BOX "box"})

(def box-style {:border "1px dashed gray"
                :background-color :white
                :padding "0.5rem 1rem"
                :margin-right "1.5rem"
                :margin-bottom "1.5rem"
                :cursor :move
                :float :left})

(def dustbin-style {:height "12rem"
                    :width "12rem"
                    :marginRight "1.5rem"
                    :marginBottom "1.5rem"
                    :color :white
                    :padding "1rem"
                    :textAlign :center
                    :fontSize "1rem"
                    :lineHeight :normal
                    :float :left})

(defn dustbin []
  [:>
   (fn []
     (let [[{:keys [canDrop isOver]} drop] (useDrop (fn []
                                                      (clj->js {:accept  (:BOX ItemTypes)
                                                                :drop    #({:name "Dustbin"})
                                                                :collect (fn [monitor]
                                                                           {:isOver  (.isOver ^js monitor)
                                                                            :canDrop (.canDrop ^js monitor)})})))
           isActive (and canDrop isOver)
           background-color (cond
                              isActive "darkgreen"
                              canDrop "darkkhaki"
                              :default "#222")]
       (r/as-element [:div {:ref   drop
                            :role  "Dustbin"
                            :style (merge dustbin-style {:background-color background-color})}
                      (if isActive "Release to drop" "Drag a box here")])))])

(defn box [{name :name}]
  [:>
   (fn []
     (let [[{:keys [isDragging]} drag] (useDrag (fn []
                                                  (clj->js {:type    (:BOX ItemTypes)
                                                            :item    {:name name}
                                                            :end     (fn [item monitor]
                                                                       (let [_ (js/console.log "item:::" item)
                                                                             _ (js/console.log "monitor:::" monitor)
                                                                             dropResult (.getDropResult ^js monitor)
                                                                             _ (js/console.log "item:::" item)
                                                                             _ (js/console.log "dropResult:::" dropResult)]
                                                                         (when (and item dropResult)
                                                                           (js/alert (str "You dropped " (:name item) " into " (:name dropResult) "!")))))
                                                            :collect (fn [monitor]
                                                                       {:isDragging (.isDragging ^js monitor)
                                                                        :handlerId  (.getHandlerId ^js monitor)})})))
           #_#__ (js/console.log "name:::" name)
           opacity (if isDragging 0.4 1)]
       (r/as-element [:div {:ref   drag
                            :role  "Box"
                            :style (merge box-style {:opacity opacity})}
                      name])))])

(defn container []
  [:div
   [:div {:style {:overflow :hidden, :clear :both}}
    [dustbin]]
   [:div {:style {:overflow :hidden, :clear :both}}
    [box {:name "Glass"}]
    [box {:name "Banana"}]
    [box {:name "Paper"}]]])

(defn app []
  [:div.app
   [:> DndProvider {:backend HTML5Backend}
    [container]]])

;; --- Render ---

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:dev/after-load reload! []
  (render))

(defn ^:export main []
  (render))
