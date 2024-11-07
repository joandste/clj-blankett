(ns my-webapp.routes
    (:require [taoensso.carmine :as car]
              [jsonista.core :as j]))


;; TODO move all redis stuff to different namespace
;; redis connection
(def redis-conn {:pool {} :spec {:uri "redis://localhost:6379"}})

(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))


;; Functions for redis
(defn in?
  [coll value]
  (some #(= value %) coll))

(defn get-form-ids
  []
  (map Integer/parseInt (wcar* (car/lrange "form-ids" 0 -1))))

(defn get-form-md
  [id]
  (first (wcar* (car/lrange (str "markdown-id:" id) 0 -1))))

(defn get-form-info
  [id]
  (first (wcar* (car/lrange (str "info-id:" id) 0 -1))))

(defn get-registered
  [id]
  (wcar* (car/lrange (str "registered-id:" id) 0 -1)))

(defn add-form-id
  [id]
  (when (not (in? (get-form-ids) id))
    (wcar* (car/rpush "form-ids" id))))

(defn add-form-md
  [id markdown]
  (when (and (in? (get-form-ids) id) (empty? (get-form-info id)))
    (wcar* (car/rpush (str "markdown-id:" id) markdown))))

(defn add-form-info
  [id title date description]
  (when (and (in? (get-form-ids) id) (empty? (get-form-info id)))
    (wcar* (car/rpush (str "info-id:" id) {:title title :date date :description description}))))

(defn add-registered
  [id name email]
  (when (in? (get-form-ids) id)
   (wcar* (car/rpush (str "registered-id:" id) {:name name :email email}))))


;; Route functions
(defn list-forms
  [_]
  {:status 200
   :body (j/write-value-as-string (map get-form-info (get-form-ids)))})

(defn form
  [request]
  (let [form (Integer/parseInt (:id (:path-params request)))]
    {:status 200
     :body (j/write-value-as-string (map #(:name %) (get-registered form)))}))

(defn register
  [request]
  (let [params (:params request)
          id (Integer/parseInt (:id (:path-params request)))
          name (get params "name")
          email (get params "email")]
    (add-registered id name email)
    {:status 200}))

(defn add
  [request]
  (let [params (:params request)
        name (get params "name")
        date (get params "date")
        description (get params "description")
        id (rand-int 99999)]
    (do (add-form-id id) (add-form-info id name date description)
    {:status 200})))


;; Testing
(comment
  (get-form-ids)
  (add-form-id 12)
  (add-form-info 12 "nalle partaj" "nov 11" "bahia hos nalle")
  (add-registered 12 "stewu" "stewu@abo.fi")
  (map get-form-info (get-form-ids))
  (map get-registered (get-form-ids))
  )