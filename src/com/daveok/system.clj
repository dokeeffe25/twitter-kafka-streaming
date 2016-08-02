(ns com.daveok.system
  (:require [clojure.core.async :as async]
            [com.daveok.component
             [config :as config]
             [twitter-producer :as twitter-producer]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(defn system [config-files]
  (let [twitter-chan (async/chan)]
    (info "Starting Twitter Streaming System")
    (component/system-map
     :config (config/create-config-component config-files)
     :twitter-producer (component/using
                        (twitter-producer/create-twitter-producer twitter-chan)
                        [:config]))))

(defn stop [system]
  (info "Stopping System")
  (component/stop-system system))

(defn -main [& configs]
  (component/start (system (into [] configs))))
