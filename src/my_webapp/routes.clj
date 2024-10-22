(ns my-webapp.routes
    (:require [my-webapp.db :as db]))

(def registered-users (atom {}))

(def forms (atom []))

(defn add-form
  [name]
  (swap! forms conj name))

(defn get-registered
  [form]
  (map #(get % :name) (get (deref registered-users) form)))

(defn add-registered
  [form name email]
  (swap! registered-users update form (fnil conj []) {:name name :email email}))

(defn list-forms
  [_]
  {:status 200
   :body (deref forms)})

(defn form
  [request]
  (let [form (Integer/parseInt (:id (:path-params request)))]
    {:status 200
     :body (get-registered form)}))

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

;; test handler:
(defn test
  [req]
  {:status 200
   :body (str req)})

(comment
  (def form1 (db/get-registed 1))
  (def all-forms (db/get-all-forms))
  (str (slurp registered-users))
  (db/get-all-forms)
  (deref registered-users)
  (add-registered 1 "joakim" "stewu@abo.fi")
  (add-form "event 69")
  (deref forms)
  (get (deref registered-users) 1)
  (Integer/parseInt "1")
  (count (get-registered 1))
  (map #(get % :name) (get-registered 1)))