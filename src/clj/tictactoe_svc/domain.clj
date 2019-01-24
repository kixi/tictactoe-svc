(ns tictactoe-svc.domain)


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


(defn transpose [matrix]
  (apply mapv vector matrix))


(defn winner [evaluated-board]
  (some identity (apply concat evaluated-board)))


(defn evaluate-rows [board]
  (let [rows (for [r board]
               (reduce (fn [x y] (if (= x y) x nil)) r))]
    (for [r rows]
      (into [] (repeat 3 r)))))


(defn evaluate-board [board]
  (let [r0 (evaluate-rows board)
        rt (transpose (evaluate-rows (transpose board)))]
    (if (winner r0)
      r0
      rt)))


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
  

(-> (create-game :o)
    (make-move {:player :o :position [0 0]})
    (make-move {:player :x :position [0 1]})
    (make-move {:player :o :position [1 0]})
    (make-move {:player :x :position [0 2]})
    (make-move {:player :o :position [2 0]}))
