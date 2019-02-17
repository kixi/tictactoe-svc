(ns tictactoe-svc.game-state-in-memory
  (:require [tictactoe-svc.domain :as d]
            [mount.core :refer [defstate]]))

(defstate games :start (atom {}))

(s/fdef create-game!)
(defn create-game! [player]
  (let [id (java.util.UUID/randomUUID)]
    (swap! games assoc id (d/create-game player))
    id))

(defn find-game! [id]
  (@games id))

(defn make-move! [id move]
  (swap! games update id #(d/make-move % move))
  (find-game! id))

;;(make-move! (create-game! :o) {::d/player :o ::d/position [0 0]})

;;(find-game! (create-game! :o))

;;games

