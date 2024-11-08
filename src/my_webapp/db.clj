(ns my-webapp.db
  (:require [taoensso.carmine :as car])
  (:import [java.time LocalDateTime]))



;; redis connection
(def redis-conn {:pool {} :spec {:uri "redis://localhost:6379"}})

(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))



;; Functions for redis
(defn datetime-passed? [year month day hour minute]
  (let [now (LocalDateTime/now)
        specified-datetime (LocalDateTime/of year month day hour minute)]
    (.isAfter now specified-datetime)))

(defn in?
  [coll value]
  (some #(= value %) coll))



(defn get-form-ids
  []
  (map Integer/parseInt (wcar* (car/lrange "form-ids" 0 -1))))

(defn get-form-md
  [id]
  (wcar* (car/get (str "form-markdown-" id))))

(defn get-form-title
  [id]
  (wcar* (car/get (str "form-title-" id))))

(defn get-form-date
  [id]
  (wcar* (car/get (str "form-date-" id))))

(defn form-open?
  [id]
  (apply datetime-passed? (map Integer/parseInt (clojure.string/split (get-form-date id) #" "))))



(defn add-form-id
  [id]
  (when (not (in? (get-form-ids) id))
    (wcar* (car/rpush "form-ids" id))))

(defn set-form-md
  [id markdown]
  (when (in? (get-form-ids) id)
    (wcar* (car/set (str "form-markdown" id) markdown))))

(defn set-form-title
  [id title]
  (when (in? (get-form-ids) id)
    (wcar* (car/set (str "form-title-" id) title))))

(defn set-form-date
  [id date]
    (when (in? (get-form-ids) id)
     (wcar* (car/set (str "form-date-" id) date))))



(defn get-registered
  [id]
  (wcar* (car/lrange (str "registered-id:" id) 0 -1)))

(defn add-registered
  [id name email]
  (when (and (in? (get-form-ids) id) (form-open? id))
   (wcar* (car/rpush (str "registered-id:" id) {:name name :email email}))))



;; Testing
(comment
  (get-form-ids)
  (add-form-id 69)
  (set-form-title 69 "nalle partaj 2")
  (set-form-date 69 "2024 11 8 2 30")
  (get-form-date 12)
  (add-registered 12 "stewu" "stewu@abo.fi")
  (count (get-registered 12))
  (get-registered 12)
  (get-form-date 12)
  (map get-form-md (get-form-ids))
  (map get-registered (get-form-ids))
  (time (form-open? 12))
  )
