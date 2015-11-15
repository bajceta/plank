(ns plank.sync)

(defn- js->clj-keywords [x]
  (js->clj x :keywordize-keys true ))

(defn- fb-setter [fb-ref]
  (fn [key ref old-value new-value]
    (if (not= old-value new-value)
      (.set fb-ref (clj->js new-value)))))
      ;fb-reb.set(new-value);

(defn sync-ref [atom ref] 
    ; var ref = new Firebase(url);
    (.on ref "value" (fn [data]  (->> data .val js->clj-keywords (reset! atom))))
    ; ref.on("value",callback);
    (add-watch atom :fb-sync (fb-setter ref)))

(defn sync-url [synced-atom url]
  (let [firebase-ref (js/Firebase. url)]
    (sync-ref synced-atom firebase-ref)))
