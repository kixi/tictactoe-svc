(ns tictactoe-svc.test.domain
  (:require [tictactoe-svc.domain :as ttt]
            [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(stest/instrument)

(gen/generate (s/gen ::ttt/player))
(gen/generate (s/gen ::ttt/game))

(ttt/other-player :x)


(s/exercise ::ttt/row)

(s/exercise-fn `ttt/other-player)

(stest/check `ttt/mirror)
(gen/generate (s/gen ::ttt/winner))


(deftest t-transpose
  (testing ""
    (is (= [[1]] (ttt/transpose [[1]])))))
