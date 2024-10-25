(ns my-webapp.routes
    (:require [taoensso.carmine :as car]
              [jsonista.core :as j]))


;; redis connection
(def redis-conn {:pool {} :spec {:uri "redis://localhost:6379"}})

(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))


;; Functions for redis
(defn add-form
  [name]
  (wcar* (car/rpush "forms" name)))

(defn add-registered
  [form name email]
  (wcar* (car/rpush (str "registered-users:" form) {:name name :email email})))

(defn get-forms
  []
  (wcar* (car/lrange "forms" 0 -1)))

(defn get-registered
  [form]
  (wcar* (car/lrange (str "registered-users:" form) 0 -1)))


;; Route functions
(defn list-forms
  [_]
  {:status 200
   :body (j/write-value-as-string (get-forms))})

(defn form
  [request]
  (let [form (Integer/parseInt (:id (:path-params request)))]
    {:status 200
     :body (j/write-value-as-string (map #(get % :name) (get-registered form)))}))

(defn register
  [request]
  (if (= 0 0)
    (let [params (:params request)
          form (Integer/parseInt (:id (:path-params request)))
          name (get params "name")
          email (get params "email")]
      (add-registered form name email)
      {:status 200
       :body "success"})
    {:status 200
     :body "form is not open yet"}))

(defn add
  [request]
  (let [params (:params request)
        name (get params "name")]
    (add-form name)
    {:status 200
     :body "success"}))


;; Testing
(comment 
  (add-registered 1 "joakim" "stewu@abo.fi")
  (add-form "event 69")
  (wcar* (car/lrange "forms" 0 -1))
  (wcar* (car/lrange (str "registered-users:" 1) 0 -1))
  (count (get-registered 1))
  (map #(get % :name) (get-registered 1)))