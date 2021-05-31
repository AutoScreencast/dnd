(ns dnd.app.dustbin-f
  (:require [reagent.core :as r]
            ["react" :as react :refer [memo]]
            ["react-dnd" :as react-dnd :refer [useDrag useDrop]]))

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

(defn box [{name :name}]
  (let [[collectedProps drag] (useDrag
                                (fn []
                                  #js {:type    (:BOX ItemTypes)
                                       :item    {:name name}
                                       :end     (fn [item monitor]
                                                  (let [dropResult (.getDropResult ^js monitor)]
                                                    (when (and item dropResult)
                                                      (js/alert (str "You dropped " (:name item) " into " (.-name dropResult) "!")))))
                                       :collect (fn [monitor]
                                                  (let [_ _]
                                                    #js {:isDragging (.isDragging ^js monitor)
                                                         :handlerId  (.getHandlerId ^js monitor)}))}))
        _ (js/console.log "Render box" name)
        isDragging (.-isDragging collectedProps)
        _ (js/console.log "isDragging" isDragging name)
        opacity (if isDragging 0.4 1)]
    [:div {:ref   drag
           :role  "Box"
           :style (assoc box-style :opacity opacity)}
     name]))

(defn dustbin []
  (let [[collectedProps drop] (useDrop
                                (fn []
                                   #js {:accept  (:BOX ItemTypes)
                                        :drop    (fn []
                                                   #js {:name "Dustbin"})
                                        :collect (fn [monitor]
                                                   (let [#_#__ (js/console.log "isOver:::" (.isOver ^js monitor))
                                                         #_#__ (js/console.log "canDrop:::" (.canDrop ^js monitor))]
                                                     #js {:isOver  (.isOver ^js monitor)
                                                          :canDrop (.canDrop ^js monitor)}))}))
        canDrop (.-canDrop collectedProps)
        isOver (.-isOver collectedProps)
        isActive (and canDrop isOver)
        background-color (cond
                           isActive "darkgreen"
                           canDrop "darkkhaki"
                           :else "#555")]
    [:div {:ref   drop
           :role  "Dustbin"
           :style (assoc dustbin-style :background-color background-color)}
     (if isActive "Release to drop" "Drag a box here")]))

(defn container []
  [:div
   [:div {:style {:overflow :hidden, :clear :both}}
    [:f> dustbin]]
   [:div {:style {:overflow :hidden, :clear :both}}
    [:f> box {:name "Glass"}]
    [:f> box {:name "Banana"}]
    [:f> box {:name "Paper"}]]])
