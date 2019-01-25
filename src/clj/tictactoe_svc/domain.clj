(ns tictactoe-svc.domain
  (:require [clojure.spec.alpha :as s]))

(s/def ::player #{:x :o})
(s/def ::cell (s/nilable #{:x :o}))
(s/def ::range (s/and int? #(>=  % 0) #(<=  % 2)))
(s/def ::position (s/tuple ::range ::range))
(s/def ::move (s/keys :req [::player ::position]))
(s/def ::row (s/tuple ::cell ::cell ::cell))
(s/def ::board (s/tuple ::row ::row ::row))
(s/def ::turn ::player)
(s/def ::evaluation ::board)
(s/def ::winner #{:x :o :tie})
(s/def ::game (s/keys :req [::board]
                      :opt [::turn ::winner ::evaluation]))


(defn transpose [matrix]
  (apply mapv vector matrix))


(defn mirror [matrix]
  (mapv #(into [] (reverse %1)) matrix))


(defn create-row [val]
  (into [] (repeat 3 val)))


(def initial-board 
  (let [line (create-row nil)]
    (create-row line)))


(s/fdef update-board
  :args (s/cat :board ::board :move ::move)
  :ret ::board)
(defn update-board [board {::keys [player position]}]
  (let [[x y] position]
    (if (get-in board [x y])
      (throw (Exception. "occupied"))
      (assoc-in board [x y] player))))


(s/fdef winner
  :args (s/cat :evaluated-board ::board)
  :ret (s/nilable ::player))
(defn winner [evaluated-board]
  (some identity (apply concat evaluated-board)))


(s/fdef evaluate-row
  :args (s/cat :row ::row)
  :ret ::cell)
(defn evaluate-row [row]
  (reduce (fn [x y] (if (= x y) x nil)) row))


(s/fdef evaluate-rows
  :args (s/cat :board ::board)
  :ret ::board)
(defn evaluate-rows [board]
  (let [rows (for [r board]
               (evaluate-row r))]
    (into []
      (for [r rows]
        (into [] (repeat 3 r))))))


(s/fdef evaluate-rows
  :args (s/cat :board ::board)
  :ret ::board)
(defn evaluate-diagonal [board]
  (let [p (evaluate-row (into [] (for [p (range 3)] (get-in board [p p]))))]
    [[p nil nil]
     [nil p nil]
     [nil nil p]]))


(s/fdef evaluate-rows
  :args (s/cat :board ::board)
  :ret ::board)
(defn evaluate-board [board]
  (let [r0 (evaluate-rows board)
        rt  (transpose (evaluate-rows (transpose board)))
        rd0 (evaluate-diagonal board)
        rdt (into [] (mirror (evaluate-diagonal (mirror board))))]
    (cond
      (winner r0) r0
      (winner rt) rt
      (winner rdt) rdt
      :else rdt))) 


(s/fdef evaluate-rows
  :args (s/cat :board ::board)
  :ret boolean?)
(defn board-full [board]
  (every? identity (apply concat board)))


(s/fdef create-game
  :args (s/cat :starting-player ::player)
  :ret ::game)
(defn create-game [starting-player]
  {::board initial-board
   ::turn starting-player})


(s/fdef other-player
  :args (s/cat :player ::player)
  :ret ::player)               
(defn other-player [player]
  (cond (= player :x) :o
        (= player :o) :x
        :else nil))


(s/fdef make-move
  :args (s/cat :gamestate ::game :move ::move)
  :ret ::game)
(defn make-move [gamestate move]
  (when (not= (::turn gamestate) (::player move))
    (throw (Exception. "turn")))
  (let [gamestate* (update gamestate ::board update-board move)  
        evaluated-board (evaluate-board (::board gamestate*)) 
        player-won (winner evaluated-board)
        full? (board-full (::board gamestate*))]
     (cond-> gamestate*
       player-won (assoc ::winner player-won
                         ::evaluation evaluated-board)
       (and (not player-won) full?) (assoc ::winner :tie)
       (not (or player-won full?)) (update ::turn other-player))))
  

(evaluate-diagonal (mirror [[0 2 1][4 1 6][1 8 9]]))

(-> (create-game :o)
    (make-move {::player :o ::position [0 2]})
    (make-move {::player :x ::position [0 1]})
    (make-move {::player :o ::position [1 1]})
    (make-move {::player :x ::position [0 0]})
    (make-move {::player :o ::position [2 0]}))
