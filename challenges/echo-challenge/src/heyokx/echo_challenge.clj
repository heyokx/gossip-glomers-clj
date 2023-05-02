(ns heyokx.echo-challenge
  "Challenge - Echo: https://fly.io/dist-sys/1/"
  (:gen-class)
  (:require [cheshire.core :as c])
  
  (:import [java.util UUID]))

(defn listen
  "Listen to stdin until newline."
  []
  (let [input (read-line)]
    (tap> input)
    input))

(defn speak
  "Speak to stdout."
  [output]
  (tap> output)
  (spit *out* (str output "\n")))

(defn echo
  "Echo function!"
  []
  (let [content (c/generate-string {:src "a string identifying the node this message came from" 
                                    :dest "a string identifying the node this message is to"
                                    :body {
                                           :a 12
                                           :b "Blah"
                                           }})]
    content))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (print  "Need to implement!"))

(comment 
  (->> (listen)
       (c/parse-string)
       (speak))
  )
