(ns heyokx.echo-challenge
  (:gen-class)
  (:require [cheshire.core :as c]))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

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
  (greet {:name (first args)}))
