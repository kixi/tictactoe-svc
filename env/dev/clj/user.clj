(ns user
  (:require [tictactoe-svc.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [tictactoe-svc.core :refer [start-app]]
            [tictactoe-svc.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'tictactoe-svc.core/repl-server))

(defn stop []
  (mount/stop-except #'tictactoe-svc.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'tictactoe-svc.db.core/*db*)
  (mount/start #'tictactoe-svc.db.core/*db*)
  (binding [*ns* 'tictactoe-svc.db.core]
    (conman/bind-connection tictactoe-svc.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))

(defn add-dependency [dep-vec]
  (require 'cemerick.pomegranate)
  ((resolve 'cemerick.pomegranate/add-dependencies)
   :coordinates [dep-vec]
   :repositories (merge @(resolve 'cemerick.pomegranate.aether/maven-central)
                        {"clojars" "https://clojars.org/repo"})))


;; (add-dependency '[net.mikera/core.matrix "0.62.0"])
;; (require '[clojure.core.matrix :as m])
;; (m/transpose [[1 2] [3 4]])
