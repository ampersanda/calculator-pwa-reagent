(ns calculator-app.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cuerdas.core :as stg]))

(def default-value {:display-value     0
                    :prev-value        0
                    :next-value        0
                    :current-operation nil})

(def digit-limit 10)

(defonce calculator-state (atom default-value))

(defn- string-to-fn [s]
  (cond
    (= s "+") +
    (= s "-") -
    (= s "x") *
    (= s "/") /))

(defn- class-spec [type]
  (cond
    (or (= type :number) (= type :append) (= type :reset)) "button-white"
    (= type :operator) "button-gray"
    (= type :equal) "button-orange button-equal"))

(defn calculator-button [type text]
  (let [{:keys [display-value current-operation prev-value next-value]} @calculator-state
        cops-nil? (nil? current-operation)]
    [:button {:class    (class-spec type)
              :on-click (fn [e]
                          (cond
                            (= type :operator) (if cops-nil?
                                                 (swap! calculator-state assoc :current-operation (string-to-fn text))
                                                 (let [ans (current-operation prev-value next-value)]
                                                   (swap! calculator-state assoc
                                                          :display-value (str ans)
                                                          :prev-value ans
                                                          :next-value 0
                                                          :current-operation (string-to-fn text))))
                            (= type :number) (if (not (> (count (str display-value)) digit-limit))
                                               (let [changed-value (stg/parse-double (str (if cops-nil? prev-value next-value) text))]
                                                 (if cops-nil?
                                                   (swap! calculator-state assoc
                                                          :display-value (str changed-value)
                                                          :prev-value changed-value)
                                                   (swap! calculator-state assoc
                                                          :display-value (str changed-value)
                                                          :next-value changed-value))))
                            (= type :reset) (reset! calculator-state default-value)
                            (= type :append) (let [comma? (re-find #"\d+\.+\d+" (str display-value))]
                                               (when (not comma?)
                                                 (swap! calculator-state assoc
                                                        (if cops-nil? :prev-value :next-value) (str display-value ".")
                                                        :display-value (str display-value "."))))
                            (= type :equal) (if (not cops-nil?)
                                              (let [ans (current-operation prev-value next-value)]
                                                (swap! calculator-state assoc
                                                       :display-value (subs (str ans) 0 10)
                                                       :prev-value ans
                                                       ;; :next-value 0
                                                       ;; :current-operation nil
                                                       )))))} text]))

(defn calculator []
  (let [{:keys [display-value]} @calculator-state]
    [:div.calculator
     [:div.display display-value]
     [:div.button-wrapper
      [calculator-button :operator "+"]
      [calculator-button :operator "-"]
      [calculator-button :operator "x"]
      [calculator-button :operator "/"]
      [calculator-button :equal "="]
      [calculator-button :number "7"]
      [calculator-button :number "8"]
      [calculator-button :number "9"]
      [calculator-button :number "4"]
      [calculator-button :number "5"]
      [calculator-button :number "6"]
      [calculator-button :number "1"]
      [calculator-button :number "2"]
      [calculator-button :number "3"]
      [calculator-button :number "0"]
      [calculator-button :append "."]
      [calculator-button :reset "C"]]]))

(defn start []
  (reagent/render-component [calculator]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds

  ;; service worker
  (try
    (-> (. js/navigator.serviceWorker (register "/sw.js"))
        (.then (fn [] (js/console.log "service worker registered"))))

    (catch js/Object err (js/console.error "Failed to register service worker" err)))

  ;(js/console.error (contains? js/navigator "serviceWorker"))
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
