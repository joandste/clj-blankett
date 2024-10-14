(ns my-webapp.handler
  (:require [reitit.ring :as ring]
            [org.httpkit.server :refer [run-server]]
            [my-webapp.views :as views]))

(def app 
  (ring/ring-handler
   (ring/router
    [["/api" [
              ["/" {:handler views/index}]
              ["/add" {:handler views/test}]
              ["/form" [
                        ["/:id" {:handler views/form}]
                        ["/:id/register" {:handler views/test}]
              ]] 
      ]]
     ["/hej" {:handler (fn [_] {:status 200 :body "hej"})}]]) 
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})})))

(defn -main []
  (run-server #'app {:port 3000}))

(comment
  ;; evaluate this def form to start the webapp via the REPL:
  ;; :join? false runs the web server in the background!
  (def server (run-server #'app {:port 3000 :join? false})))