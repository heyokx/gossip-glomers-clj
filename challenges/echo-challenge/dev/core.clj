(ns dev.core
  (:require [portal.api :as p]
            [tech.v3.io :refer [get-json]]
            [heyokx.echo-challenge :as main]))

(defn start-portal-vs-code []
  (p/open {:launcher :vs-code}))

(defn init-msg-test [] 
  (->> (get-json "dev/resources/init_msg.json" :key-fn keyword)
       (main/init-msg-proc)
       (tap>))
  (reset! main/node {}))

(defn node-proc-test []
  (let [node-proc (main/create-node-proc 
                   identity
                   identity
                   {:node.init/message main/init-msg-proc})]
    (->> (get-json "dev/resources/empty.json" :key-fn keyword)
         (node-proc)
         (tap>))
    (tap> @main/node)
    (reset! main/node {})))

(comment

  (def portal-session (start-portal-vs-code)) ; create portal inspector session
  (add-tap #'p/submit) ; add portal as a tap> target

  (tap> "test!")

  (init-msg-test)
  (reset! main/node {})

  (def msg-proc-map 
    {
     :node.init/message main/init-msg-proc
    })
  
  (val (first msg-proc-map))

  (node-proc-test)

  (p/clear)

  (p/close)

  )

