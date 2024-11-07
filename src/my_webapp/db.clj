(ns my-webapp.db
  (:require [taoensso.carmine :as car]))

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

(defn get-form-status
  [id]
  (when (= (first
            (wcar* (car/lrange (str "status-id:" id) 0 -1)))
           "open")
    true))

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

(defn add-form-status-open
  [id]
  (when (and (in? (get-form-ids) id) (not (get-form-status id)))
    (wcar* (car/rpush (str "status-id:" id) "open"))))

(defn add-registered
  [id name email]
  (when (and (in? (get-form-ids) id) (get-form-status id))
   (wcar* (car/rpush (str "registered-id:" id) {:name name :email email}))))


;; Testing
(comment
  (get-form-ids)
  (add-form-id 12)
  (add-form-info 12 "nalle partaj" "nov 11" "bahia hos nalle")
  (add-registered 12 "stewu" "stewu@abo.fi")
  (get-registered 12)
  (map get-form-info (get-form-ids))
  (map get-registered (get-form-ids))
  (get-form-info 12)
  (get-form-status 12)
  (add-form-status-open 12)
  )
