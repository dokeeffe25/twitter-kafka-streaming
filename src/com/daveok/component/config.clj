(ns com.daveok.component.config
  (:require [com.stuartsierra.component :as component]
            [immuconf.config :as config]
            [taoensso.timbre :as timbre]))

(timbre/refer-timbre)

(defrecord Config []
  component/Lifecycle
  (start [this]
    (info "Starting Config Component")
    (let [configs (apply config/load (:config this))]
      (info "Configuration Settings:" configs)
      (assoc this :config configs)))
  (stop [this]
    (info "Stopping Config Component")
    (assoc this :config nil)))

(defn create-config-component [config-files]
  (map->Config {:config config-files}))
