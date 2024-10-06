(ns my-webapp.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def forms '(1 2 69 73))

(defroutes app-routes
  (GET "/" [] (parser/render-file "index.html" {:forms forms}))
  (GET "/form/:id" [id] (parser/render-file "form.html" {:id id}))
  (POST "/form/:id/register" {params :params} (str params))
  (route/not-found "Not Found"))

(def app
  ;; use #' prefix for REPL-friendly code
  (wrap-defaults #'app-routes (assoc-in site-defaults [:security :anti-forgery] false)))

(defn -main []
  (jetty/run-jetty #'app {:port 3000}))

(comment
  ;; evaluate this def form to start the webapp via the REPL:
  ;; :join? false runs the web server in the background!
  (def server (jetty/run-jetty #'app {:port 3000 :join? false}))
  ;; evaluate this form to stop the webapp via the the REPL:
  (.stop server)
  ;; template path:
  (selmer.parser/set-resource-path! "/home/user/clj-blankett/src/my_webapp"))