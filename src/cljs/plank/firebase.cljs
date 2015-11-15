(ns plank.firebase
  (:require [reagent.core :as reagent :refer [atom]]
            [plank.sync :as sync]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]))

(def ref (atom nil))
(def user-ref (atom nil))
(def user (atom {}))
(def auth-data (atom nil))

(defn init-user-data []
  (reset! user {:name (:displayName (:google @auth-data))
                        :planks [] })
  (.set @user-ref (clj->js @user))
  (println @user)
  (println "init-user-data"))

(add-watch user :valid-data (fn [key ref old new] (if (= nil new) (init-user-data))))

(defn- auth-handler [error authData]
  (let  [data (js->clj authData :keywordize-keys true)]
    (if (= nil error)
      (do 
        (reset! auth-data data)
        (reset! user-ref (.child @ref (str "users/"  (:uid data))))
        (sync/sync-ref user @user-ref)
        (println @user)
        (println "set the user ref"))
      )
    (println error)
    (println authData)
    (println data)))

(defn init []
  (println "woohoo") 
  (reset! ref (js/Firebase. "https://fb-test321.firebaseio.com"))
  (.authWithOAuthPopup @ref "google" auth-handler))
