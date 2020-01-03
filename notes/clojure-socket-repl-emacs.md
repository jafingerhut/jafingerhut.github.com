# Introduction

The original purpose of writing this was to give fairly short and
correct instructions for starting from a computer with one of these
freshly installed operating systems:

+ Ubuntu Linux
+ macOS

and install this software, all of which is open source unless stated
otherwise:

+ A Java development kit (JDK)
  + Not only installing a JDK, but doing so in a way that makes it
    straightforward to later install multiple JDK versions side by
    side on the same system, easily switching between them.  Not
    everyone needs this, but sometimes it can be useful to quickly
    experiment with a different JDK version.
+ Clojure CLI tools
+ At least one editor/IDE setup with a few Clojure-specific add-ons:
  + GNU Emacs, but using
    [`inf-clojure`](https://github.com/clojure-emacs/inf-clojure), not
    the more-widely-used Cider

Prerequisites: You know how to start and use a terminal on a Linux or
macOS system (e.g. the `Terminal` program in the Utiliities folder
inside your Applications folder on macOS).

I could imagine this perhaps growing in the future to include other
editor/IDE setups such as the following, but right now they are only
mentioned in this list:

+ Cognitect's [REBL](https://github.com/cognitect-labs/REBL-distro)
  (free for non-commercial use, costs money to use commercially)
+ Emacs plus [Cider](https://cider.mx)
+ [Atom](https://atom.io) plus
  [Chlorine](https://atom.io/packages/chlorine)
+ [IntelliJ IDEA](https://www.jetbrains.com/idea/) plus
  [Cursive](https://cursive-ide.com) (free for non-commercial use,
  costs money to use commercially)

I do not use Windows much, so do not have any detailed steps or
recommendations here.  If you have similarly detailed steps that work,
I would consider adding them here, or linking to them from here.


# Install a Java Development Kit (JDK) on Ubuntu Linux or macOS

If you want to get started with the fewest steps possible on an Ubuntu
Linux system, you can use this command to install OpenJDK 11:

```bash
$ sudo apt-get install --yes openjdk-11-jre-headless
```

The rest of the instructions in this section are a short list of steps
for installing one particular version of Zulu OpenJDK 11 on your
system.

However, if later you decide you want to try out additional JDK
versions, you can follow these instructions again to install other JDK
versions on the same system, side-by-side with the first one you
installed, and easily choose between them in each shell/terminal you
use.

Zulu OpenJDK 11 has a couple of nice properties:

+ It is based upon OpenJDK source code, and is not encumbered with any
  license from Oracle.  Azul releases it under very permissive terms
  (for full legal versions of such notices, do not trust what I say
  here -- see Azul's web site regarding the Zulu Community Edition
  downloads).
+ It includes Java FX libraries (the OpenJFX flavor), which makes it
  more straightforward to run [Cognitect's
  REBL](https://github.com/cognitect-labs/REBL-distro) software with
  it, if you wish.

The steps here suggest that you create a new directory named
`$HOME/jdks`, and to install some files there.  Customize that
directory if you wish the files to be installed elsewhere on your
system.

The particular version of the Zulu OpenJDK 11 given in these commands
was the latest one available as of 2020-Jan-02, but see ["Finding
available versions of Zulu
OpenJDK"](#finding-available-versions-of-zulu-openjdk) below for
finding other versions.

```bash
$ cd $HOME
$ mkdir jdks
$ cd jdks
```

Ubuntu Linux:
```bash
$ curl -O https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-fx-jdk11.0.5-linux_x64.tar.gz
$ tar xzf zulu11.35.15-ca-fx-jdk11.0.5-linux_x64.tar.gz
```

macOS:
```bash
$ curl -O https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-fx-jdk11.0.5-macosx_x64.tar.gz
$ tar xzf zulu11.35.15-ca-fx-jdk11.0.5-macosx_x64.tar.gz
```

Create a text file named `$HOME/jdks/setup-zulu-11.sh` with these
lines:

Ubuntu Linux:
```bash
export JAVA_HOME=${HOME}/jdks/zulu11.35.15-ca-fx-jdk11.0.5-linux_x64
export PATH=${JAVA_HOME}/bin:${PATH}
```

macOS:
```bash
export JAVA_HOME=${HOME}/jdks/zulu11.35.15-ca-fx-jdk11.0.5-macosx_x64
export PATH=${JAVA_HOME}/bin:${PATH}
```

Then in any bash shell where you want that JDK version to be used, you
can execute the command:

```bash
$ source $HOME/jdks/setup-zulu-11.sh
```

You can install multiple JDK versions on a macOS or Linux system, and
switch between them whenever you wish using a `source` command like
the one above.  Simply create a separate bash script like
`setup-zulu-11.sh` for each version, naming each script as you prefer.


# Install Clojure CLI tools

Note: It is possible to install [Leiningen](https://leiningen.org)
and/or [Boot](https://github.com/boot-clj/boot) on the same system
with the Clojure CLI tools, and pick which one to use for each Clojure
project independently.


## Install Clojure CLI tools on Ubuntu Linux

Here are the [getting started
instructions](https://clojure.org/guides/getting_started#_installation_on_linux)
on the Clojure web site to install Clojure's CLI tools.  I have copied
those instructions below for quick reference, plus added the first
`sudo apt-get` command (that command would require modification for
non-Debian-based Linux distributions).

```bash
$ sudo apt-get install --yes curl rlwrap
$ curl -O https://download.clojure.org/install/linux-install-1.10.1.492.sh
$ chmod +x linux-install-1.10.1.492.sh
$ sudo ./linux-install-1.10.1.492.sh
```


## Install Clojure CLI tools on macOS

Here are the [getting started
instructions](https://clojure.org/guides/getting_started#_installation_on_mac_via_homebrew)
on the Clojure web site to install Clojure's CLI tools.


# Install GNU Emacs


## Install GNU Emacs on Ubuntu Linux

```bash
$ sudo apt-get install --yes emacs
```


## Install GNU Emacs on macOS

You can get a copy of GNU Emacs that is as drag-and-drop easy to
install as any other macOS application on the [Emacs for Mac OS
X](https://emacsformacosx.com) site.


# Installing the Emacs `inf-clojure` package

A widely-used Emacs add-on for Clojure development is
[Cider](https://cider.mx).  It is definitely possible to install one
of `inf-clojure` and `Cider`, then make changes to your setup to use
the other instead, but I do not know how many changes are required to
do so.  If you know, please let me know.

These instructions are common for at least Ubuntu Linux and macOS
after one has installed GNU Emacs as described above.  They might also
work for a GNU Emacs installation on Windows, but I have not tested
this.

There are many other packages one could also install in Emacs, but I
will only explain how to install `inf-clojure` here, plus one other
called `clojure-mode`, which enables some color syntax highlighting
that I find indispensable when reading Clojure code.

First, set up Emacs to access the collection of packages available on
MELPA (Milkypostman's Emacs Lisp Package Archive).  Instructions are
given here: https://melpa.org/#/getting-started

If you used Emacs to edit your `$HOME/.emacs` or
`$HOME/.emacs.d/init.el` init file, then one straigtforward way to
ensure that those changes take effect is to quit and restart Emacs,
causing it to read that init file when it starts again.

```bash
$ emacs
```

In the Emacs window, get a list of Emacs packages available for
installation by typing the following (in case you are not familiar
with Emacs abbreviations for key sequences, you can type `M-x` by
typing the Escape key followed by `x`, and `RET` is typing the Return
key):

```
M-x package-list-packages RET
```

In the list of packages that appears, either search for the string
`inf-clojure`, or scroll down the alphabetical list until you find the
line with that package.  With the cursor on that line, type the `i`
key to mark it for installation.

If you wish, find the line for the `clojure-mode` package and do the
same.  When you have marked the packages you want to install, type the
`x` key to actually install them.  Confirm the installation, and when
it is complete, type `q` to quit from the package list.


# Starting a JVM with Clojure listening for a socket REPL connection

The options described in this section can be specified differently
every time you start up a new JVM process.

As described in the Clojure documentation for ["Launching a Socket
Server"](https://clojure.org/reference/repl_and_main#_launching_a_socket_server),
when you start a Java VM process that runs Clojure, some of the
Clojure initialization code examines the Java system properties to see
if they contain one (or more) that indicate that a socket REPL should
be created.  One way to cause this is by giving these command line
options when starting the `java` command:

```
-Dclojure.server.repl="{:port 50505 :accept clojure.core.server/repl}"
```

50505 is an example TCP port number.  At most one running process in
the same system can be listening on the same TCP port number at the
same time.


## Socket REPL using the Clojure CLI tools

Using a `deps.edn` file (either a user-account-wide one in
`$HOME/.clojure/deps.edn`, or a project-specific one), you can create
an alias that adds this command line option when it starts the JVM,
but only when you provide the alias as a command line option when you
run the `clj` or `clojure` commands.  Using commas instead of spaces
is a trick that helps avoid some issues with string quoting/escaping.
It takes advantage of the fact that commas are treated as whitespace
by the Clojure reader.

```clojure
{:aliases {
   ;; - start a Clojure/Java Socket REPL on port 50505
   :socket {:jvm-opts ["-Dclojure.server.repl={:port,50505,:accept,clojure.core.server/repl}"]}}}
```

With the above as your entire `deps.edn` file, or at least with that
`:socket` alias in the map that is the value associated the `:aliases`
key of your `deps.edn` file, you can start Clojure with this command:

```bash
$ clojure -A:socket
```

plus any other aliases or command line options you wish to add, and
the Java VM will be started with the necessary command line options so
that the Java VM process listens on TCP port 50505 for socket REPL
connections.


## Socket REPL using Leiningen

TBD


## Socket REPL details

Detail that you might never need: Only the last such option given on
the Java VM command line is used, overriding any earlier ones.  If for
some reason you wish to run a Java VM listening on multiple TCP ports
for socket REPL connections, you may do so by running Clojure code
like this, calling `start-server` once for each such TCP port you want
to listen on:

```clojure
(require 'clojure.core.server)
(clojure.core.server/start-server {:name "name for this socket"
                                   :port 60606
				   :accept 'clojure.core.server/repl})
```


# Using the Emacs `inf-clojure` package to connect to a socket REPL

When this was written, I was testing with `inf-clojure` version
2.2.0-snapshot, as determined by first installing `inf-clojure` and
then typing `M-x inf-clojure-display-version RET`.

Note that the JVM process that you are connecting to need not be
started from within Emacs.  You can use these steps to connect to a
JVM process that was started before Emacs was started, or from a
terminal after Emacs was started, etc.  You can also use these steps
to connect to a JVM process running on another machine over the
network (as long as you are not blocked by network firewalls that
might be in the way, and likely you would want to use some form of
encryption software like a VPN or SSH tunneling to avoid sending
development information over the network in the clear).

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

You can type Clojure expressions in that Emacs buffer much like you
would at any other Clojure REPL prompt, using Emacs key bindings to do
any editing you wish on them before sending them to the JVM process.
By default, every time you press Return, the current line of text is
sent to the JVM process.  As a quick example, try typing something
short like "(+ 2 3) RET" into the `*inf-clojure*` buffer, and you
should see the result 5 shown, followed by another REPL prompt.

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


# Finding available versions of Zulu OpenJDK

These instructions are for finding a version of a JDK that includes
the Java FX libraries, which makes it more straightforward to run
[Cognitect's REBL](https://github.com/cognitect-labs/REBL-distro)
software with it, if you wish.

+ Go to the [Download Zulu
  Community](https://www.azul.com/downloads/zulu-community) page.
+ Scan down the page a little bit looking for a row of popup menus
  underneath a heading "Zulu Community Downloads", with labels like
  "Java Version:", "Operating System:", "Architecture:", etc.
+ In the "Operating System" popup menu, select your operating system.
+ In the "Java Package" popup menu, select "JDK FX"
+ I picked the Java 11 (LTS) version with a ".tar.gz" file name
  suffix.  In many browsers, right-clicking on the button labeled
  ".tar.gz" and choosing "Copy Link" will get the full link to that
  file,
  e.g. https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-fx-jdk11.0.5-macosx_x64.tar.gz
  on 2020-Jan-02 was the latest Zulu OpenJK 11 version for macOS with
  "JDK FX".
