(ns my-webapp.routes
  (:require [jsonista.core :as j]
            [my-webapp.db :as db]))


;; Route functions
(defn list-forms
  [_]
  {:status 200
   :body (j/write-value-as-string (map (fn [id] {:title (db/get-form-title id) :date (db/get-form-date id)}) (db/get-form-ids)))})

(defn form
  [request]
  (let [form (Integer/parseInt (:id (:path-params request)))]
    {:status 200
     :body (j/write-value-as-string (map #(:name %) (db/get-registered form)))}))

(defn register
  [request]
  (let [params (:params request)
          id (Integer/parseInt (:id (:path-params request)))
          name (get params "name")
          email (get params "email")]
    (db/add-registered id name email)
    {:status 200}))

(defn add
  [request]
  (let [params (:params request)
        name (get params "name")
        date (get params "date")
        description (get params "description")
        id (rand-int 99999)]
    (do (db/add-form-id id) (db/add-form-info id name date description)
    {:status 200})))
