# Introduction

The solution of some computational problems where a sequence of values
S is given as input, and the goal is to compute a function like
Clojure's (reduce f S)


Examples:

+ W-bit wide unsigned integers with wraparound + (maybe also wraparound *)
+ arbitrary precision integers with + or *
+ maybe: integers in range [-X, +Y] with + that 'clamps' at that same range.
+ strings with concatenation operation

+ 'compose' operator over turtle graph-like actions such as "move
  forward X", "move backward X", "rotate left X", "rotate right X",
  that I believe that as long as all you want to know is the final
  position and facing-direction relatve to the starting position and
  facing-direction, can always compose into a fixed length sequence of
  operations "turn left/right X, move forward Y, turn left/right Z"
  for some values X, Y, and Z that depend upon the sequence.  Note: If
  you care about other things, such as the total length of path
  traversed, or the area of the minimal convex enclosure of the entire
  path traversed, the answer might be different.

+ 'update' operator on the state of some pseudo-random number
  generator.  This one seems unlikely to be possible to
  divide-and-conquer parallelize like many other examples here.  How
  can we mathematically characterize the difference between 'update'
  and the other operations considered?

+ "divide string into words" problem that Guy Steele discussed in his
  talk TBD

TBD: Read this and see if it answers any of my questions, or has other
useful examples:
http://xahlee.info/comp/Guy_Steele_parallel_computing.html

Talk by Guy Steele, "How to Think about Parallel Programming: Not!"
https://www.infoq.com/presentations/Thinking-Parallel-Programming/

The relevant part of that talk for this article starts 29 minutes from
the beginning of the recording.  Before that he is mainly giving
anecdotes about "the worst program I ever wrote", describing
machine-specific tricks he used on older computers, which he used
because they had such small storage compared to modern computers.

He gives an example algorithm for splitting a string into words,
separated by white space, which is interesting in that it is very
straightforward to do sequentially, and it only requires a little bit
of creative thinking to figure out how to create a data structure that
represents partial solutions for a substring, and an associative
"merge" operation that can be used to turn 2 smaller partial solutions
into one larger partial solution.

Associativity is a big deal in his presentation.

About 57 minutes from the beginning he advises looking for ways to use
this approach on other problems, with a tiny bit of guidance on the
kind of solutions that work.

At time 1:00:47 from beginning he starts talking about a slide titled
"Another Big Idea".  I would love to see an example or two of the
general approach described there.

At time 1:02:34 he begins a slide "Automatic Divide-and-Conquer Code",
which I will include the full text here for quicker reference:

    If you can construct _two_ sequential versions of a function that
    is a homomorphism on lists, one that operates left-to-right and
    one right-to-left, then there is a technique for constructing a
    divide-and-conquer version automatically.

        K. Morita, A. Morihata, K. Matsuzaki, Z. Hu, and M. Takeichi,
        "Automatic inversion generates divide-and-conquer parallel
        programs", Proc. 2007 ACM SIGPLAN PLDI, 146-155.

    Just derive a weak right inverse function and then apply the Third
    Homomorphism Theorem.  See -- its' easy!

    There is an analogous result for tree homomorphisms.

        A. Morihata, K. Matsuzaki, Z. Hu, and M. Takeichi, "The third
        homomorphism theorem on trees: Downward and upward lead to
        divide-and-conquer", Proc. 2009 ACM SIGPLAN-SIGACT POPL,
        177-185.

    Full disclosure: the authors of these papers were members of a
    research group at the University of Tokyo that has had a
    collaborative research agreement with the Programming Language
    Research group at Sun Microsystems Laboratories.

On the next slide, he hints that "Parallel prefix is an important
related concept".  Where can one find more details about that?

User-defined monoids!  Monoids are pretty simple to describe: a set of
values with a 2-input function defined on them, with the restriction
that the function is closed under the set (i.e. given any 2 elements
of the set, the function returns an element of the set), the function
is associative, and there is an identity element of the set.  Examples:

+ integers with function + and identity 0
+ integers with function * and identity 1
+ strings with function concatenate and identity element of the empty
  string
+ two dimensional square matrices on integers or real numbers with
  function "multiply two matrices" and identity element of the
  identity matrix

A semigroup is similar to a monoid, except a semigroup has the 2-input
function that is associative, but need not have an identity element.
Most of what Guy Steele talked about seems to work for parallelizing a
reduce/fold on the function of a semigroup, and does not need an
identity element (as far as I can see, anyway).

Creative catamorphisms!  I checked the Wikipedia page for
catamorphisms, but it did not give the kinds of examples that would
help me see how these are useful for divide-and-conquer parallel
algorithms.
