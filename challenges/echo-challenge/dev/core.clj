(ns dev.core
  (:require [portal.api :as p]
            [heyokx.echo-challenge :as main]))
;; => nil

(defn start-portal-vs-code []
  (p/open {:launcher :vs-code}))

(tap> (main/echo))

(comment
  
  (def portal-session (start-portal-vs-code)) ; create portal inspector session
  (add-tap #'p/submit) ; add portal as a tap> target

  (tap> "test!")

  (p/clear)

  (p/close)

  )

