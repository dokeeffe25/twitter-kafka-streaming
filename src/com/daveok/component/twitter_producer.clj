(ns com.daveok.component.twitter-producer
  (:require [clj-kafka.producer :as producer]
            [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre])
  (:import [twitter4j FilterQuery StatusListener TwitterStreamFactory]
           twitter4j.conf.ConfigurationBuilder))

(timbre/refer-timbre)

(defn produce-twitter-to-topic [component twitter-chan]
  (let [configs (get-in component [:config :config])
        p (producer/producer
           {"metadata.broker.list" (str (:kafka-broker-host configs)
                                        ":"
                                        (:kafka-broker-port configs))
            "zookeeper.connect" (str (:zookeeper-host configs)
                                     ":"
                                     (:zookeeper-port configs))
            "serializer.class" "kafka.serializer.DefaultEncoder"
            "partitioner.class" "kafka.producer.DefaultPartitioner"})
        track-terms (into-array ["Olympics" "Rio" "IOC" "Athlete" "Athletes"])
        filter (doto (FilterQuery.)
                 (.track track-terms)
                 (.language (into-array ["en"])))
        listener (proxy [StatusListener] []
                   (onStatus [status]
                     (let [tweet (.getText status)]
                       (info tweet)
                       (try
                         (producer/send-message p (producer/message
                                                   (:kafka-topic configs)
                                                   (.getBytes tweet)))
                         (catch Exception e
                           (error "Can't send to topic" e))))))
        twitter-config (-> (doto (ConfigurationBuilder.)
                             (.setOAuthConsumerKey
                              (:consumer-key configs))
                             (.setOAuthConsumerSecret
                              (:consumer-secret configs))
                             (.setOAuthAccessToken
                              (:access-token configs))
                             (.setOAuthAccessTokenSecret
                              (:access-token-secret configs))
                             (.setJSONStoreEnabled true))
                           .build)
        twitter-stream (.getInstance (TwitterStreamFactory. twitter-config))]
    (async/go
      (info "Starting Streaming")
      (doto twitter-stream
        (.addListener listener)
        (.filter filter))
      (when (= :finished (async/<! twitter-chan))
        (info "cleaning up stream")
        (.cleanUp twitter-stream)
        (async/close! twitter-chan)))))

(defrecord Twitter-producer [twitter-chan]
  component/Lifecycle
  (start [this]
    (info "Starting Twitter Producer")
    (produce-twitter-to-topic this twitter-chan)
    this)
  (stop [this]
    (async/>!! twitter-chan :finished)
    (info "Stopping Twitter Producer")
    this))

(defn create-twitter-producer [twitter-chan]
  (map->Twitter-producer {:twitter-chan twitter-chan}))
