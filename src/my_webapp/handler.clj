(ns my-webapp.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [selmer.parser :as parser]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.anti-forgery :as util]
            [my-webapp.db :as db]))

(defroutes app-routes
  (GET "/" 
    [] 
    (parser/render-file "index.html" {:forms (db/get-all-forms) :token (util/anti-forgery-field)}))
  (POST "/add" 
    {params :params}
    (db/add-form (params :name)) (parser/render-file "success_add.html" {}))
  (GET "/form/:id" 
    [id] 
    (when (not-empty (db/get-form id) )
    (parser/render-file "form.html" {:id id :registered (db/get-registed id) :token (util/anti-forgery-field) :name ((first (db/get-form id)) :name)})))
  (POST "/form/:id/register" 
    {params :params} 
    (when (not-empty (db/get-form (params :id)))
    (db/add-registered (params :id) (params :name) (params :email)) (parser/render-file "success_register.html" {:id (params :id)})))
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