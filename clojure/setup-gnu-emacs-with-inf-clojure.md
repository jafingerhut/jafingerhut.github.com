# Install GNU Emacs

See [this article](install-gnu-emacs.md) for instructions.


# Installing the Emacs `inf-clojure` package

These instructions are common for Ubuntu Linux and macOS, after one
has installed GNU Emacs.  They might also work for a GNU Emacs
installation on Windows, but I have not tested this yet.

There are _many_ packages one can install in Emacs.  I will explain
how to install `inf-clojure` and `clojure-mode` here, but the method
described makes it straightforward to see a list of other packages
available, and install the ones you want.

First, set up Emacs to access the collection of packages available on
MELPA (Milkypostman's Emacs Lisp Package Archive).  Instructions are
given here: https://melpa.org/#/getting-started

If you used Emacs to edit your Emacs init file, then one way to ensure
that those changes take effect is to quit and restart Emacs.

```bash
emacs
```

In the Emacs window, get a list of Emacs packages available for
installation by typing the following.  In case you are not familiar
with Emacs abbreviations for key sequences, you can type `M-x` by
typing the Escape key followed by `x`, and `RET` means to type the
Return key:

```
M-x package-list-packages RET
```

Troubleshooting: I have sometimes seen error messages related to GnuPG
(aka GPG) keys and authentication when the `package-list-packages`
command is trying to access a server to get an updated list of
packages.  I am happy to add some instructions here for resolving such
problems, if someone knows a reliable way.  I suspect some of the
problems I have seen may be due to testing on older Ubuntu Linux
versons like 16.04, and/or Emacs versions 24 or older, and are solved
with more recent versions.

In the list of packages that appears, either search for the string
`inf-clojure`, or scroll down the alphabetical list until you find the
line with that package.  With the cursor on that line, type the `i`
key to mark it for installation.

Find the line for the `clojure-mode` package and do the same.  When
you have marked the packages you want to install, type the `x` key to
actually install them.  Confirm the installation, and when it is
complete, type `q` to quit from the package list.

See the main documentation for
[`inf-clojure`](https://github.com/clojure-emacs/inf-clojure) if you
explore the limits of what is described here, and would like to find
out what else it can do.

A widely-used Emacs add-on for Clojure development is
[CIDER](https://cider.mx).  It is definitely possible to install one
of `inf-clojure` and `CIDER`, then make changes to your Emacs setup to
use the other instead, but I do not know exactly what steps are
required to do so.


# Using the Emacs `inf-clojure` package to connect to a socket REPL

When this was written, I was testing with `inf-clojure` version
2.2.0-snapshot, as determined by first installing `inf-clojure` and
then typing `M-x inf-clojure-display-version RET`.

Note that the JVM process that you are connecting to need not be
started from within Emacs.  You can use these steps to connect to a
JVM process that was started before Emacs was started, or to a JVM
process started after Emacs was started, e.g. from a terminal.

You can also use these steps to connect to a JVM process running on
another machine over the network (as long as you are not blocked by
network firewalls that might be in the way, and likely you would want
to use some form of encryption software like a VPN or SSH tunneling to
avoid sending development information over the network in the clear).

In Emacs, enter this:
```
M-x inf-clojure-connect RET
```

At the "host:" prompt at the bottom left of the window, type
`localhost RET` to connect to a JVM process that is running on the
same system as the Emacs process, or use an IP address or machine name
to connect to another machine over the network.

Now you should see a prompt "port:".  Type `50505 RET` to use TCP port
50505, which is the TCP port that the JVM process is listening on if
you followed the example in the previous section.

Now a new Emacs buffer named `*inf-clojure*` should be created
containing a Clojure REPL prompt, most often `user=>`, which indicates
that its current Clojure namespace is `user`.


# Interacting with a socket REPL using Emacs `inf-clojure`

You can type Clojure expressions in the `*inf-clojure*` Emacs buffer
much like you would at any other Clojure REPL prompt, using Emacs key
bindings to do any editing you wish on them before sending them to the
JVM process.  By default, every time you press Return, the current
line of text is sent to the JVM process.  As a quick example, try
typing something short like `(+ 2 3)`, then the Return key, into the
`*inf-clojure*` buffer, and you should see the result 5 shown,
followed by another REPL prompt.

While you can type expressions into the `*inf-clojure*` buffer
directly, many Cloure developers find it preferable to _never_ type
into a REPL session.  Instead, they type everything into a normal text
editor buffer that is saved to a file, either a file of Clojure code
that is part of the project they are working on, or a "scratch" file
intended to store code snippets they are experimenting with.  Such
Clojure expressions that you type require insignificant amounts of
storage, even if you save many years of such history, and having such
a history can be valuable to refer to later.

You may use the editor's normal copy and paste mechanisms to copy
expressions from one buffer, and paste them into the `*inf-clojure*`
buffer.  While certainly possible, it is far more convenient to
configure an easily-typed keystroke to have the effect "select the
Clojure expression just before, or surrounding, the cursor, and copy
and paste it into the REPL buffer, and send it".

Let us set that up now.

TBD
