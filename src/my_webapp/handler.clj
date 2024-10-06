(ns my-webapp.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :as util]))

(def forms '(73 69 54 23))

(def registered [])

(defroutes app-routes
  (GET "/" [] (parser/render-file "index.html" {:forms forms}))
  (GET "/form/:id" [id] (parser/render-file "form.html" {:id id :registered registered :token (util/anti-forgery-field)}))
  (POST "/form/:id/register" {params :params} (def registered (conj registered (params :name))) "success")
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
  (.stop server)
  ;; template path:
  (selmer.parser/set-resource-path! "/home/user/clj-blankett/src/my_webapp"))