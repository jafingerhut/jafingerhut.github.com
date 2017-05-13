Transducers were introduced with Clojure 1.7

[Recording](https://www.youtube.com/watch?v=6mTbuzafcII) of Rich
Hickey's Strange Loop talk on transducers.

From Timothy Baldridge:

But at a basic level transducers are simply a way of expressing
transformation pipelines separate from the method in which the output
collection is created

I have done some video tutorials on them
[here](https://www.youtube.com/watch?v=WkHdqg_DBBs).

But the gist is this: assume a mapping reduce looks like this:

```
(defn mapping [f coll]
  (reduce (fn [acc i]
            (conj acc (f i)))
          []
          coll))
```

Then `(mapping inc [1 2 3]) => [2 3 4]`

Transducers in a nut-shell:

```
(defn map [f coll]
  (reduce (fn [acc i]
            (conj acc (f i)))
          []
          coll))

;; refactor to pull out reduce
(defn mapping [f]
  (fn [acc i]
    (conj acc (f i)))

(reduce (mapping inc) [] [1 2 3]) => [2 3 4]

;; But this isn't good enough because our accumulator may not support `conj`
;; Refactor once more:

(defn mapping [f]
  (fn [xf]
    (fn [acc i]
      (xf acc (f i)))))

(reduce ((mapping inc) conj) [] [1 2 3]) => [2 3 4]
(reduce ((mapping inc) +) 0 [1 2 3]) => 9
```

Now you can reuse `mapping` in a dozen different situations and we
never have to define a mapping transducer again.

I'd stick with studying the transducers in clojure.core then move on
to other developer's interpretations of them (including mine).  There
are a lot of things that *can* be done with transducers.  And a lot of
those use cases may or may not be good depending on the context.

But the xform transducers are really advanced.  I'd wait to try to
understand them until you've mastered the ones in clojure.core

The implementations of `map` and `filter` are great places to start.

TBD: What is the `pipeline` function, and where is it defined?  I am
pretty sure it isn't in clojure.core.
