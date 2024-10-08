(ns my-webapp.views
    (:require [selmer.parser :as parser]
              [my-webapp.db :as db]))

(defn index
  [_]
  {:status 200 :headers {"Content-Type" "Text/html"} 
   :body (parser/render-file "index.html" 
                             {:forms (db/get-all-forms)})})

;; todo, add macro for getting id?
(defn form
  [req]
  {:status 200 :headers {"Content-Type" "Text/html"} 
   :body (parser/render-file "form.html" 
                             {:id ((req :path-params) :id) :registered (db/get-registed ((req :path-params) :id)) :name ((first (db/get-form ((req :path-params) :id))) :name)})})

(defn register
  []
  {})

;; test handler:
(defn test
  [req]
  {:status 200 :body (str req)})
