;;   The Computer Language Benchmarks Game
;;   http://shootout.alioth.debian.org/

;; contributed by Andy Fingerhut

(ns fasta
  (:gen-class))

(set! *warn-on-reflection* true)

;; Handle slight difference in function name between Clojure 1.2.0 and
;; 1.3.0-alpha*.
(defmacro my-unchecked-inc-int [& args]
  (if (and (== (*clojure-version* :major) 1)
           (== (*clojure-version* :minor) 2))
    `(unchecked-inc ~@args)
    `(unchecked-inc-int ~@args)))

(defmacro my-unchecked-add-int [& args]
  (if (and (== (*clojure-version* :major) 1)
           (== (*clojure-version* :minor) 2))
    `(unchecked-add ~@args)
    `(unchecked-add-int ~@args)))

(defmacro my-unchecked-multiply-int [& args]
  (if (and (== (*clojure-version* :major) 1)
           (== (*clojure-version* :minor) 2))
    `(unchecked-multiply ~@args)
    `(unchecked-multiply-int ~@args)))

(defmacro my-unchecked-remainder-int [& args]
  (if (and (== (*clojure-version* :major) 1)
           (== (*clojure-version* :minor) 2))
    `(unchecked-remainder ~@args)
    `(unchecked-remainder-int ~@args)))


(defn make-repeat-fasta [#^java.io.BufferedOutputStream ostream
                         line-length id desc s n]
  (let [descstr (str ">" id " " desc "\n")]
    (.write ostream (.getBytes descstr) 0 (count descstr)))
  (let [s-len (int (count s))
        line-length (int line-length)
        line-length+1 (int (inc line-length))
        min-buf-len (int (+ s-len line-length))
        repeat-count (int (inc (quot min-buf-len s-len)))
        buf (apply str (repeat repeat-count s))
        ;; Precompute all byte arrays that we might want to write, one
        ;; at each possible offset in the string s to be repeated.
        line-strings (vec (map (fn [i]
                                 (.getBytes (str (subs buf i (+ i line-length))
                                                 "\n")))
                               (range 0 s-len)))
        num-full-lines (int (quot n line-length))]
    (loop [j (int 0)
           s-offset (int 0)]
      (if (== j num-full-lines)
        ;; Write out the left over part of length n, if any.
        (let [remaining (int (rem n line-length))]
          (when (not= 0 remaining)
            (.write ostream
                    (.getBytes (str (subs buf s-offset (+ s-offset remaining))
                                    "\n"))
                    0 (inc remaining))))
        (do
          (.write ostream #^bytes (line-strings s-offset) 0 line-length+1)
          (recur (inc j) (int (my-unchecked-remainder-int
                               (my-unchecked-add-int s-offset line-length)
                               s-len))))))))


(definterface IPRNG
  (gen_random_BANG_ [^double max-val]))


(deftype PRNG [^{:unsynchronized-mutable true :tag int} rand-state]
  IPRNG
  (gen-random! [this max-val]
    (let [IM (int 139968)
          IM-double (double 139968.0)
          IA (int 3877)
          IC (int 29573)
          max (double max-val)
          last-state (int rand-state)
          next-state (int (my-unchecked-remainder-int
                           (my-unchecked-add-int
                            (my-unchecked-multiply-int last-state IA) IC) IM))
          next-state-double (double next-state)]
      (set! rand-state next-state)
      (/ (* max next-state-double) IM-double))))


(defmacro fill-random! [#^bytes gene-bytes #^doubles gene-cdf n #^bytes buf
                        my-prng]
  `(let [double-one# (double 1.0)]
     (dotimes [i# ~n]
       (let [x# (double (.gen-random! ~my-prng double-one#))
             ;; In my performance testing, I found linear search to
             ;; be a little faster than binary search.  The arrays
             ;; being searched are small.
             b# (byte (loop [j# (int 0)]
                        (if (< x# (aget ~gene-cdf j#))
                          (aget ~gene-bytes j#)
                          (recur (my-unchecked-inc-int j#)))))]
         (aset ~buf i# b#)))))


(defn make-random-fasta [#^java.io.BufferedOutputStream ostream
                         line-length id desc n #^bytes gene-bytes
                         #^doubles gene-cdf #^PRNG my-prng]
  (let [descstr (str ">" id " " desc "\n")]
    (.write ostream (.getBytes descstr)))
  (let [line-length (int line-length)
        len-with-newline (int (inc line-length))
        num-full-lines (int (quot n line-length))
        line-buf (byte-array len-with-newline)]
    (aset line-buf line-length (byte (int \newline)))
    (dotimes [i num-full-lines]
      (fill-random! gene-bytes gene-cdf line-length line-buf my-prng)
      (.write ostream line-buf (int 0) len-with-newline))
    (let [remaining-len (int (rem n line-length))]
      (when (not= 0 remaining-len)
        (fill-random! gene-bytes gene-cdf remaining-len line-buf my-prng)
        (.write ostream line-buf 0 remaining-len)
        (.write ostream (int \newline)))))
  my-prng)


(def alu (str "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG"
              "GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA"
              "CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT"
              "ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA"
              "GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG"
              "AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC"
              "AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA"))

(def iub [[\a 0.27]
          [\c 0.12]
          [\g 0.12]
          [\t 0.27]
          [\B 0.02]
          [\D 0.02]
          [\H 0.02]
          [\K 0.02]
          [\M 0.02]
          [\N 0.02]
          [\R 0.02]
          [\S 0.02]
          [\V 0.02]
          [\W 0.02]
          [\Y 0.02]])

(def homosapiens [[\a 0.3029549426680]
                  [\c 0.1979883004921]
                  [\g 0.1975473066391]
                  [\t 0.3015094502008]])


(defn prefix-sums-helper [x coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (let [sum (+ x (first s))]
       (cons sum (prefix-sums-helper sum (rest s)))))))


(defn prefix-sums [coll]
  (prefix-sums-helper 0 coll))


(defn make-genelist [pdf-map]
  (let [n (count pdf-map)
        bytes (byte-array n (map (fn [pair]
                                   (byte (int (first pair))))
                                 pdf-map))
        cdf (double-array n (prefix-sums (map #(nth % 1) pdf-map)))]
    [bytes cdf]))


(defn -main [& args]
  (let [n (if (and (>= (count args) 1)
                   (re-matches #"^\d+$" (nth args 0)))
            (. Integer valueOf (nth args 0) 10))
        line-length 60
        ostream (java.io.BufferedOutputStream. System/out)
        [iub-bytes iub-cdf] (make-genelist iub)
        [homosapiens-bytes homosapiens-cdf] (make-genelist homosapiens)
        my-prng (PRNG. (int 42))]
    (make-repeat-fasta ostream line-length "ONE" "Homo sapiens alu" alu (* 2 n))
    (let [my-prng2
          (make-random-fasta ostream line-length "TWO" "IUB ambiguity codes"
                             (* 3 n) iub-bytes iub-cdf my-prng)]
      (make-random-fasta ostream line-length "THREE" "Homo sapiens frequency"
                         (* 5 n) homosapiens-bytes homosapiens-cdf my-prng2))
    (.flush ostream)))
