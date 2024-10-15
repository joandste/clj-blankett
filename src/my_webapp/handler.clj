(ns my-webapp.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [org.httpkit.server :refer [run-server]]
            [my-webapp.views :as views]))

(def app 
  (ring/ring-handler
   (ring/router
    ["/api" [
             ["/listForms" {:get {
                                  :handler views/index
             }}] 
             ["/addForm" {:post { :middleware [parameters-middleware]
                                 :handler views/add
                                 }}] 
             ["/form" [
                       ["/:id" {:get {
                                      :handler views/form
                       }}] 
                       ["/:id/register" {:get {
                                               :handler views/test
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