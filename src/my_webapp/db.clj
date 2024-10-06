(ns my-webapp.db
  (:require [next.jdbc.sql :as sql]))

(def db-spec {:dbtype "h2" :dbname "./my-db"})

