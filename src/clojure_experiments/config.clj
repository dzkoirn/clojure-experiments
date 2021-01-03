(ns clojure-experiments.config
  (:require [clojure.java.io :as io]
            [environ.core :refer :all]))

(def ^:private app-name "clojure-experiments")
(def ^:private config-filename (str app-name "-config.clj"))

(defn- exists? [path]
  (.exists (io/file path)))

(defn- linux? []
  (.startsWith (env :os-name) "Linux"))

(defn- mac? []
  (.startsWith (env :os-name) "Mac"))

(defn- windows? []
  (.startsWith (env :os-name) "Windows"))

(defn- read-config-file [filename]
  (with-open [r (java.io.PushbackReader. (io/reader filename))]
    (read r)))

(defn- generate-filepath [& {:keys [prepend pathargs] :or {prepend false}}]
  (if (seq pathargs)
    (let [separated (interpose (env :file-separator) pathargs)]
      (apply str (if prepend (conj separated (env :file-separator)) separated)))))

(defn- append-filename [pathargs filename]
  (if (seq filename) (apply conj pathargs filename) pathargs))

(defn- system-level-config-path [& filename]
  (let [pathargs (-> (cond
                      (linux?) ["etc" app-name]
                      (windows?) [(env :programdata) app-name]
                      (mac?) ["Library" app-name])
                     (append-filename filename))]
    (cond
     (or (mac?)(linux?)) (generate-filepath :pathargs pathargs :prepend true)
     (windows?) (generate-filepath :pathargs pathargs))))
      
(defn- user-level-config-path [& filename]
  (let [pathargs (-> [(env :user-home) ".config" app-name] (append-filename filename))]
    (generate-filepath :pathargs pathargs)))

(defn- directory-level-config-path [& filename]
  (let [pathargs (-> [(env :user-dir)] (append-filename filename))]
    (generate-filepath :pathargs pathargs)))

(defn configure []
  (let [paths [(system-level-config-path config-filename)
               (user-level-config-path config-filename)
               (directory-level-config-path config-filename)]
        config (-> (filter exists? paths) first)
        config (if (seq config) (-> config io/file) (-> config-filename io/resource))]
    (read-config-file config)))
