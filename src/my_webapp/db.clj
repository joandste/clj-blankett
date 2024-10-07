(ns my-webapp.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def db-spec {:dbtype "h2" :dbname "./my-db"})

(defn get-registed
  [form]
  (sql/query db-spec ["select name, email from registered where form = ?" form]))

(defn get-all-registed
  []
  (sql/query db-spec ["select form, name, email from registered"]))

(defn get-registed-names
  [form]
  (sql/query db-spec ["select name from registered where form = ?" form] {:builder-fn rs/as-unqualified-lower-maps}))

(defn add-registered
  [form name email]
  (sql/insert! db-spec :registered {:form form :name name :email email}))

(defn create-registered-table
  []
  (jdbc/execute-one! db-spec ["
     create table registered (
     form int,
     name varchar(32),
     email varchar(255)
     )
     "]))

(defn create-form-table
  []
  (jdbc/execute-one! db-spec ["
     create table forms (
     id int auto_increment primary key,
     name varchar(32)
     )
     "]))

(defn add-form
  [name]
  (sql/insert! db-spec :forms {:name name}))

(defn get-forms
  []
  (sql/query db-spec ["select name, id from forms"] {:builder-fn rs/as-unqualified-lower-maps}))

(comment 
  (create-form-table)
  (add-form "snopp")
  (get-forms)
  (create-registered-table)
  (get-registed 69)
  (add-registered 69 "Oscar" "oscar@abo.fi")
  (get-registed-names 69)
  (get-all-registed)

  (map #(get % :name) (get-forms))
  )