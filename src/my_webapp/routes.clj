(ns my-webapp.routes
    (:require [my-webapp.db :as db]))

(defn get-cached-form
  [form]
  ())

(defn list-forms
  [_]
  {:status 200
   :body (str all-forms)})

(defn form
  [request]
  {:status 200
   :body (str form1)})

(defn register
  [request] 
  (let [params (:params request)]
  {:status 200
   :body (do (db/add-registered 1 "stewu" "stewu@abo.fi") "succes")}))

(defn add
  [request]
  (let [params (:params request)]
    {:status 200
     :body (str "Received form data: " params)}))

;; test handler:
(defn test
  [req]
  {:status 200
   :body (str req)})

(comment
  (def form1 (db/get-registed 1))
  (def all-forms (db/get-all-forms)))