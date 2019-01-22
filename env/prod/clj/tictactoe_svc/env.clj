(ns tictactoe-svc.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[tictactoe-svc started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[tictactoe-svc has shut down successfully]=-"))
   :middleware identity})
