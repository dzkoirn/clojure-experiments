(ns clojure-experiments.handler
  (:require [org.ozias.cljlibs.logging.logging :refer (infoc)]))

(defn handle-args 
  "Handle your program arguments here.  You may support zero or more.

  Ensure that you use the exit function when processing is complete."
  [config {:keys [options arguments]}]
  (if (seq arguments)
    (if (= 1 (count arguments))
      [0 ["Argument handler"]]
      [0 ["Arguments handler"]])
    [0 ["No argument handler"]]))
