(ns tictactoe-svc.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [tictactoe-svc.game-state-in-memory :as state]
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
     (GET "/" []
       :return ::games
       (ok (keys @state/games)))
     (context "/:id" []
        :path-params [id :- ::gameid]
        (GET "/" []
          :return ::any
          (fn [req]
            (let [game (@state/games id)]
              (println "CONFORM***" (s/conform ::ttt/game game))
              (ok game))))))))

@state/games

   #_(context "/api" []
       (context "/games" []
         :coercion :spec
         (resource
          {:post
           {:summary "create a new game"
            :parameters {:form-params (s/keys :req-un [::player])}
            :responses {200 {:schema ::gameid}}
            :handler (fn [{{:keys [player]} :form-params}]
                       (ok (state/create-game! (keyword player))))}
           :get
           {:summary "list all games"
            :responses {200 {:schema ::games}}
            :handler (fn [req]
                       (ok (keys @state/games)))}})))




  



