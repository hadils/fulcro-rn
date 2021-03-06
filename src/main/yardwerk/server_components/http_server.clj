(ns yardwerk.server-components.http-server
  (:require
    [yardwerk.server-components.config :refer [config]]
    [yardwerk.server-components.middleware :refer [middleware]]
    [clojure.pprint :refer [pprint]]
    [mount.core :refer [defstate]]
    [org.httpkit.server :as http-kit]
    [taoensso.timbre :as log]))

(defstate http-server
  :start
  (let [cfg (::http-kit/config config)]
    (log/info "Starting HTTP Server with config " (with-out-str (pprint cfg)))
    (http-kit/run-server middleware cfg))
  :stop (http-server))
