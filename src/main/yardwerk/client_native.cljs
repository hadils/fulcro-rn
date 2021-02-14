(ns yardwerk.client-native
  "Entry point for native client."
  (:require
    [yardwerk.application :refer [SPA]]
    [fulcro-native.expo-application :as expo]
    [yardwerk.mobile-ui.root :as root]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [yardwerk.model.session :as session]))

;; See defines in shadow-cljs for dev mode
(goog-define SERVER_URL "http://production.server.com/api-native")

(defn ^:export start
  {:dev/after-load true}
  []
  (log/info "Re-mounting")
  (app/mount! @SPA root/Root :i-got-no-dom-node))

(defn init []
  (reset! SPA (expo/fulcro-app
                {:client-did-mount (fn [app]
                                     (uism/begin! app session/session-machine ::session/session
                                       {:actor/login-form      root/Login
                                        :actor/current-session session/Session}))
                 :remotes          {:remote (net/fulcro-http-remote {:url        SERVER_URL
                                                                     :make-xhrio #(doto (net/make-xhrio)
                                                                                    (.setWithCredentials true))})}}))
  (app/set-root! @SPA root/Root {:initialize-state? true})
  (dr/initialize! @SPA)
  (uism/begin! @SPA session/session-machine ::session/session
    {:actor/login-form      root/Login
     :actor/current-session session/Session})
  (dr/change-route! @SPA ["main"])
  (start))
