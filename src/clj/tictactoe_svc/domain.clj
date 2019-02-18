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

(s/fdef update-board
  :args (s/cat :board ::board :move ::move)
  :ret ::board)

(s/fdef winner
  :args (s/cat :evaluated-board ::board)
  :ret (s/nilable ::player))

(s/fdef find-row-winner
  :args (s/cat :row ::row)
  :ret ::cell)

(s/fdef mark-winner-row
  :args (s/cat :board ::board)
  :ret ::board)

(s/fdef create-game
  :args (s/cat :starting-player ::player)
  :ret ::game)

(s/fdef other-player
  :args (s/cat :player ::player)
  :ret ::player)

(s/fdef make-move
  :args (s/cat :gamestate ::game :move ::move)
  :ret ::game)

;; matrix helper functions
;; ==============================


(defn- transpose [matrix]
  (apply mapv vector matrix))

(defn- mirror [matrix]
  (mapv #(into [] (reverse %1)) matrix))

(defn- map-matrix [f m]
  (mapv (fn [r]
          (mapv (fn [c] (f c))
                r))
        m))

(defn- matrix-and [m1 m2]
  (mapv (fn [r1 r2]
          (mapv (fn [c1 c2] (and c1 c2)) r1 r2))
        m1 m2))
;;================================

(def initial-board
  [[nil nil nil]
   [nil nil nil]
   [nil nil nil]])

(def winning-rows
  (map (fn [row] (assoc initial-board row [true true true]))
       (range 0 3)))

(def winning-cols
  (map transpose winning-rows))

(def winning-diag1
  [[true nil nil]
   [nil true nil]
   [nil nil true]])

(def winning-diag2
  (mirror winning-diag1))

(def winning-positions
  (concat winning-rows winning-cols [winning-diag1] [winning-diag2]))

;;============================================

(defn evaluation-matrix [board player]
  (map-matrix (fn [cell] (= cell player)) board))

(defn winner-board [board player]
  (let [em (evaluation-matrix board player)]
    (->> winning-positions
         (filter (fn [position]
                   (= position (matrix-and position em))))
         (map (fn [winner-pos]
                (map-matrix (fn [cell]
                              (if cell player nil))
                            winner-pos)))
         first)))

(defn- update-board [board {::keys [player position]}]
  (let [[x y] position]
    (if (get-in board [x y])
      (throw (Exception. "occupied"))
      (assoc-in board [x y] player))))

(defn- winner [evaluated-board]
  (some identity (apply concat evaluated-board)))

(defn- evaluate-board [board]
  (or (winner-board board :x)
      (winner-board board :o)
      initial-board))

(defn- board-full [board]
  (every? identity (apply concat board)))

(defn create-game [starting-player]
  {::board initial-board
   ::turn starting-player})

(defn- other-player [player]
  (cond (= player :x) :o
        (= player :o) :x
        :else nil))

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

;;(require '[clojure.spec.test.alpha :as stest])
;;(stest/instrument)
;;(mark-winner-diagonal (mirror [[0 2 1] [4 1 6] [1 8 9]]))
;;
;;(-> (create-game :o)
;;    (make-move {::player :o ::position [0 2]})
;;    (make-move {::player :x ::position [0 1]})
;;    (make-move {::player :o ::position [1 1]})
;;    (make-move {::player :x ::position [0 0]})
;;    (make-move {::player :o ::position [2 0]})))
;;(create-game :o)
;;#:tictactoe-svc.domain{:board [[nil nil nil] [nil nil nil] [nil nil nil]], :turn :o}
