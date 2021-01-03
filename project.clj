(defproject clojure-experiments "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[environ "0.4.0"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.reader "0.8.3"]
                 [org.ozias.cljlibs/logging "0.1.3"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]]}})
