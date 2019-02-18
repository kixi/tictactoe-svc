(ns tictactoe-svc.test.domain
  (:require [tictactoe-svc.domain :as ttt]
            [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(stest/instrument)

(def evaluate-board #'ttt/evaluate-board)

(deftest winning-games
  (testing "winning row"
    (is (= [[:x :x :x] [nil nil nil] [nil nil nil]]
           (evaluate-board [[:x :x :x] [:x :o :o] [:o nil nil]]))))
  (testing "winning col"
    (is (= [[:x nil nil] [:x nil nil] [:x nil nil]]
           (evaluate-board [[:x nil :x] [:x :o :o] [:x nil nil]]))))
  (testing "winning diag"
    (is (= [[:x nil nil] [nil :x nil] [nil nil :x]]
         (evaluate-board [[:x nil :x] [:o :x :o] [:o nil :x]]))))
  (testing "no winning position"
    (is (= [[nil nil nil] [nil nil nil] [nil nil nil]]
         (evaluate-board [[:x nil :x] [:o :x :o] [:o nil :o]])))))

(deftest make-move
  (testing "make a non winning move"
    (is (= {::ttt/board [[:o nil nil] [nil nil nil] [nil nil nil]],
            ::ttt/turn :x}
           (ttt/make-move {::ttt/board [[nil nil nil] [nil nil nil] [nil nil nil]],
                           ::ttt/turn :o}
                          {::ttt/player :o ::ttt/position [0 0]})))))

