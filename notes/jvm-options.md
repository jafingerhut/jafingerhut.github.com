# JVM command line options

## Stack traces

This is simply a list of JVM command line options I have found to be
useful at various times.  It is in no way a complete list of options,
nor are they necessarily the most important ones for any given
purpose.

* `-XX:-OmitStackTraceInFastThrow`

When given, prevents stack traces from being optimized away.  I have
heard that many people who run JVMs for deployed production software
often use this option, just in case some difficult-to-reproduce
exception is thrown, so that as much information about the cause of
that exception is preserved as possible.

More details: https://blog.dripstat.com/java-exception-stacktraces-in-production/


## Options affecting JIT

There are many.  Chapter 4 "Working with the JIT Compiler" of the book
"Java Performance: The Definitive Guide" by Scott Oaks I have read,
and found to explain quite a few of these options very well.

* [The book as a whole](https://www.oreilly.com/library/view/java-performance-the/9781449363512/)
* [Chapter 4](https://www.oreilly.com/library/view/java-performance-the/9781449363512/ch04.html)

* `-XX:+PrintCompilation` - false by default.  When the option is
  given, details about when code is JIT compiled is printed to the
  output.
