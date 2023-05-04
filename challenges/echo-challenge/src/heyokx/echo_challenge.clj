(ns heyokx.echo-challenge
  "Challenge - Echo: https://fly.io/dist-sys/1/"
  (:gen-class)
  (:require [charred.api :as charred]
            [taoensso.timbre :as timbre
             :refer [error]])
  
  (:import [java.util UUID]))

(defn listen
  "Listen to stdin until newline."
  []
  (let [input (read-line)] 
    (tap> (str "Listening:" input))
    (cond
      (string? input);;TODO: potentially extract this (shouldn't be part of `listen`) + change check
      (do 
        (tap> (str "Input:" input))
        input))))

(defn speak
  "Speak to stdout."
  [output]
  (tap> (str "Output:" output))
  (cond
    (not (nil? output)) 
    (do
      (tap> (str "Speaking:" output))
      (println output))))

(defn- parse-input
  [input]
  (try
    (charred/read-json input)
    (catch Exception e (error e))))

(defn node-proc
  "Node processor."
  [input]
  (let [parsed-input (parse-input input)] ;should probably be called `parse-message` and `message`
    (cond
      (not (nil? parsed-input)) (let 
                                 [output (charred/write-json-str parsed-input)]
                                  output))))

(defn iter-loop
  "Node loop execution wrapper."
  [listen-fn speak-fn node-proc-fn]
  #(loop []
     (->> (listen-fn)
          (node-proc-fn)
          (speak-fn))
     (recur)))

(defn -main
  "Node entry point."
  [& args]
  (let [run-loop (iter-loop listen speak node-proc)]
    (run-loop)))

(comment 
  (->> (listen)
       (node-proc)
       (speak))
  )
