(ns plank.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [plank.firebase :as firebase]
              [accountant.core :as accountant]))

;; -------------------------
;; Views




(defn minutes-planked [num] 
  [:a {:on-click (fn [] (swap! firebase/user update-in [:planks] conj num)) } num])

(defn home-page []
  [:div 
     [:div (str @firebase/user)]
    [:h2 "Welcome to plank"]
    [:h3 "Planked so far"]
    (reduce + (:planks @firebase/user))
    [:h3 "Minutes planked today?"]
    (map minutes-planked (range 1 10))
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About plank"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (firebase/init)
  (mount-root))
