(ns my-webapp.views
    (:require [my-webapp.db :as db]))

(def form1 (db/get-registed 1))

(defn index
  [_]
  {:status 200
   :body (db/get-all-forms)})

;; todo, add macro for getting id?
(defn form
  [_]
  {:status 200
   :body (str form1)})

(defn register
  []
  {})

;; test handler:
(defn test
  [req]
  {:status 200 :body (str req)})