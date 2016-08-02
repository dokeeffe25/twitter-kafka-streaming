(defproject twitter-kafka-streaming "0.1.0-SNAPSHOT"
  :description "Kafka Producer that takes from Twitter using the Streaming API"
  :url "TODO"
  :license {:name "TODO: Choose a license"
            :url "http://choosealicense.com/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [levand/immuconf "0.1.0"]
                 [twitter-api "0.7.8"]
                 [org.twitter4j/twitter4j-core "3.0.6"]
                 [org.twitter4j/twitter4j-stream "3.0.6"]
                 [clj-kafka "0.3.4"]
                 [http.async.client "1.1.0"]
                 [clj-http "3.1.0"]
                 [org.clojure/core.async "0.2.385"]
                 [com.taoensso/timbre "4.7.3"]]
  :profiles {:dev {:dependencies
                   [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["dev"]}}
  :main com.daveok.system)
