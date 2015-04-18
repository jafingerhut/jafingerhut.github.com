(ns obfuscated-clojure)

;; Please, don't do these things in serious programs.  This is just a
;; collection of tricks, in the spirit of Swearjure [1], but not
;; limited to its criteria of 'no alphanumeric characters', of things
;; that you might not expect the Clojure compiler to do.

;; [1] http://hypirion.com/musings/swearjure


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; From justin_smith:

{~@[:a 0 :b] ~@[1 :c 2]}
;; => {:a 0, :b 1, :c 2}

;; From hyPiRion:
`{~@(range 4) ~@[]}
;; => {0 1, 2 3}

;; hyPiRion: That's how to implement hash-map in swearjure
(def hash-map #([`{~@%& ~@()}](+)))

;; [2:21pm] hellofunk: hyPiRion: what is the point of the (+)
;; [2:21pm] cursivecode left the chat room. (Quit: My Mac has gone to sleep. ZZZzzz…)
;; [2:23pm] hyPiRion: hellofunk: ##({:foo :bar}) isn't legal because you cannot call a map with itself
;; [2:23pm] lazybot: clojure.lang.ArityException: Wrong number of args (0) passed to: PersistentArrayMap
;; [2:24pm] hyPiRion: so ##([{:foo :bar}](+)) is just a way of getting around that, so we can return the map itself
;; [2:24pm] lazybot: ⇒ {:foo :bar}
;; [2:24pm] justin_smith: &(#([{:a 0}] 0))
;; [2:24pm] lazybot: ⇒ {:a 0}
;; [2:24pm] justin_smith: (slightly less obfuscated)
;; [2:24pm] hyPiRion: and (+) is 0 so it'll return the first argument to the list [...]


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; From someone on #clojure IRC channel, demonstrating weirdness of
;; behavior with multiple consecutive uses of #_

;; Comments beginning with ';' are simple.  They do not interact in
;; hard-to-understand ways with other things that come after them.

;; #_ is complex.  It does interact with things that come after it, in
;; ways that require detailed knowledge of how it works.

(def a #_#_#_4 3 2 1)
;; #'user/a
a
;; 1

;; Not sure, but I believe the first #_ discards the 4, the second #_
;; discards the 3, etc.  Or maybe it is the reverse order.

(println #_#_#_#_[3 2 #_1 0] -1 -2 #_-3 -4 -5 -6)
;; -5 -6



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; andyf noticed this while messing with the reader:

;; The numeric expression read after % can be any number, which is
;; then truncated to an integer.

(#(+ %5/2 %1.2) 7 8)
;; 15
;; same as (#(+ %2 %1) 7 8)

(#(vector %2 %-1) 7 8)
;; [8 nil]
(macroexpand '(#(vector %2 %-1) 7 8))
;; ((fn* [p1__1271# p2__1269# & rest__1270#] (vector p2__1269# rest__1270#)) 7 8)

;; I am not quite sure why %-1 does something, but %-2 or %-3
;; etc. cause an error.  Weird corner case.


(#(vector %2 %0.314159e+1) 7 8 9)
;; [8 9]


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; From justin_smith:
((get get get get) [1 2] 1)
;; 2
;; Because (get get get get) returns the last argument, which is the
;; default value that get returns if it cannot find the searched-for
;; key in the map/vector.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TBD: something involving lots of nested ``` and ~ or ~@ and ',
;; perhaps even something in Clojure itself, could be considered
;; obfuscated, but it could be taken to worse extremes easily.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; From dnolen on #clojure IRC, Feb 9 2015:

;; (let [x :foo {a x} {:foo 1}] a)
;; => 1
;; dnolen: learn something new everyday
