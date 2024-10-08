(ns my-webapp.handler
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [my-webapp.views :as views]))

(def app 
  (ring/ring-handler
   (ring/router
    [["/" {:handler views/index}]
     ["/add" {:handler views/test}]
     ["/form" 
      ["/:id" {:handler views/form}]
      ["/:id/register" {:handler views/test}]]]) 
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Not found"})})))

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