(ns tictactoe-svc.game-state-in-db
  (:require [tictactoe-svc.domain :as d]
            [tictactoe-svc.db.core :as db]
            [mount.core :refer [defstate]]))


              
(defn nilarg [f x]
  (when x
    (f x)))

(defn gamestate->dbgame [gamestate id]
  {:id id
   :c00 (nilarg name (get-in gamestate [::d/board 0 0]))
   :c01 (nilarg name (get-in gamestate [::d/board 0 1])) 
   :c02 (nilarg name (get-in gamestate [::d/board 0 2])) 
   :c10 (nilarg name (get-in gamestate [::d/board 1 0]))
   :c11 (nilarg name (get-in gamestate [::d/board 1 1])) 
   :c12 (nilarg name (get-in gamestate [::d/board 1 2])) 
   :c20 (nilarg name (get-in gamestate [::d/board 2 0]))
   :c21 (nilarg name (get-in gamestate [::d/board 2 1])) 
   :c22 (nilarg name (get-in gamestate [::d/board 2 2])) 
   :turn (nilarg name (::d/turn gamestate))
   :winner nil})

(defn dbgame->gamestate [dbgame]
  (when dbgame
    {::d/board [[(keyword (:c00 dbgame)) (keyword (:c01 dbgame)) (keyword (:c02 dbgame))]
                [(keyword (:c10 dbgame)) (keyword (:c11 dbgame)) (keyword (:c12 dbgame))]
                [(keyword (:c20 dbgame)) (keyword (:c21 dbgame)) (keyword (:c22 dbgame))]]
     ::d/turn (keyword (:turn dbgame))}))

(defn create-game! [player]
  (let [id (java.util.UUID/randomUUID)
        gamestate (d/create-game player)
        dbgamestate (gamestate->dbgame gamestate id)]
    (db/insert-game dbgamestate)
    id))

(defn find-game! [id]
  (let [db-gamestate  (db/get-game {:id id})]
     (dbgame->gamestate db-gamestate)))

;; (db/get-game {:id  #uuid "9b752900-68ea-4a20-95c7-d94ff9e94c60"})
;; (find-game!  #uuid "9b752900-68ea-4a20-95c7-d94ff9e94c60")

(defn find-games! []
  (map #(:id %) (db/get-games {})))

;; (find-games!)

(defn make-move! [id move]
  (let [game (find-game! id)
        _ (println game)
        game* (d/make-move game move)]
    (println game*)
    (println (gamestate->dbgame game* id))
    (db/update-game (gamestate->dbgame game* id))
    game*))

;; (make-move! #uuid "9b752900-68ea-4a20-95c7-d94ff9e94c60"
            ;; {::d/player :o ::d/position [0 2]}))
;; (name nil)

;;(make-move! (create-game! :o) {::d/player :o ::d/position [0 0]})

;;(find-game! (create-game! :o))

;;games

;;(create-game! :o)
;; fnil
