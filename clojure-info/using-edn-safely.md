<!-- -*- mode: markdown ; mode: visual-line ; coding: utf-8 -*- -->

# Using EDN safely in Clojure

## CONTENTS

<pre>
 1 Purpose of this document
 2 Executive summary
 3 edn capabilities and limitations
 4 Vulnerabilities in clojure.core's read and read-string
 5 Other data interchange formats
 6 Open questions
</pre>

# Purpose of this document

Clojure developers have often used `clojure.core`'s `pr` and other
similar printing functions for writing Clojure data, and `read` or
`read-string` to read it.  For example, Leiningen's `project.clj`
files are simply Clojure lists whose elements are symbols, keywords,
strings, vectors, and maps.

A subset of the data that can be written and read in this way is a
format called [edn](https://github.com/edn-format/edn), Extensible
Data Notation.

This note explains how to produce data in edn format from a Clojure
program, and how to safely read data in edn format, even if it comes
from untrusted sources.

Motivation: In Clojure 1.4 and earlier, using `clojure.core`'s `read`
or `read-string` to read such data from untrusted sources could leave
your application open to attack.


# Executive summary

To create a string in edn format of one or more values `x1, x2, ...,`
you can do so with:

    (defn pr-edn-str [& xs]
      (binding [*print-length nil
                *print-dup* nil
                *print-level* nil
                *print-readably* true]
        (apply pr-str xs)))

    (pr-edn-str x1 x2 ...)

You can define `pr-edn` the same way, except use `pr` instead of
`pr-str` in its definition, and it will print the objects to `*out*`.

    (binding [*out* w]
        (pr-edn x1 x2 ...))

can then be used to print to any writer `w`.

To read a single object in edn format from a string, use
`clojure.edn/read-string`

    (ns my.name.space
      (:require [clojure.edn :as edn]))

    ...

    (edn/read-string my-string)

You will most likely want to wrap that in a try/catch block to handle
any exceptions that occur due to errors in the format of `my-string`,
especially if it is from an untrusted source.

To read from a file, TBD: give short example


TBD: Even when using `clojure.edn/read` and `read-string`,
security-conscious developers should be aware of data readers
(introduced in Clojure 1.4 -- see the documentation for symbols
[`default-data-readers`](http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/default-data-readers)
and
[`*data-readers*`](http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/*data-readers*)).
Ensure that all tags you wish to support have a read handler defined
for them, and that you trust the behavior of those read handler
functions.

The `default-data-readers` supplied with Clojure should be safe
(i.e. it is considered a serious bug if they are not).  The safety of
any other data reader functions you use is up to you.

TBD: Describe what library authors should do to make their code safe,
if they don't need the capabilities of `clojure.core`'s `read*`
functions.  Give examples of converting multiple-argument
`clojure.core/read` calls to `clojure.edn/read` calls with the correct
options.

TBD: Include directions on using the new `clojure.read.eval=unknown`
system property that may help find calls to `clojure.core`'s `read*`
functions that do not explicitly bind `*read-eval*` to `true` or
`false`.  Even with this, it is strongly recommended that all Clojure
developers search through their code for all calls to `clojure.core`'s
`read*` functions, change them to `clojure.edn` versions if they can.
If not, bind `*read-eval*` to `false` immediately around any calls to
`clojure.core` `read*` functions if you can, and pray that you are
safe.  Send a message to the Clojure or Clojure Dev Google groups
explaining why you believe `clojure.edn` will not work for you, and
perhaps someone can help you figure out how to use them.

TBD: Can `pprint` be used to write out data restricted to edn format?
If so, what are the bindings to dynamic vars that are needed to
restrict its behavior?  `pprint` can be nicer on human eyes for saved
data files, although they require more bits for the extra white space.


# edn capabilities and limitations

Many Clojure values can be written and read using the method in the
previous section, but not all.

The following types of data are all supported for printing and reading
(taken from the [edn README](https://github.com/edn-format/edn))

* `nil`, `true`, `false`
* characters: \a \b \c \newline \return
* "strings in double quotes, may span multiple lines\n"
* symbols
* keywords
* integers.  Note that since `*print-dup*` is `nil` when printing
  above, if you print out and read back `java.lang.Integer`, `Short`,
  `Byte`, etc. types and read them back, they will become type
  `java.lang.Long` or `clojure.lang.BigInt`.  If you need other
  integer types in the resulting data you read, you will need to
  create your own data reader tags and functions for them.
* floating point numbers.  Similarly as for integers, values read by
  `clojure.edn/read*` will come back as `java.lang.Double`'s even if
  they were originally `Float`'s, in the absence of new data reader
  tags and functions.
* lists of values surrounded by ( )
* vectors of values surrounded by [ ]
* maps from keys to values surrounded by { }
* sets of values surrounded by #{ }
* tagged elements for instants in time tagged with #inst, and UUIDs
  tagged with #uuid.
* Other tagged elements, as long as you define a data reader function
  for them and add the tag and data reader function as a key/value
  pair to `*data-readers*`

The following types of data can be printed using the methods in the
previous section, but cannot be read with `clojure.edn/read*`, at
least not with Clojure 1.5.0-RC15:

* regex patterns, e.g. `#"foo|bar"` or values returned by the function
  `re-pattern`.
* Clojure records defined with `defrecord1, unless you define a data
  reader for them.  TBD: Give an example of how to do so.
* Clojure types defined with `deftype`, unless you define a print
  method and a data reader for them.  TBD: Give an example of how to
  do so.
* TBD: What else?


# Vulnerabilities in clojure.core's read and read-string

Clojure's `clojure.core/read` and `read-string` were designed for
reading Clojure code and data from trusted sources.

Using them to read data from untrusted sources when `*read-eval*` is
bound to `true` (its default value) makes it trivial for an attacker
to execute arbitrary code on your system:

    user=> (read-string "#=(clojure.java.shell/sh \"echo\" \"hi\")")
    {:exit 0, :out "hi\n", :err ""}

More destructive examples that remove all of your files, copy them to
someone else's computer over the Internet, install Trojans, etc., are
simple modifications of the example above.

Less obviously, in Clojure 1.4 and earlier, even binding `*read-eval*`
to `false` and using `read` on untrusted data can be dangerous.  An
attacker can cause any Java constructors or class static
initialization code to be executed:

    (defn read-string-unsafely [s]
      (binding [*read-eval* false]
        (read-string s)))

    ;; This causes a socket to be opened, as long as the JVM
    ;; sandboxing allows it.
    (read-string-unsafely "#java.net.Socket[\"www.google.com\" 80]")

    ;; This causes precious-file.txt to be created if it doesn't
    ;; exist, or if it does exist, its contents will be erased (given
    ;; appropriate JVM sandboxing permissions, and underlying OS file
    ;; permissions).
    (read-string-unsafely "#java.io.FileWriter[\"precious-file.txt\"]")

    ;; Memory allocation is trivial to cause with large files or
    ;; strings, but constructors make it easy to allocate large
    ;; amounts of memory with only short input data, easily exhausting
    ;; your heap space.
    (read-string-unsafely "#java.util.ArrayList[10000000]")

While this particular issue has been changed in Clojure 1.5 (the
`read-string-unsafely` calls above will throw an exception instead of
calling the constructors), it is still strongly recommended that no
one use `clojure.core/read` or `read-string` for reading data from
untrusted sources.

    Rich Hickey on #clojure IRC channel: Feb 5 2013

    "as long as the reader has a mode that's unsafe, you shouldn't be
    using it for internet data. e.g. there was (and is) a hole around
    record literals not being governed by *read-eval*.  The only safe
    thing is something that does not have that possibility at all,
    e.g. read-edn.  People will *have to* switch in order to be safe.
    We need to move people off read ASAP, not assuage them with
    defaults"

    Source: http://clojure-log.n01se.net/date/2013-02-05.html


# Other data interchange formats

There are many data serialization formats: XML, JSON, YAML, and ASN.1,
to name a few (see the [Wikipedia page comparing data serialization
formats]
(http://en.wikipedia.org/wiki/Comparison_of_data_serialization_formats)
for a few more).

JSON interchange can be done with many different libraries, including
[`clojure.data.json`](http://dev.clojure.org/jira/browse/DJSON) and
[Cheshire](https://github.com/dakrone/cheshire).

XML interchange can also be done with many libraries, including
[`clojure.data.xml`](http://dev.clojure.org/jira/browse/DXML).


# Open questions

Is this the current edn spec? https://github.com/edn-format/edn

There are several things that `clojure.edn/read*` can read in Clojure
1.5.0-RC15 that are not in that spec.  For example:

* integers
** octal integers with a leading 0
** hexadecimal integers with a leading 0x or 0X
* ratios, e.g. 5/4
* escape sequences within strings
** \uXXXX UTF-16 code units in strings
** \DDD octal characters in strings
* characters
** \oDDD octal characters
** \uXXXX UTF-16 code unit characters
* metadata

Is it the plan to add those to the edn spec some time?
