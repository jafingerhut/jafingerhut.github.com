;;   The Computer Language Benchmarks Game
;;   http://shootout.alioth.debian.org/

;; contributed by Johannes Friestad
;; modified by Marko Kocic
;; modified slightly by Andy Fingerhut so it would compile and run on
;; Clojure 1.2 and 1.3

(ns nbody
  (:gen-class))

(set! *warn-on-reflection* true)

;; Handle slight difference in function name between Clojure 1.2 and
;; 1.3.
(defmacro my-unchecked-inc-int [& args]
  (if (and (== (*clojure-version* :major) 1)
           (== (*clojure-version* :minor) 2))
    `(unchecked-inc ~@args)
    `(unchecked-inc-int ~@args)))

(definterface IBody
  (^String name [])
  (^double mass [])
  (^double x [])
  (^double y [])
  (^double z [])
  (^double vx [])
  (^double vy [])
  (^double vz [])
  (clone [] "returns copy of self")
  (p_BANG_ [^double x ^double y ^double z] "set position")
  (p_dt_BANG_ [^double dt] "update position")
  (v_dt_BANG_ [^double dt other] "update velocity")
  (v_BANG_ [^double vx ^double vy ^double vz] "set velocity")
  (v_PLUS__BANG_ [^double vx ^double vy ^double vz] "add to velocity")
  (energy [nbodies] "energy relative to nbodies"))


(deftype Body [^String name
               ^double mass
               ^{:unsynchronized-mutable true :tag double} x
               ^{:unsynchronized-mutable true :tag double} y
               ^{:unsynchronized-mutable true :tag double} z
               ^{:unsynchronized-mutable true :tag double} vx
               ^{:unsynchronized-mutable true :tag double} vy
               ^{:unsynchronized-mutable true :tag double} vz ]
  IBody
  (name [this] name)
  (x [this] x)
  (y [this] y)
  (z [this] z)
  (vx [this] vx)
  (vy [this] vy)
  (vz [this] vz)
  (mass [this] mass)
  (clone [this] (Body. name mass x y z vx vy vz))

  (p! [this nx ny nz]
    (set! x nx) (set! y ny) (set! z nz)
    this)

  (v! [this nvx nvy nvz]
    (set! vx nvx) (set! vy nvy) (set! vz nvz)
    this)

  (v+! [this dvx dvy dvz]
    (set! vx (+ vx dvx))
    (set! vy (+ vy dvy))
    (set! vz (+ vz dvz)))

  (p-dt! [this dt]
    (set! x (+ x (* dt vx)))
    (set! y (+ y (* dt vy)))
    (set! z (+ z (* dt vz)))
    this )

  (v-dt! [this dt other]
    (let [^Body b other
          dx (- x (.x b))
          dy (- y (.y b))
          dz (- z (.z b))
          dsq (+ (* dx dx)
                (+ (* dy dy)
                  (* dz dz)))
          mag (/ dt (* dsq (Math/sqrt dsq)))
          m1 (- (* (.mass b) mag))
          m2 (* mass mag) ]
      ; own velocity
      (set! vx (+ vx (* dx m1)))
      (set! vy (+ vy (* dy m1)))
      (set! vz (+ vz (* dz m1)))
      ; v of other
      (.v+! b (* dx m2) (* dy m2) (* dz m2))
      this ))

  (energy [this nbodies]
    (let [v-sq (+ (* vx vx) (* vy vy) (* vz vz))
          e (* 1/2 mass v-sq)  ; kinetic energy: 1/2 * m * v^2
          f (fn [e ^Body b]
              (let [dx (- x (.x b))
                    dy (- y (.y b))
                    dz (- z (.z b))
                    dist (Math/sqrt
                           (+ (* dx dx) (* dy dy) (* dz dz))) ]
                (- e (/ (* mass (.mass b))
                       dist)))) ]
      (reduce f e nbodies)))

  Object
  (toString [this]
    (let [fmt " %s mass %.3f\n p: [%.3f %.3f %.3f]\n v: [%.3f %.3f %.3f]\n"]
      (format fmt name mass x y z vx vy vz (type x)))) )

(let [solar-mass (* 4 Math/PI Math/PI)
      days-year 365.24 ]
  (defn mk-body [b-name & args]
    (let [{:keys [x y z vx vy vz mass]} (apply hash-map args) ]
      (Body. (name b-name) (* mass solar-mass) x y z
        (* vx days-year) (* vy days-year) (* vz days-year)))))

; Data for initial state
(def +data+
  (list
    (mk-body :sun
      :x 0.0
      :y 0.0
      :z 0.0
      :vx 0.0
      :vy 0.0
      :vz 0.0
      :mass 1.0 )
    (mk-body :jupiter
      :x 4.84143144246472090e+00
      :y -1.16032004402742839e+00
      :z -1.03622044471123109e-01
      :vx 1.66007664274403694e-03
      :vy 7.69901118419740425e-03
      :vz -6.90460016972063023e-05
      :mass 9.54791938424326609e-04 )
    (mk-body :saturn
      :x 8.34336671824457987e+00
      :y 4.12479856412430479e+00
      :z -4.03523417114321381e-01
      :vx -2.76742510726862411e-03
      :vy 4.99852801234917238e-03
      :vz 2.30417297573763929e-05
      :mass 2.85885980666130812e-04 )
    (mk-body :uranus
      :x 1.28943695621391310e+01
      :y -1.51111514016986312e+01
      :z -2.23307578892655734e-01
      :vx 2.96460137564761618e-03
      :vy 2.37847173959480950e-03
      :vz -2.96589568540237556e-05
      :mass 4.36624404335156298e-05 )
    (mk-body :neptune
      :x 1.53796971148509165e+01
      :y -2.59193146099879641e+01
      :z 1.79258772950371181e-01
      :vx 2.68067772490389322e-03
      :vy 1.62824170038242295e-03
      :vz -9.51592254519715870e-05
      :mass 5.15138902046611451e-05 ) ))


(def bodies)

(defn init-state []
  ;; Initialize state
  ;; clone is a convenience for repeated runs in the REPL.
  (def bodies (into-array Object (map #(.clone ^Body %) +data+)))
  (let [[px py pz] (reduce (fn [[px py pz] ^Body b]
                             (vector
                              (+ px (* (.vx b) (.mass b)))
                              (+ py (* (.vy b) (.mass b)))
                              (+ pz (* (.vz b) (.mass b))) ))
                           [0.0 0.0 0.0]
                           bodies)
        ^Body sun (aget ^objects bodies 0)
        mass (.mass sun) ]
    (.v! sun (/ (- px) mass) (/ (- py) mass) (/ (- pz) mass))))

(defn energy
  ;; Total energy for current state
  ([] (energy bodies)) ; total
  ([bodies]              ; loop
    (if-not bodies 0.0
      (+ (.energy ^Body (first bodies) (next bodies))
        (energy (next bodies))))))

(defn advance [dt]
  ;; Move system one dt timestep forwards
  (let [^objects bodies bodies
        len (int (alength bodies))
        dt (double dt) ]
    (dotimes [i len]
      (let [^Body body (aget bodies i) ]
        ; update velocity
        (loop [j (my-unchecked-inc-int i) ]
          (when (< j len)
            (let [^Body nbody (aget bodies j)]
              (.v-dt! body dt nbody)
              (recur (my-unchecked-inc-int j)))))
        ; update position
        (.p-dt! body dt)))))


(defn usage [exit-code]
  (printf "usage: %s n\n" *file*)
  (printf "    n, a positive integer, is the number of simulation steps to run\n")
  (flush)
  (. System (exit exit-code)))


(defn -main [& args]
  (when (not= (count args) 1)
    (usage 1))
  (def n
       (let [arg (nth args 0)]
         (when (not (re-matches #"^\d+$" arg))
           (usage 1))
         (let [temp (. Integer valueOf arg 10)]
           (when (< temp 1)
             (usage 1))
           temp)))
  (init-state)
  (printf "%.9f\n" (energy))
  (dotimes [_ (int n)]
    (advance 0.01))
  (printf "%.9f\n" (energy))
  (flush))
