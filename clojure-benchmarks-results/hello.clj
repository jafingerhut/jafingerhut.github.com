;; Hello, world for Clojure, so I can see how low I can set -Xmx max
;; heap setting on JVM and still succeed, and how much maximum
;; resident set size is used by the JVM process on various OS/JVM
;; combinations.

(ns hello
  (:gen-class))

(defn -main [& args]
  (println "Hello, world!"))
