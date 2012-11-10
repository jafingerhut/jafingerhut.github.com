;; The Computer Language Benchmarks Game
;; http://shootout.alioth.debian.org/

;; Based on the Racket version of the benchmark by Matthew Flatt
;;; contributed by PheliX
;; Minor changes by Andy Fingerhut so it runs on both Clojure 1.2 and 1.3

(ns pidigits
  (:gen-class))

(defmacro if-available [sym if-expr else-expr]
  (try
    (resolve sym)
    if-expr
    (catch Exception _
      else-expr)))

;; 'fixup' allows this code to work on both Clojure 1.3, which has the
;; new class clojure.lang.BigInt, and Clojure 1.2, which does not.

(defmacro fixup [j]
  (if-available clojure.lang.BigInt
   `(let [j# ~j]
      (if (instance? clojure.lang.BigInt j#)
        (.toBigInteger j#)
        j#))
   j))

(defn floor-ev [q r s t x]
  (quot (+ (* q x) r) (+ (* s x) t)))

(defn ncomp [q r s t q2 r2 s2 t2]
  [(+ (* q q2) (* r s2))
   (+ (* q r2) (* r t2))
   (+ (* s q2) (* t s2))
   (+ (* s r2) (* t t2))])

(defn digit [k q r s t n row col]
  (if (> n 0)
    (let [y (floor-ev q r s t 3)]
      (if (== y (floor-ev q r s t 4))
	(let [[q r s t] (ncomp 10 (* -10 y) 0 1 q r s t)]
	  (if (== col 10)
	    (let [row (+ row 10)]
	      (printf "\t:%d\n%d" row (fixup y))
	      (recur k q r s t (dec n) row 1))
	    (do (printf "%d" (fixup y))
		(recur k q r s t (dec n) row (inc col)))))
	(let [[q r s t] (ncomp q r s t k (* 2 (inc (* 2 k))) 0 (inc (* 2 k)))]
	  (recur (inc k) q r s t n row col))))
    (printf "%s\t:%d\n" (apply str (repeat (- 10 col) " ")) (+ row col))))

(defn -main [& args]
  (let [n (try (Integer/parseInt (first args))
               (catch NumberFormatException e 27))]
    (digit 1 (bigint 1) (bigint 0) (bigint 0) (bigint 1) n 0 0))
  (flush))
