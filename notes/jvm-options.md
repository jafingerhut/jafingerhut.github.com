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



# Getting information about a running JVM

Show process IDs of JVMs currently running on the system: `jcmd -l`
also: `ps axguwww | grep java`

List what kinds of information `jcmd` can give about a particular JVM,
given its processed ID <pid>: `jcmd <pid> help`

For example, you can show system properties with:
`jcmd <pid> VM.system_properties`

or JVM command line options with: `jcmd <pid> VM.command_line`

Brief version of JVM flags, showing which flags were set on the
 command line, plus some flags that were set directly by the JVM,
 because their value was determined ergonomically: `jcmd <pid>
 VM.flags`

All JVM flags, whether they were specified on the command line or not.
Produced 670 lines of output when run on Ubuntu 18.04.3 with
AdoptOpenJDK 11: `jvm <pid> VM.flags -all`

To see what all flags are set to on a particular platform: `java
-XX:+PrintFlagsFinal -version`
