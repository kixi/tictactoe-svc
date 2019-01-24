(ns tictactoe-svc.domain)


(defn transpose [matrix]
  (apply mapv vector matrix))


(defn mirror [matrix]
  (mapv #(into [] (reverse %1)) matrix))


(defn create-row [val]
  (into [] (repeat 3 val)))


(def initial-board 
  (let [line (create-row nil)]
    (create-row line)))


(defn update-board [board {:keys [player position]}]
  (let [[x y] position]
    (if (get-in board [x y])
      (throw (Exception. "occupied"))
      (assoc-in board [x y] player))))


(defn winner [evaluated-board]
  (some identity (apply concat evaluated-board)))


(defn evaluate-row [row]
  (reduce (fn [x y] (if (= x y) x nil)) row))
  

(defn evaluate-rows [board]
  (let [rows (for [r board]
               (evaluate-row r))]
    (for [r rows]
      (into [] (repeat 3 r)))))


(defn evaluate-diagonal [board]
  (let [p (evaluate-row (for [p (range 3)] (get-in board [p p])))]
    [[p nil nil]
     [nil p nil]
     [nil nil p]]))


(defn evaluate-board [board]
  (let [r0 (evaluate-rows board)
        rt (transpose (evaluate-rows (transpose board)))
        rd0 (evaluate-diagonal board)
        rdt (mirror (evaluate-diagonal (mirror board)))]
    (cond
      (winner r0) r0
      (winner rt) rt
      (winner rdt) rdt
      :else rdt))) 


(defn board-full [board]
  (every? identity (apply concat board)))


(defn create-game [starting-player]
  {:board initial-board
   :turn starting-player})


(defn other-player [player]
  (cond (= player :x) :o
        (= player :o) :x
        :else nil))


(defn make-move [gamestate move]
  (when (not= (:turn gamestate) (:player move))
    (throw (Exception. "turn")))
  (let [gamestate* (update gamestate :board update-board move)  
        evaluated-board (evaluate-board (:board gamestate*)) 
        player-won (winner evaluated-board)
        full? (board-full (:board gamestate*))]
     (cond-> gamestate*
       player-won (assoc :winner player-won
                         :evaluation evaluated-board)
       (and (not player-won) full?) (assoc :winner :tie)
       (not (or player-won full?)) (update :turn other-player))))
  

(evaluate-diagonal (mirror [[0 2 1][4 1 6][1 8 9]]))

(-> (create-game :o)
    (make-move {:player :o :position [0 2]})
    (make-move {:player :x :position [0 1]})
    (make-move {:player :o :position [1 1]})
    (make-move {:player :x :position [0 0]})
    (make-move {:player :o :position [2 0]}))
