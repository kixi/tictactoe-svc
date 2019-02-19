(ns tictactoe-svc.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [tictactoe-svc.game-state-in-db :as dbstate]
            [tictactoe-svc.domain :as ttt]))

(s/def ::player #{"x" "o"})
(s/def ::gameid uuid?)
(s/def ::games (s/coll-of ::gameid))
(s/def ::any identity)

(def service-routes
  (api
   {:swagger {:ui "/swagger-ui"
              :spec "/swagger.json"
              :data {:info {:version "1.0.0"
                            :title "Sample API"
                            :description "Sample Services"}}}
    :coercion :spec}

   (context "/games" []
     ;; (GET "/echo" []
     ;;   :query-params [param :- string?]
     ;;   :return string?
     ;;   (ok (str "hello " param)))
     (GET "/" []
       :return ::games
       (ok (dbstate/find-games!)))
     
     (POST "/" []
       :form-params [player :- ::player]
       :return ::gameid
       (ok  (dbstate/create-game! (keyword player))))

     (context "/:id" []
       :path-params [id :- ::gameid]
       
       (GET "/" []
         :return ::ttt/game
         (fn [req]
           (let [game (dbstate/find-game! id)]
             (ok game))))

       (POST "/moves" []
         :form-params [player :- ::player, row :- ::ttt/range, col :- ::ttt/range]
         :return ::ttt/game
         (ok (dbstate/make-move! id {::ttt/player (keyword player)
                                     ::ttt/position [row col]})))))))

