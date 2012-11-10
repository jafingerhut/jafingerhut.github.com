;;   The Computer Language Benchmarks Game
;;   http://shootout.alioth.debian.org/

;; contributed by Andy Fingerhut.
;; Speed improvements contributed by Stuart Halloway.

(ns fannkuchredux
  (:require clojure.string)
  (:gen-class))

(set! *warn-on-reflection* true)


;; This macro assumes that 1 <= n <= (alength a), where a is a Java
;; array of ints.  No guarantees are made of its correctness if this
;; condition is violated.  It does this merely to avoid checking a few
;; conditions, and thus perhaps be a bit faster.

(defmacro reverse-first-n! [n #^ints a]
  `(let [n# (int ~n)
         n-1# (int (dec n#))]
     (loop [i# (int 0)
            j# (int n-1#)]
       (when (< i# j#)
         (let [temp# (aget ~a i#)]
           (aset ~a i# (aget ~a j#))
           (aset ~a j# temp#))
         (recur (inc i#) (dec j#))))))


(defmacro rotate-left-first-n! [n #^ints a]
  `(let [n# (int ~n)
	 n-1# (dec n#)
	 a0# (aget ~a 0)]
     (loop [i# (int 0)]
       (if (== i# n-1#)
	 (aset ~a n-1# a0#)
	 (let [i+1# (inc i#)]
	   (aset ~a i# (aget ~a i+1#))
	   (recur i+1#))))))


(defn fannkuch-of-permutation [#^ints p]
  (if (== (int 1) (aget p 0))
    ;; Handle this special case without bothering to create a Java
    ;; array.
    0
    ;; Using aclone instead of copy-java-int-array was a big
    ;; improvement.
    (let [#^ints p2 (aclone p)]
      (loop [flips (int 0)]
        (let [first-num (int (aget p2 0))]
          (if (== (int 1) first-num)
            flips
            (do
              (reverse-first-n! first-num p2)
              (recur (inc flips)))))))))


;; initialize the permutation generation algorithm.  The permutations
;; need to be generated in a particular order so that the checksum may
;; be computed correctly (or if you can determine some way to
;; calculate the sign from an arbitrary permutation, then you can
;; generate the permutations in any order you wish).

;; With the particular order of generating permutations used in this
;; program, it turns out that each of the n consecutive "groups" of
;; (n-1)!  permutations begin with these permutations (example given
;; for n=6):

;;   1st permutation: 1 2 3 4 5 6    sign: 1  count vals: 1 2 3 4 5 6
;; 121st permutation: 2 3 4 5 6 1    sign: 1  count vals: 1 2 3 4 5 5
;; 241st permutation: 3 4 5 6 1 2    sign: 1  count vals: 1 2 3 4 5 4
;; 361st permutation: 4 5 6 1 2 3    sign: 1  count vals: 1 2 3 4 5 3
;; 481st permutation: 5 6 1 2 3 4    sign: 1  count vals: 1 2 3 4 5 2
;; 601st permutation: 6 1 2 3 4 5    sign: 1  count vals: 1 2 3 4 5 1

;; This makes it very easy to divide the work into n parallel tasks
;; that each start at one of the permutations above, and generate only
;; (n-1)! permutations each.  Then combine the checksum and maxflips
;; values of each thread and print.

(defn init-permutations [n]
  (let [n-1 (dec n)]
    (loop [i 1
           p (int-array (range 1 (inc n)))
           sign 1
           c (int-array (range 1 (inc n)))
           tasks [{:perm p :sign sign :counts c}]]
      (if (== i n)
        tasks
        (let [p2 (aclone p)
              c2 (aclone c)]
          (rotate-left-first-n! n p2)
          (aset c2 n-1 (dec (aget c2 n-1)))
          (recur (inc i) p2 sign c2
                 (conj tasks {:perm p2 :sign sign :counts c2})))))))


(defmacro swap-array-elems! [a i j]
  `(let [temp# (aget ~a ~i)]
     (aset ~a ~i (aget ~a ~j))
     (aset ~a ~j temp#)))


;; Modify the passed Java arrays p (a permutation) and c (count
;; values) in place.  Let caller negate the sign themselves.

;; Return true if the final value of p is a new permutation, false if
;; there are no more permutations and the caller should not use the
;; value of p for anything.

(defn next-permutation! [N #^ints p sign #^ints c]
  (if (neg? sign)
    (let [N (int N)
	  N-1 (dec N)]
      (swap-array-elems! p 1 2)
      (loop [i (int 2)]
	(if (== i N)
	  true)
	(let [cx (aget c i)
	      i+1 (inc i)]
	  (if (not= cx 1)
	    (do
	      (aset c i (dec cx))
	      true)
	    (if (== i N-1)
	      false
	      (do
		(aset c i i+1)
		(rotate-left-first-n! (inc i+1) p)
		(recur i+1)))))))
    ;; else sign is +1
    (swap-array-elems! p 0 1)))


(defn partial-fannkuch [num-perms #^ints p-arg first-sign #^ints c-arg]
  (let [#^ints p (aclone p-arg)
        #^ints c (aclone c-arg)
        N (int (count p))]
    (loop [i (int num-perms)
           sign (int first-sign)
	   maxflips (int 0)
	   checksum (int 0)]
      (if (zero? i)
        [checksum maxflips]
        (let [curflips (int (fannkuch-of-permutation p))]
          (next-permutation! N p sign c)
          (recur (dec i) (- sign) (int (max maxflips curflips))
                 (+ checksum (* sign curflips))))))))


(defn fannkuch [N]
  (let [init-perms (init-permutations N)
        N-1-factorial (reduce * (range 1 N))
        partial-results (pmap (fn [{p :perm, s :sign, c :counts}]
                                (partial-fannkuch N-1-factorial p s c))
                              init-perms)]
    (reduce (fn [[checksum1 maxflips1] [checksum2 maxflips2]]
              [(+ checksum1 checksum2) (max maxflips1 maxflips2)])
            partial-results)))


(defn -main [& args]
  (let [N (if (and (>= (count args) 1)
		   (re-matches #"^\d+$" (nth args 0)))
	    (. Integer valueOf (nth args 0) 10)
	    10)]
    (let [[checksum maxflips] (fannkuch N)]
      (printf "%d\n" checksum)
      (printf "Pfannkuchen(%d) = %d\n" N maxflips)))
  (flush)
  (shutdown-agents))
