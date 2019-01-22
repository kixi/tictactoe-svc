(ns tictactoe-svc.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [tictactoe-svc.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[tictactoe-svc started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[tictactoe-svc has shut down successfully]=-"))
   :middleware wrap-dev})
