(ns dnd.app.blocks-w
  (:require [reagent.core :as r]
            ["react" :as react :refer [memo]]
            ["react-dnd" :as react-dnd :refer [useDrag useDrop]]))

(defn draggable-wrapper [item reagent-child-fn]
  [:>
   (fn []
     (let [[dnd-props ref preview-ref] (useDrag (fn []
                                                  (clj->js {:type :block
                                                            :item item
                                                            :collect (fn [monitor]
                                                                       (let [is-dragging? (.isDragging ^js monitor)]
                                                                         {:is-dragging? is-dragging?}))})))]
       (r/as-element
         (reagent-child-fn ref preview-ref dnd-props))))])

#_(defn droppable-wrapper [can-drop-fn drop-fn reagent-child-fn]
    [:>
     (fn []
       (let [collect-cache-atm (atom nil)
             [dnd-props ref] (useDrop (clj->js {:accept :block
                                                :drop drop-fn
                                                :canDrop can-drop-fn
                                                :collect (fn [monitor]
                                                           ;; this debounce function was inspired by https://github.com/react-dnd/react-dnd/issues/421
                                                           (let [now (.now js/Date)
                                                                 [ms cache] @collect-cache-atm]
                                                             (if (and cache
                                                                      (< (- now ms) 50))
                                                               cache
                                                               (let [is-over? (.isOver ^js monitor)
                                                                     can-drop? (.canDrop ^js monitor)
                                                                     ;; item (.getItem ^js monitor)
                                                                     ret {:is-over? is-over?
                                                                          :can-drop? can-drop?}]
                                                                          ;;:item item
                                                                 (reset! collect-cache-atm [now ret])
                                                                 ret))))}))]
         (r/as-element (reagent-child-fn ref dnd-props))))])

(defn droppable-wrapper [can-drop-fn drop-fn reagent-child-fn]
  [:>
   (fn []
     (let [[dnd-props ref] (useDrop (fn []
                                      (clj->js {:accept  :block
                                                :drop    drop-fn
                                                :canDrop can-drop-fn
                                                :collect (fn [monitor]
                                                           (let [#_#__ (js/console.log "collect")
                                                                 is-over? (.isOver ^js monitor)
                                                                 can-drop? (.canDrop ^js monitor)]
                                                             {:is-over?  is-over?
                                                              :can-drop? can-drop?}))})))
           #_#__ (js/console.log "dnd props:" dnd-props)]
       (r/as-element (reagent-child-fn ref dnd-props))))])

(defn block [id]
  [:div {:style {:border "1px solid" :height "50px" :margin "5px" :position :relative}}
   [droppable-wrapper
    (fn [js-item]
      (let [num (-> js-item (js->clj :keywordize-keys true) :id)]
        (and (not= id num)
             (or (and (even? id) (even? num))
                 (and (odd?  id) (odd?  num))))))
    (fn [] (println "drop onto:" id))
    (fn [ref dnd-props]
      (let [{:keys [can-drop? is-over?]} (-> dnd-props (js->clj :keywordize-keys true))]
        [:div {:ref ref
               :style (merge {:width "100%" :height "100%" :position :absolute :background-color "gold"}
                             (when can-drop? {:background-color "green"}))}]))]
   [draggable-wrapper
    {:id id}
    (fn [ref preview-ref dnd-props]
      (let [is-dragging? (:is-dragging? dnd-props)
            _ (js/console.log "is-dragging?:" is-dragging?)
            opacity (if is-dragging? 0.2 1)]
        [:div {:ref   ref
               :style (assoc {:z-index 100 :padding "4px" :background-color "darkgrey" :position :relative} :opacity opacity)}
         "id: " id]))]])

(defn example []
  [:div
   [:div "Welcome to react-dnd-in-reagent"
    (map (fn [x] ^{:key x} [block x]) (range 0 21))]])
