(ns my-webapp.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [org.httpkit.server :refer [run-server]]
            [my-webapp.routes :as routes]))

(def app 
  (ring/ring-handler
   (ring/router
    ["/api" [
             ["/listForms" {:get {
                                  :handler routes/list-forms
             }}] 
             ["/addForm" {:post { :middleware [parameters-middleware]
                                 :handler routes/add
                                 }}] 
             ["/form" [
                       ["/:id" {:get {
                                      :handler routes/form
                       }}] 
                       ["/:id/register" {:post { :middleware [parameters-middleware]
                                               :handler routes/register
                       }}]
              ]]
      ]])
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})})
   ;{:middleware [wrap-params]}
   ))

(defn -main []
  (run-server #'app {:port 3000}))

(comment
  ;; evaluate this def form to start the webapp via the REPL:
  ;; :join? false runs the web server in the background!
  (def server (run-server #'app {:port 3000 :join? false})))