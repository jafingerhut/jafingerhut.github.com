There are likely some bugs in `memaccess.c` that causes it not to
measure what I was hoping it measures.  Unless it is fixed, I would
ignore its results.

`memaccess2.c` measures _dependent_ data reads on different working set
sizes, using what I believe is a similar method to `lmbench` (but I
have only skimmed the `lmbench` paper, not compared its source code).
