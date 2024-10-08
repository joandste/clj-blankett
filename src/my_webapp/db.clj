(ns my-webapp.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(def db-spec {:dbtype "h2" :dbname "./my-db"})

(defn get-registed
  [form]
  (sql/query db-spec ["select name, email from registered where form = ?" form] {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-all-registed
  []
  (sql/query db-spec ["select form, name, email from registered"] {:builder-fn rs/as-unqualified-lower-maps}))

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

(defn get-all-forms
  []
  (sql/query db-spec ["select name, id from forms"] {:builder-fn rs/as-unqualified-lower-maps}))

(defn get-form
  [form]
  (sql/query db-spec ["select name, from forms where id = ?" form] {:builder-fn rs/as-unqualified-lower-maps}))

(comment 
  ;; create the tables, for now database has to be created from the REPL, not very userfriendly :D
  (create-form-table) 
  (create-registered-table)
  ;; functions to view the database tables:
  (get-all-registed)
  (get-all-forms))