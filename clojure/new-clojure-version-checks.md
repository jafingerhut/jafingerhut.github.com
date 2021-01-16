# Clojure code cleanliness checks

Below are some ways to help perform some minor nit-picky checks when a
new Clojure release is imminent, e.g. on the release candidate 1
version.


## Are there any new occurrences of reflection that was not in the previous version?

There are many places in the Clojure core library where the Clojure
compiler uses run-time reflection to call the proper Java method.
Many of these have been eliminated via type declarations over the
years, but there are many that remain.  Some will likely remain
forever, because they are in code that is not considered
performance-critical.

It is useful to know if there is some new occurrence of reflection in
the code from one release to another, and scrutinize it carefully to
see if it is intentional, or should be removed.

This repository contains bash scripts that can automate the building
of several versions of Clojure source code, and search the output for
errors, warnings, and reflection warnings.

https://github.com/jafingerhut/clojure-reflection-check


## Are there new public Vars without doc strings, or without `:added` metadata?

Most public Vars in Clojure have a doc string, and metadata with a key
`:added` whose value is a string specifiying a Clojure version,
e.g. `"1.6"`.  There are a few exceptions which will probably remain
exceptions, so rather than reporting all of the several hundred Vars
that did not have these in the previous Clojure release, either, it is
probably better to focus on any new Vars that do not have these
things.

On a Linux or macOS system with `git` and the Clojure CLI tools
installed, these commands will run a small Clojure program that prints
out a list of all public Vars that have no doc string, and a separate
list of all public Vars that have no `:added` metadata.

```bash
git clone https://github.com/jafingerhut/clojure-cheatsheets
cd clojure-cheatsheets/src/clj-jvm
./scripts/clj-meta-checks.sh
```

Then do `diff` between the output files for different versions of
Clojure to see what has changed.  The output files are named
`check-<clojure-version>.txt`.


## Do all of the tests pass on Windows?

The tests included with Clojure itself are run regularly on an
automated build machine with results reported on
https://build.clojure.org

Those tests are run on Linux.  Many people who build modified versions
of Clojure and run the built-in tests use macOS, so the tests are
regularly run there, and usually the results on macOS and Linux are
the same.

Far fewer Clojure developers use Windows regularly.  One of the main
reasons that the built-in Clojure tests might fail on Windows, when
they pass on Linux and macOS, is due to Windows using the two
character sequence `"\r\n"` for a line separator, versus the one
character sequence `"\n"` used on Linux and macOS.

This commit shows one set of changes made to a few Clojure tests that
were failing on Windows, enabling them to pass on Windows, while
continuing to pass on Linux and macOS, using a test helper function
`platform-newlines`.

https://github.com/clojure/clojure/commit/1f2cc45f77bb981f82d36177790477d21d8e9f79

One way to run the tests on Windows is:

+ Install a JDK built for Windows, e.g. from https://adoptopenjdk.net/
+ Install Apache Maven.  Getting it as a ZIP file from
  https://maven.apache.org/ probably enables the easiest steps to
  unpack it on Windows.
+ Get a copy of the Clojure source code onto the Windows machine,
  perhaps by downloading a ZIP file of the version you want, or
  install `git` on the Windows machine in some way.
+ In the top level directory of the `clojure` source code, run the
  commands:
  + `mvn clean`
  + `mvn test`
