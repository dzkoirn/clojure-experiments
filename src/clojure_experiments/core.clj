(ns clojure-experiments.core
  (:gen-class)
  (:refer-clojure :exclude [read read-string])
  (:require [clojure-experiments.config :refer (configure)]
            [clojure-experiments.handler :refer (handle-args)]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.reader.edn :refer [read]]
            [environ.core :refer :all]
            [org.ozias.cljlibs.logging.logging :refer (configure-logging errorc infoc)]
            [taoensso.timbre :as timbre]))

(def ^:private logging-options
  {:stdout true 
   :formatter (fn [{:keys [throwable message]}]	
                (format "%s%s" (or message "") (or (timbre/stacktrace throwable \newline) "")))})

(defn- tag-indices [tag v]
  (keep-indexed #(when (= %2 tag) %1) v))

(defn- usage [config summary]
  (let [usage (:usage config)]
    (first (map #(assoc usage %1 summary) (tag-indices "##summary##" usage)))))

(defn- error-msg [config errors]
  (apply conj (:error config) (interpose \newline errors)))

(defn exit [exit-code message]
  (if (= 0 exit-code)
    (apply infoc message)
    (apply errorc message))
  exit-code)

(defn- mutually-exclusive?
  "Filter the truthy args from a sequence and count them.

  The args are mutually exclusive if the resulting sequence only has one element."
  [args]
  (-> (filter true? args) count (= 1)))

(defn- check-mutually-exclusive 
  "Check a list of arguments for one truthy value.  If there isn't exactly one truthy argument
  add the given error message to the :errors key in the supplied options."
  [options args error]
  (if (mutually-exclusive? args)
    options
    (->> error (conj (:error options)) (assoc-in options [:errors]))))

(defn- set-default
  "Set the default value for mutually exclusive options if none are set.

  Otherwise, check that the supplied arguments are mutually exclusive."
  [options args defaultkey]
  (if (every? false? args)
    (assoc-in options [:options defaultkey] true)
    (check-mutually-exclusive options args "Either all or none can be chosen")))
  
(defn- check-options
  "Check options, setting defaults as necessary."
  [{{:keys [all none]} :options :as opts}]
  (-> opts (set-default (list all none) :none)))

(defn run 
  "Processing starts here.  Use this function when running from a REPL.

  The help option and any errors are handled here.  Otherwise, processing
  is passed to the argument handler."
  [& args]
  (let [config (configure)
        {:keys [options arguments errors summary] :as opts} 
        (-> (parse-opts args (:cli config))
            check-options
            (update-in [:options] merge logging-options)
            configure-logging)]
    (cond
     (:help options) (exit 0 (usage config summary))
     errors (exit 1 (error-msg config errors))
     :else (let [[exit-code message] (handle-args config opts)]
             (exit exit-code message)))))
  
(defn -main
  "Entry point for command-line processing.  This will be used when running outside of a REPL."
  [& args]
  (System/exit (apply run args)))
