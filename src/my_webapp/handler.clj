(ns my-webapp.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes 
  (GET "/" [] "Hello List")
  (GET "/form/:id" [id] (str "Hello Form @" id))
  (POST "/form/:id/register" {params :params} (str params))
  (GET "/test" [] (parser/render "Hello {{name}}!" {:name "Test"}))
  (route/not-found "Not Found"))

(def app
  ;; use #' prefix for REPL-friendly code
  (wrap-defaults #'app-routes site-defaults))

(defn -main []
  (jetty/run-jetty #'app {:port 3000}))

(comment
  ;; evaluate this def form to start the webapp via the REPL:
  ;; :join? false runs the web server in the background!
  (def server (jetty/run-jetty #'app {:port 3000 :join? false}))
  ;; evaluate this form to stop the webapp via the the REPL:
  (.stop server))