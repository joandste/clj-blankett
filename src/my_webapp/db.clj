(ns my-webapp.db
  (:require [taoensso.carmine :as car])
  (:import [java.time LocalDateTime]))



;; redis connection
(def redis-conn {:pool {} :spec {:uri "redis://localhost:6379"}})

(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))



;; Functions for checking
(defn datetime-passed? [year month day hour minute]
  (let [now (LocalDateTime/now)
        specified-datetime (LocalDateTime/of year month day hour minute)]
    (.isAfter now specified-datetime)))

(defn in?
  [coll value]
  (some #(= value %) coll))



;; functions for getting form data
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
  (if (= (wcar* (car/get (str "form-open-" id))) "open")
    true
   (when (apply datetime-passed? (map Integer/parseInt (clojure.string/split (get-form-date id) #" ")))
     (do (wcar* (car/set (str "form-open-" id) "open")) true))))



;; functions for adding and setting form data
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



;; registered users data
(defn get-registered
  [id]
  (wcar* (car/lrange (str "form-registered-" id) 0 -1)))

(defn add-registered
  [id name email]
  (when (and (in? (get-form-ids) id) (form-open? id))
   (wcar* (car/rpush (str "form-registered-" id) {:name name :email email}))))



;; Testing
(comment
  (get-form-ids)
  (add-form-id 15)
  (set-form-title 15 "nalle partaj 2")
  (set-form-date 15 "2024 11 8 5 52")
  (get-form-date 15)
  (time (form-open? 15))
  (add-registered 15 "stewu" "stewu@abo.fi")
  (count (get-registered 15))
  (get-registered 15)
  (get-form-date 12)
  (map get-form-md (get-form-ids))
  (map get-registered (get-form-ids))
  (time (form-open? 12))
  )
