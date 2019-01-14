# Property-based testing

This style of software testing is known by multiple names, perhaps
begun to be popularized by the
[QuickCheck](https://en.wikipedia.org/wiki/QuickCheck) library,
created in 1999:

* property-based testing
* specification-based testing
* generative testing (this term may have been originated in the
  Clojure development community, where the
  [test.check](https://github.com/clojure/test.check) and
  [test.generative](https://github.com/clojure/test.generative)
  libraries were inspired by QuickCheck)
* constrained random testing (this name is probably more often used in
  computer hardware testing)

I am aware of at least the property-based and constrained random parts
of this technique used in hardware testing at least a decade earlier
than QuickCheck was created, but it isn't clear to me where to find
references to articles or books on this (please let me know if you
know of any).  QuickCheck is the first place I have heard of the idea
of automatically _shrinking_ test cases that demonstrate bugs, to find
a shorter/smaller test case that also does so.


# Lecture videos

John Hughes, "Testing the Hard Stuff and Staying Sane", Clojure/West
Conference, March 2014,
[[YouTube](https://www.youtube.com/watch?v=zi0rHwfiX1Q&t=44s)]
[[transcript](2014-03-24-john-hughes-testing-the-hard-stuff-and-staying-sane.txt)]

John discusses several examples of finding bugs in stateful systems
implemented in C and Erlang using QuickCheck.  He starts with a simple
C implementation of a bounded FIFO queue of integers.  He then
discusses his experience developing property-based tests for
automotive software.  He ends with an example of finding bugs that
were only found with multiple clients running in parallel, accessing a
database system.


John Hughes, "The Mysteries of Dropbox", Lambda Days, 2016,
[[YouTube](https://www.youtube.com/watch?v=H18vxq-VsCk)]

This is one of the more complicated uses of property-based testing I
have seen, complicated because of the many possible behaviors one can
observe and consider correct when using a file synchronization service
like Dropbox from multiple clients in parallel.


There are more recorded talks by John Hughes, and others that work at
[QuviQ](http://www.quviq.com), on their [seminars
page](http://www.quviq.com/services/seminars).


# Books

This is the only book I am aware of on this topic:

Fred Hebert, "Property-Based Testing with PropEr, Erlang, and Elixir:
Find Bugs Before Your Users Do", 2019,
https://pragprog.com/book/fhproper/property-based-testing-with-proper-erlang-and-elixir


# Research papers

Koen Claessen, John Hughes, "QuickCheck: A Lightweight Tool for Random
Testing of Haskell Programs", Proc. of 5th ACM SIGPLAN International
Conference on Functional Programming (ICFP), 2000, Montreal, Canada,
[http://www.cs.tufts.edu/~nr/cs257/archive/john-hughes/quick.pdf](http://www.cs.tufts.edu/~nr/cs257/archive/john-hughes/quick.pdf)
Also [dl.acm.org](http://dl.acm.org/citation.cfm?doid=351240.351266)

Koen Claessen, John Hughes, "Testing Monadic Code with QuickCheck",
SIGPLAN Notices 37(12): 47-59 (2002)
[www.cse.chalmers.se/~rjmh/Papers/QuickCheckST.ps](www.cse.chalmers.se/~rjmh/Papers/QuickCheckST.ps)

Thomas Arts, John Hughes, Joakim Johansson, Ulf Wiger, "Testing
Telecoms Software with Quviq QuickCheck", Erlang '06 Sep. 16, 2006,
Portland, Oregon, USA,
[http://www.quviq.com/wp-content/uploads/2014/08/erlang001-arts.pdf](http://www.quviq.com/wp-content/uploads/2014/08/erlang001-arts.pdf)

Thomas Arts, Koen Claessen, John Hughes, Hans Svensson, "Testing
implementations of formally verified algorithms", Software Engineering
Research and Practice, 2005, Vasteras, Sweden,
[https://www.researchgate.net/publication/228614445_Testing_implementations_of_formally_verified_algorithms]
(https://www.researchgate.net/publication/228614445_Testing_implementations_of_formally_verified_algorithms)



# Slides from talks, without the talk recording

Thomas Arts, John Hughes, "Erlang/QuickCheck",
[https://pdfs.semanticscholar.org/6062/b371ea5963c38bfbcca96afe00d3ee548de2.pdf](https://pdfs.semanticscholar.org/6062/b371ea5963c38bfbcca96afe00d3ee548de2.pdf)


# Industry

John Hughes started the company [QuviQ](http://www.quviq.com) in order
to sell tools and services to industry for testing software using
these methods.
