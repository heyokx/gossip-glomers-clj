(ns heyokx.spec.node
  (:require [clojure.spec.alpha :as s]))

(s/def :message/src string?)
(s/def :message/dest string?)

; Acceptable message body types and keys differ from request and response.
; Could separate into own types; this is fine for now.
(s/def :message.body/type (and string? #{"init" "init_ok" "error"}))

(s/def :message.body/msg_id number?)
(s/def :message.body/in_reply_to uuid?)

; Will differ from messages. Should probably create separate body types.
(s/def :message/body (s/keys :req-un [:message.body/type]
                             :opt-un [:message.body/msg_id :message.body/in_reply_to]))

(s/def :node/message (s/keys :req-un [:message/src :message/dest :message/body]))

;; {
;;   "type":     "init",
;;   "msg_id":   1,
;;   "node_id":  "n3",
;;   "node_ids": ["n1", "n2", "n3"]
;; }
(s/def :message.init.body/node_id string?)
(s/def :message.init.body/node_ids (s/+ :message.init.body/node_id))
(s/def :message.init.body/type #{"init"})
(s/def :message.init/body (s/merge :message/body
                                   (s/keys :req-un [:message.init.body/type 
                                                    :message.init.body/node_id 
                                                    :message.init.body/node_ids])))
(s/def :node.init/message (s/merge :node/message
                                   (s/keys :req-un [:message.init/body])))

(comment
  (def msg-example
    {:src "n1"
     :dest "n3"
     :body {
            :type "init"
            :msg_id 1
            :node_id "n3"
            :node_ids ["n1" "n2" "n3"]
     }})
  
  (s/explain :node.init/message msg-example)
  (s/conform :node.init/message msg-example)
  (tap> (s/describe :node.init/message))
  
  )