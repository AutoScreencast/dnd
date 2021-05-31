(ns dnd.app.dustbin-w
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

;; `draggable-wrapper` takes args: (1) symphony-node (2) sub to id (3) function with args of drag-ref and dnd-props.
;; It is called as: [draggable-wrapper symphony-node children-ids reagent-child-fn can-drag-fn (optional)]
(defn draggable-wrapper [node id reagent-child-fn]
  [:>
   (fn []
     (let [_ (js/console.log "name" (:name node))
           [dnd-props drag-ref] (useDrag
                                  (fn []
                                      #js {:type    (:BOX ItemTypes)
                                           :item    {:name (:name node)}
                                           :end     (fn [item monitor]
                                                      (let [dropResult (.getDropResult ^js monitor)]
                                                        (when (and item dropResult)
                                                          (js/alert (str "You dropped " (:name item) " into " (.-name dropResult) "!")))))
                                           :collect (fn [monitor]
                                                      (let [isDragging (.isDragging ^js monitor)
                                                            handlerId (.getHandlerId ^js monitor)]
                                                        #js {:isDragging isDragging
                                                             :handlerId handlerId}))}))]
       (r/as-element
         [:div
          (reagent-child-fn drag-ref dnd-props node)])))]) ;; added node here to pass down the name of the draggable element

;; `droppable-wrapper` takes args: (1) can-drop-fn (arg: js-args) (2) drop-fn (arg: js-args) (3) reagent-child-fn (with args drop-ref and dnd-props).
;; It is called as: [droppable-wrapper can-drop-fn drop-fn reagent-child-fn]

(defn droppable-wrapper [can-drop-fn drop-fn reagent-child-fn]
  [:>
   (fn []
     (let [[dnd-props drop-ref] (useDrop (fn []
                                           #js {:accept (:BOX ItemTypes)
                                                :drop drop-fn
                                                :canDrop can-drop-fn
                                                :collect (fn [monitor]
                                                           (let [#_#__ (js/console.log "isOver:::" (.isOver ^js monitor))
                                                                 #_#__ (js/console.log "canDrop:::" (.canDrop ^js monitor))]
                                                             #js {:isOver  (.isOver ^js monitor)
                                                                  :canDrop (.canDrop ^js monitor)}))}))]
       (r/as-element (reagent-child-fn drop-ref dnd-props))))])

(defn example []
  [:div

   ;; --- DROP ---
   [:div {:style {:overflow :hidden, :clear :both}}
    ;[dustbin]
    [droppable-wrapper
     nil
     (fn []
       #js {:name "Dustbin"})
     (fn [drop-ref dnd-props]
       (let [canDrop (.-canDrop dnd-props)
             isOver (.-isOver dnd-props)
             isActive (and canDrop isOver)
             background-color (cond
                                isActive "darkgreen"
                                canDrop "darkkhaki"
                                :else "#555")]
         [:div {:ref   drop-ref
                :style (assoc dustbin-style :background-color background-color)}
          (if isActive "Release to drop" "Drag a box here")]))]]

   ;; --- DRAG ---
   [:div {:style {:overflow :hidden, :clear :both}}
    ;[box {:name "Glass"}]
    [draggable-wrapper
     {:name "Glass"}
     nil
     (fn [drag-ref dnd-props node]
       (let [isDragging (.-isDragging dnd-props)
             opacity (if isDragging 0.4 1)]
         [:div {:ref   drag-ref
                :style (assoc box-style :opacity opacity)}
          (:name node)]))]

    ;[box {:name "Banana"}]
    [draggable-wrapper
     {:name "Banana"}
     nil
     (fn [drag-ref dnd-props node]
       (let [isDragging (.-isDragging dnd-props)
             opacity (if isDragging 0.4 1)]
         [:div {:ref   drag-ref
                :style (assoc box-style :opacity opacity)}
          (:name node)]))]

    ;[box {:name "Paper"}]
    [draggable-wrapper
     {:name "Paper"}
     nil
     (fn [drag-ref dnd-props node]
       (let [isDragging (.-isDragging dnd-props)
             opacity (if isDragging 0.4 1)]
         [:div {:ref   drag-ref
                :style (assoc box-style :opacity opacity)}
          (:name node)]))]]])
