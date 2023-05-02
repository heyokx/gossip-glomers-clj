(ns heyokx.node
  (:require [clojure.spec.alpha :as s]))

(s/def :message/src string?)
(s/def :message/dest string?)

; Acceptable message body types and keys differ from request and response.
; Could separate into own types; this is fine for now.
(s/def :message.body/type (and string? #{"init" "init_ok" "error"}))

(s/def :message.body/msg_id uuid?)
(s/def :message.body/in_reply_to uuid?)

; Will differ from messages. Should probably create separate body types.
(s/def :message/body (s/keys :req [:message.body/type]
                             :opt [:message.body/msg_id :message.body/in_reply_to]))

(s/def :node/message (s/keys :req-un [:message/src :message/dest :message/body]))

(comment
  
  )