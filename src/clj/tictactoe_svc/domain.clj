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

;;================================

(def initial-board
  [[nil nil nil]
   [nil nil nil]
   [nil nil nil]])

(defn- update-board [board {::keys [player position]}]
  (let [[x y] position]
    (if (get-in board [x y])
      (throw (Exception. "occupied"))
      (assoc-in board [x y] player))))

(defn- winner [evaluated-board]
  (some identity (apply concat evaluated-board)))

(defn- find-row-winner [row]
  (reduce (fn [x y] (if (= x y) x nil)) row))

;;(find-row-winner [:x :x :x])
;;(find-row-winner [:x :o :x])
;;(find-row-winner [:x :o nil])

(defn- mark-winner-row [board]
  (let [rows (for [r board]
               (find-row-winner r))]
    (mapv (fn [p] [p p p]) rows)))

;;(mark-winner-row [[:x :x :o] [:x :x :x] [:x :o :x]])

(apply vector '((1 2 3) (1 2 3)))
(defn- mark-winner-diagonal [board]
  (let [p (find-row-winner (into []
                                 (for [p (range 3)]
                                   (get-in board [p p]))))]
    [[p nil nil]
     [nil p nil]
     [nil nil p]]))

(defn- evaluate-board [board]
  (let [r0 (mark-winner-row board)
        rt  (transpose (mark-winner-row (transpose board)))
        rd0 (mark-winner-diagonal board)
        rdt (into [] (mirror (mark-winner-diagonal (mirror board))))]
    (cond
      (winner r0) r0
      (winner rt) rt
      (winner rdt) rdt
      :else rdt)))

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
(-> (create-game :o)
    (make-move {::player :o ::position [0 2]})
    (make-move {::player :x ::position [0 1]})
    (make-move {::player :o ::position [1 1]})
    (make-move {::player :x ::position [0 0]})
    (make-move {::player :o ::position [2 0]}))
