(ns tictactoe-svc.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [tictactoe-svc.game-state-in-memory :as state]))

(s/def ::name string?)
(s/def ::message string?)
(s/def ::player string?)
(s/def ::game string?)
(s/def ::y int?)
(s/def ::total spec/int?)
(s/def ::total-map (s/keys :req-un [::total]))

(def service-routes
  (api
    {:swagger {:ui "/swagger-ui"
               :spec "/swagger.json"
               :data {:info {:version "1.0.0"
                             :title "Sample API"
                             :description "Sample Services"}}}}
    (context "/games" []
      :coercion :spec
      (resource
       {:post
        {:summary "data-driven plus with clojure.spec using specs"
         :parameters {:form-params (s/keys :req-un [::player])}
         :responses {200 {:schema ::game}}
         :handler (fn [{{:keys [player]} :form-params}]
                    (ok (str (state/create-game! (keyword player)))))}}))))


state/games
