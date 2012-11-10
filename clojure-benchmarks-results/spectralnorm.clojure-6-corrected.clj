;; The Computer Language Benchmarks Game
;; http://shootout.alioth.debian.org/
;;
;; contributed by Jim Kannampuzha
;; inspired by Jesse Rosenstock
;; Andy Fingerhut corrected small bug where if input value n was not a
;; multiple of the number of available processors, the program would
;; hang.


(ns spectralnorm
  (:gen-class))

(set! *warn-on-reflection* true)

(defmacro eval-a [ii jj]
  `(let [i# (int ~ii)
         j# (int ~jj)
         n# (unchecked-add i# j#)
         n+1# (unchecked-inc n#)]
     (/ (double 1.0)
        (unchecked-add (bit-shift-right (unchecked-multiply n# n+1#) (int 1))
                       (unchecked-inc i#)))))

(defn multiply-a-v [agent ^doubles v ^doubles av range]
  (let [end (int (second range))]
    (loop [i (int (first range))]
      (if (= i end)
        nil
        (do
          (aset av i (double (areduce v j sum (double 0)
                                      (+ sum (* (eval-a i j) (aget v j))))))
          (recur (unchecked-inc i)))))))

(defn multiply-at-v [agent ^doubles v ^doubles atv range]
  (let [end (int (second range))]
    (loop [i (int (first range))]
      (if (= i end) nil
          (do
            (aset atv i
                  (double (areduce v j sum (double 0)
                   (+ sum (* (eval-a j i) (aget v j))))))
            (recur (unchecked-inc i)))))))

(defn multiply-at-a-v [^doubles v ^doubles tmp ^doubles at-av
                       num-threads workers ranges]
  (dotimes [i num-threads]
    (send (nth workers i) multiply-a-v v tmp (nth ranges i)))
  (apply await workers)
  (dotimes [i num-threads]
    (send (nth workers i) multiply-at-v tmp at-av (nth ranges i)))
  (apply await workers))

(defmacro dot-product [^doubles u ^doubles v]
  `(areduce ~u i# sum# (double 0) (+ sum# (* (aget ~u i#) (aget ~v i#)))))

(defn run-game [size]
  (let [num-threads (int (.availableProcessors (Runtime/getRuntime)))
        workers (vec (repeatedly num-threads #(agent ())))
        chunk-size (int (Math/ceil (/ size num-threads)))
        ranges  (vec (partition 2 1
                                (take (unchecked-inc num-threads)
                                      (iterate #(min (+ chunk-size %) size)
                                               (int 0)))))
        u (double-array size 1.0)
        tmp (double-array size 0.0)
        v (double-array size 0.0)]
    (dotimes [_ 10]
      (multiply-at-a-v u tmp v num-threads workers ranges)
      (multiply-at-a-v v tmp u num-threads workers ranges))
    (let [vbv (dot-product u v)
          vv (dot-product v v)]
      (Math/sqrt (/ vbv vv)))))

(defn -main [& args]
  (let [n (if (empty? args)
            2500
            (Integer/valueOf ^String (first args)))]
    (println (format "%.9f" (run-game n)))
    (shutdown-agents)))
