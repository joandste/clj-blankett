(ns my-webapp.routes
    (:require [taoensso.carmine :as car]
              [jsonista.core :as j]))


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

(defn get-form
  [id]
  (first (wcar* (car/lrange (str "form-id:" id) 0 -1))))

(defn get-registered
  [id]
  (wcar* (car/lrange (str "registered-id:" id) 0 -1)))

(defn add-form-id
  [id]
  (if (not (in? (get-form-ids) id))
    (wcar* (car/rpush "form-ids" id))))

(defn add-form
  [name date description id]
  (if (in? (get-form-ids) id)
    (wcar* (car/rpush (str "form-id:" id) {:name name :date date :description description :link id}))))

(defn add-registered
  [id name email]
  (if (in? (get-form-ids) id)
   (wcar* (car/rpush (str "registered-id:" id) {:name name :email email}))))


;; Route functions
(defn list-forms
  [_]
  {:status 200
   :body (j/write-value-as-string (map get-form (get-form-ids)))})

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
    (do (add-form-id id) (add-form name date description id)
    {:status 200})))


;; Testing
(comment 
  (add-registered 13217 "stewen" "stewu@abo.fi")
  (add-form-id 13)
  (get-form-ids)
  (add-form "nalle partaj 3" "nov 13" "nalle bahia hos nalle och mycket partaj" 13)
  (get-form 13217)
  (get-registered 13217)

  (wcar* (car/lrange "forms" 0 -1))
  (wcar* (car/lrange (str "registered-users:" 1) 0 -1))
  (count (get-registered 1))
  (map #(get % :name) (get-registered 1))
  (get-registered 12)
  (map get-form (get-form-ids))
  (get-form 12)
  (slurp "index.html"))