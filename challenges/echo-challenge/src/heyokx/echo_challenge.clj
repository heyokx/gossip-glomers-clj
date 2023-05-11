(ns heyokx.echo-challenge
  "Challenge - Echo: https://fly.io/dist-sys/1/"
  (:gen-class)
  (:require [charred.api :as charred]
            [clojure.spec.alpha :as s]
            [heyokx.spec.node]
            [taoensso.timbre :as timbre
             :refer [error]])
  
  (:import [java.util UUID]))

(defonce node (atom {}))

(defn listen
  "Listen to stdin until newline."
  []
  (let [input (read-line)] 
    (tap> (str "Listening:" input))
    (cond
      (string? input)
      (do 
        (tap> (str "Input:" input))
        input))))

(defn speak
  "Speak to stdout."
  ([])
  ([output]
  (tap> (str "Output:" output))
  (cond
    (not (nil? output)) 
    (do
      (tap> (str "Speaking:" output))
      (println output)))))

(defn- parse-input
  [input]
  (try
    (let [parsed-input (charred/read-json input :key-fn keyword)]
      (tap> parsed-input)
      parsed-input)
    (catch Exception e (error e))))

(defn- format-output
  [output]
  (try
    (let [formatted-output (charred/write-json-str output)]
      (tap> formatted-output)
      formatted-output)
    (catch Exception e (error e))))

(defn init-msg-proc
  [init-msg]
  (cond
    (nil? (:id @node))
    (do
      (swap! node assoc :id (-> (:body init-msg)
                                (:node_id)))
      (swap! node assoc :net-nodes (-> (:body init-msg)
                                       (:node_ids)))
      {:src (:id @node)
       :dest (:src init-msg)
       :body {
              :type "init_ok"
              :in_reply_to (-> (:body init-msg)
                               (:msg_id))
       }})
    :else (error "Node ID already set!")))

(defn iter-loop
  "Node loop execution wrapper."
  [listen-fn speak-fn node-proc-fn]
  #(loop []
     (->> (listen-fn)
          (node-proc-fn)
          (apply speak-fn))
     (recur)))

(defonce msg-proc-map
  {:node.init/message init-msg-proc})

(defn create-node-proc
  "Creates the node processor fn associated to the given parameters.
   Parameters:
    parse-input-fn - transforms input into the desired processing format,
    output-format-fn - transforms output into the desired format,
    msg-proc-map - map of messages and their associated processors"
  [parse-input-fn output-format-fn msg-proc-map]
  (fn [input]
    (let [message (parse-input-fn input)
          msg-procs (-> (filter #(s/valid? (key %) message) msg-proc-map)
                        (vals))]
      (doall 
       (map #(-> (% message) (output-format-fn)) msg-procs)))))

(defn -main
  "Node entry point."
  [& _]
  (let [node-proc (create-node-proc parse-input format-output msg-proc-map)
        run-loop (iter-loop listen speak node-proc)]
    (run-loop)))

(comment 
  (def node-proc (create-node-proc parse-input format-output msg-proc-map))

  (->> (listen)
       (node-proc)
       (speak)) 
  
  (reset! node {})
  (swap! node assoc :id "n1")
  
  (def msg-example
    {:src "n1"
     :dest "n3"
     :body {:type "init"
            :msg_id 1
            :node_id "n3"
            :node_ids ["n1" "n2" "n3"]}})
  
  (init-msg-proc msg-example)
  @node
  )