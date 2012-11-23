<!-- -*- mode: markdown ; mode: visual-line ; coding: utf-8 -*- -->

----------------------------------------------------------------------
I haven't yet attempted to pick where these changes should be
described, if at all.  The "Bug Fixes" section would be a logical
choice for them if they are not deemed worthy of calling out for
special attention.

(no ticket) Rich Hickey fix: syntax-quote was walking records, returning maps

CLJ-909 Make LineNumberingPushbackReader's buffer size configurable

CLJ-910 [Patch] Allow for type-hinting the method receiver in memfn

CLJ-1048 add test.generative to Clojure's tests

CLJ-1071 ExceptionInfo does no abstraction

CLJ-1085 clojure.main/repl unconditionally refers REPL utilities into *ns*

CLJ-730 Create test suite for functional fns (e.g. juxt, comp, partial, etc.)
----------------------------------------------------------------------


# Changes to Clojure in Version 1.5

## CONTENTS

<pre>
 1 Deprecated and Removed Features
    1.1 Clojure 1.5 requires Java 1.6 or later
 2 New/Improved Features
    2.1 Reducers
    2.2 Reader Literals improved
    2.3 clojure.core/set-agent-send-executor!, set-agent-send-off-executor!, and send-via
    2.4 New threading macros
    2.5 Column metadata captured by reader
    2.6 gen-class improvements
    2.7 Support added for marker protocols
    2.8 clojure.pprint/print-table output compatible with Emacs Org mode
    2.9 clojure.string/replace and replace-first handle special characters more predictably
    2.10 Set and map constructor functions allow duplicates
    2.11 More functions preserve metadata
    2.12 Documentation improvements
 3 Performance Enhancements
 4 Improved error messages
 5 Improved documentation strings
 6 Bug Fixes
</pre>

## 1 Deprecated and Removed Features

### 1.1 Clojure 1.5 reducers library requires Java 6 or later

The new reducers library (see below) requires Java 6 plus a ForkJoin
library, or Java 7 or later.  Clojure 1.5 can still be compiled and
run with Java 5, but the test suite will not pass due to the lack of
support for ForkJoin.

Note: Andy has confirmed that there are no new occurrences of the word
"deprecated" in the source code diffs from 1.4 to 1.5-beta1.

TBD: Nothing else removed or deprecated from 1.4 to 1.5?


## 2 New and Improved Features

### 2.1 Reducers

TBD: Describe these changes.

TBD: Consider describing the additional dependencies on jsr166y for
Java 1.6, or Fork/Join for Java 1.7 and later.

The following article by Rich Hickey may contain material that could
be used here:

http://clojure.com/blog/2012/05/08/reducers-a-library-and-model-for-collection-processing.html


### 2.2 Reader Literals improved

TBD: Describe the enhancements from the two patches below.

* [CLJ-1034](http://dev.clojure.org/jira/browse/CLJ-1034)
  "Conflicting data-read mapping" triggered when the same data_readers.clj is on the classpath twice
* [CLJ-927](http://dev.clojure.org/jira/browse/CLJ-927)
  default tagged literal reader


### 2.3 clojure.core/set-agent-send-executor!, set-agent-send-off-executor!, and send-via

TBD: Describe these new functions.


### 2.4 New threading macros

TBD: Describe these new macros.

    clojure.core/test->
    clojure.core/test->>
    clojure.core/let->
    clojure.core/when->
    clojure.core/when->>

Alan Malloy's message of Nov 15 2012 in this Clojure Google group
thread could be useful here:

https://groups.google.com/forum/?fromgroups=#!topic/clojure/67JQ7xSUOM4


### 2.5 Column metadata captured by reader

TBD: Describe these changes.

* [CLJ-960](http://dev.clojure.org/jira/browse/CLJ-960)
  Capture :column metadata (needed for ClojureScript source maps)


### 2.6 gen-class improvements

TBD: Describe these changes.

* [CLJ-745](http://dev.clojure.org/jira/browse/CLJ-745)
  Allow protected final methods to be exposed via exposes-methods
* [CLJ-948](http://dev.clojure.org/jira/browse/CLJ-948)
  support annotations for constructors generated via gen-class


### 2.7 Support added for marker protocols

TBD: Describe these changes.

* [CLJ-966](http://dev.clojure.org/jira/browse/CLJ-966)
  Add support for marker protocols


### 2.8 clojure.pprint/print-table output compatible with Emacs Org mode

For the convenience of those that use Emacs Org mode,
`clojure.pprint/print-table` now prints tables in the form used by
that mode.  Emacs Org mode has features to make it easy to edit such
tables, and even to do spreadsheet-like calculations on their
contents.  See the [Org mode documentation on
tables](http://orgmode.org/manual/Tables.html) for details.

    user=> (clojure.pprint/print-table [:name :initial-impression]
               [{:name "Rich" :initial-impression "rock star"}
                {:name "Andy" :initial-impression "engineer"}])
    | :name | :initial-impression |
    |-------+---------------------|
    |  Rich |           rock star |
    |  Andy |            engineer |


### 2.9 clojure.string/replace and replace-first handle special characters more predictably

`clojure.string/replace` and `clojure.string/replace-first` are now
consistent in the way that they handle the replacement strings: all
characters in the replacement strings are treated literally, including
backslash and dollar sign characters.

    (require '[clojure.string :as s])

    (s/replace-first "munge.this" "." "$")
    ;=> "munge$this"

    (s/replace "/my/home/dir" #"/" (fn [s] "\\"))
    ;=> "\\my\\home\\dir"

There is one exception, which is described in the doc strings.  If you
call these functions with a regex to search for and a string as the
replacement, then dollar sign and backslash characters in the
replacement string are treated specially.  Occurrences of `$1` in the
replacement string are replaced with the string that matched the first
parenthesized subexpression of the regex, occurrences of `$2` are
replaced with the match of the second parenthesized subexpression,
etc.

    (s/replace "x12, b4" #"([a-z]+)([0-9]+)" "$1 <- $2")
    ;=> "x <- 12, b <- 4"

Individual occurrences of `$` or `\` in the replacement string that
you wish to be treated literally can be escaped by prefixing them with
a `\`.  If you wish your replacement string to be treated literally
and its contents are unknown to you at compile time (or you don't wish
to tarnish your constant string with lots of backslashes), you can use
the new function `clojure.string/re-quote-replacement` to do the
necessary escaping of special characters for you.

    (s/replace "x12, b4" #"([a-z]+)([0-9]+)"
                         (s/re-quote-replacement "$1 <- $2"))
    ;=> "$1 <- $2, $1 <- $2"


### 2.10 Set and map constructor functions allow duplicates

All of the functions that construct sets such as `set` and
`sorted-set` allow duplicate elements to appear in their arguments,
and they are documented to treat this case as if by repeated uses of
`conj`.

Similarly, all map constructor functions such as `hash-map`,
`array-map`, and `sorted-map` allow duplicate keys, and are documented
to treat this case as if by repeated uses of `assoc`.

As before, literal sets, e.g. `#{1 2 3}`, do not allow duplicate
elements, and while elements can be expressions evaluated at run time
such as `#{(inc x) (dec y)}`, this leads to a check for duplicates at
run time whenever the set needs to be constructed, throwing an
exception if any duplicates are found.

Similarly, literal maps do not allow duplicate keys.  New to Clojure
1.5 is a performance optimization: if all keys are compile time
constants but one or more values are expressions requiring evaluation
at run time, duplicate keys are checked for once at compile time only,
not each time a map is constructed at run time.

* [CLJ-1065](http://dev.clojure.org/jira/browse/CLJ-1065)
  Allow duplicate set elements and map keys for all set and map constructors


### 2.11 More functions preserve metadata

Most functions that take a collection and return a "modified" version
of that collection preserve the metadata that was on the input
collection, e.g. `conj`, `assoc`, `dissoc`, etc.  One notable
exception was `into`, which would return a collection with metadata
`nil` for several common types of input collections.

Now the functions `into`, `select-keys`, `clojure.set/project`, and
`clojure.set/rename` return collections with the same metadata as
their input collections.


### 2.12 Documentation improvements

TBD: Describe these changes, or just move them in the big list of bug
fixes.

* [CLJ-963](http://dev.clojure.org/jira/browse/CLJ-963)
  Support pretty printing namespace declarations under code-dispatch
* [CLJ-902](http://dev.clojure.org/jira/browse/CLJ-902)
  doc macro broken for namespaces


## 3 Performance Enhancements

* [CLJ-988](http://dev.clojure.org/jira/browse/CLJ-988)
  Calling multimethods from multiple threads now has better parallelism.
* [CLJ-1061](http://dev.clojure.org/jira/browse/CLJ-1061)
  `when-first` now evaluates its expression only once.
* [CLJ-1084](http://dev.clojure.org/jira/browse/CLJ-1084)
  `PersistentVector$ChunkedSeq` now implements `Counted` interface, to avoid some cases where vector elements were being counted by iterating over their elements.
* [CLJ-867](http://dev.clojure.org/jira/browse/CLJ-867)
  Records with same fields and field values, but different types, now usually hash to different values.


## 4 Improved error messages

TBD: Describe these changes in more detail, or is a bullet list enough?

* [CLJ-103](http://dev.clojure.org/jira/browse/CLJ-103)
  Improved if-let error message
* [CLJ-897](http://dev.clojure.org/jira/browse/CLJ-897)
  Don't use destructuring in defrecord/deftype arglists to get a slightly better error message when forgetting to specify the fields vector
* [CLJ-788](http://dev.clojure.org/jira/browse/CLJ-788)
  Add source and line members and getters to CompilerException
* [CLJ-157](http://dev.clojure.org/jira/browse/CLJ-157)
  Better error messages for syntax errors w/ defn and fn
* [CLJ-940](http://dev.clojure.org/jira/browse/CLJ-940)
  Passing a non-sequence to refer :only results in uninformative exception
* [CLJ-1024](http://dev.clojure.org/jira/browse/CLJ-1024)
  Check for invalid varargs/destructuring uses.
* [CLJ-1052](http://dev.clojure.org/jira/browse/CLJ-1052)
  assoc should throw exception if missing val for last key


## 5 Improved documentation strings

* [CLJ-893](http://dev.clojure.org/jira/browse/CLJ-893)
  Document that vec will alias Java arrays
* [CLJ-892](http://dev.clojure.org/jira/browse/CLJ-892)
  Clarify doc strings of sort and sort-by: they will modify Java array arguments
* [CLJ-1019](http://dev.clojure.org/jira/browse/CLJ-1019)
  ns-resolve doc has a typo
* [CLJ-1038](http://dev.clojure.org/jira/browse/CLJ-1038)
  Docstring for deliver doesn't match behavior
* [CLJ-1055](http://dev.clojure.org/jira/browse/CLJ-1055)
  "be come" should be "become"
* [CLJ-917](http://dev.clojure.org/jira/browse/CLJ-917)
  clojure.core/definterface is not included in the API docs


## 6 Bug Fixes

* [CLJ-962](http://dev.clojure.org/jira/browse/CLJ-962)
  Vectors returned by subvec allow access at negative indices
* [CLJ-952](http://dev.clojure.org/jira/browse/CLJ-952)
  bigdec does not properly convert a clojure.lang.BigInt
* [CLJ-975](http://dev.clojure.org/jira/browse/CLJ-975)
  inconsistent destructuring behaviour when using nested maps 
* [CLJ-954](http://dev.clojure.org/jira/browse/CLJ-954)
  TAP support in clojure.test.tap Needs Updating
* [CLJ-881](http://dev.clojure.org/jira/browse/CLJ-881)
  exception when cl-format is given some ~f directive/value combinations
* [CLJ-763](http://dev.clojure.org/jira/browse/CLJ-763)
  Do not check for duplicates in destructuring map creation
* [CLJ-667](http://dev.clojure.org/jira/browse/CLJ-667)
  Allow loops fully nested in catch/finally
* [CLJ-768](http://dev.clojure.org/jira/browse/CLJ-768)
  cl-format bug in ~f formatting
* [CLJ-844](http://dev.clojure.org/jira/browse/CLJ-844)
  NPE calling keyword on map from bean
* [CLJ-934](http://dev.clojure.org/jira/browse/CLJ-934)
  disj! Throws exception when attempting to remove multiple items in one call
* [CLJ-943](http://dev.clojure.org/jira/browse/CLJ-943)
  When load-lib fails, a namespace is still created
* [CLJ-981](http://dev.clojure.org/jira/browse/CLJ-981)
  clojure.set/rename-keys deletes keys when there's a collision
* [CLJ-961](http://dev.clojure.org/jira/browse/CLJ-961)
  with-redefs loses a Var's root binding if the Var is thread-bound
* [CLJ-1032](http://dev.clojure.org/jira/browse/CLJ-1032)
  seque leaks threads from the send-off pool
* [CLJ-1041](http://dev.clojure.org/jira/browse/CLJ-1041)
  reduce-kv on sorted maps should stop on seeing a Reduced value
* [CLJ-1011](http://dev.clojure.org/jira/browse/CLJ-1011)
  clojure.data/diff should cope with null and false values in maps
* [CLJ-977](http://dev.clojure.org/jira/browse/CLJ-977)
  (int \a) returns a value, (long \a) throws an exception
* [CLJ-964](http://dev.clojure.org/jira/browse/CLJ-964)
  test-clojure/rt.clj has undeclared dependency on clojure.set
* [CLJ-923](http://dev.clojure.org/jira/browse/CLJ-923)
  Reading ratios prefixed by + is not working
* [CLJ-1012](http://dev.clojure.org/jira/browse/CLJ-1012)
  partial function should also accept 1 arg (just f)
* [CLJ-932](http://dev.clojure.org/jira/browse/CLJ-932)
  contains? Should throw exception on non-keyed collections
* [CLJ-757](http://dev.clojure.org/jira/browse/CLJ-757)
  Empty transient maps/sets return wrong value for .contains
* [CLJ-828](http://dev.clojure.org/jira/browse/CLJ-828)
  clojure.core/bases returns a cons when passed a class and a Java array when passed an interface
* [CLJ-1062](http://dev.clojure.org/jira/browse/CLJ-1062)
  CLJ-940 breaks compilation of namespaces that don't have any public functions
* [CLJ-1070](http://dev.clojure.org/jira/browse/CLJ-1070)
  PersistentQueue's hash function does not match its equality
* [CLJ-987](http://dev.clojure.org/jira/browse/CLJ-987)
  pprint doesn't flush the underlying stream

