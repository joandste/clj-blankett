(ns my-webapp.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(def db-spec {:dbtype "h2" :dbname "./my-db"})

(defn get-registed
  []
  (sql/query db-spec ["select id, name, email from address"]))

(defn add-registered
  []
  (sql/insert! db-spec :address {:name "Oscar" :email "oscar@abo.fi"}))

(defn create-table
  []
  (jdbc/execute-one! db-spec ["
     create table address (
     id int auto_increment primary key,
     name varchar(32),
     email varchar(255)
     )
     "]))
