;;   The Computer Language Benchmarks Game
;;   http://shootout.alioth.debian.org/

;; contributed by Andy Fingerhut

(ns regexdna
  (:gen-class)
  (:require [clojure.string :as str])
  (:import (java.util.regex Pattern)))


;; Slightly modified from standard library slurp so that it can read
;; from standard input.

(defn slurp-std-input
  ;; Reads the standard input using the encoding enc into a string and
  ;; returns it.
  ([] (slurp-std-input (.name (java.nio.charset.Charset/defaultCharset))))
  ([#^String enc]
     (with-open [r (new java.io.BufferedReader *in*)]
       (let [sb (new StringBuilder)]
	 (loop [c (.read r)]
	   (if (neg? c)
	     (str sb)
	     (do
	       (.append sb (char c))
	       (recur (.read r)))))))))


(def dna-seq-regexes '(    "agggtaaa|tttaccct"
		       "[cgt]gggtaaa|tttaccc[acg]"
		       "a[act]ggtaaa|tttacc[agt]t"
		       "ag[act]gtaaa|tttac[agt]ct"
		       "agg[act]taaa|ttta[agt]cct"
		       "aggg[acg]aaa|ttt[cgt]ccct"
		       "agggt[cgt]aa|tt[acg]accct"
		       "agggta[cgt]a|t[acg]taccct"
		       "agggtaa[cgt]|[acg]ttaccct" ))


(def iub-codes '( [ "B"  "(c|g|t)"   ]
		  [ "D"  "(a|g|t)"   ]
		  [ "H"  "(a|c|t)"   ]
		  [ "K"  "(g|t)"     ]
		  [ "M"  "(a|c)"     ]
		  [ "N"  "(a|c|g|t)" ]
		  [ "R"  "(a|g)"     ]
		  [ "S"  "(c|g)"     ]
		  [ "V"  "(a|c|g)"   ]
		  [ "W"  "(a|t)"     ]
		  [ "Y"  "(c|t)"     ] ))


(defn one-replacement [str [iub-str iub-replacement]]
  (str/replace str (. Pattern (compile iub-str)) iub-replacement))


(defn count-regex-occurrences [re s]
  ;; Prepending (?i) to the regexp in Java makes it
  ;; case-insensitive.
  [re (count (re-seq (. Pattern (compile (str "(?i)" re)))
                     s))])


(defn -main
  [& args]
  (let [content (slurp-std-input)
        original-len (count content)
        ;; I'd prefer if I could use the regexp #"(^>.*)?\n" like the
        ;; Perl benchmark does, but that only matches ^ at the beginning
        ;; of the string, not at the beginning of a line in the middle
        ;; of the string.
        content (str/replace content #"(^>.*|\n>.*)?\n" "")
        dna-seq-only-len (count content)]
    
    (doseq [[re num-matches] (pmap #(count-regex-occurrences % content)
                                   dna-seq-regexes)]
      (printf "%s %d\n" re num-matches))
    
    (let [content (reduce one-replacement content iub-codes)]
      (printf "\n%d\n%d\n%d\n" original-len dna-seq-only-len (count content))))
  (flush)
  (shutdown-agents))
