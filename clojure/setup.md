# Introduction

These are instructions for starting from a computer with one of these
operating systems:

+ Ubuntu Linux
+ macOS

and install some or all of the following software, all of which is
open source unless stated otherwise:

+ A Java development kit (JDK)
  + You may never write a line of Java code yourself, but some Java
    runtime environment is required to develop and run Clojure or
    ClojureScript.  Having a full JDK installed does not take much
    more disk space, and can be useful if you ever encounter Clojure
    projects that do include Java code that needs to be compiled.
+ Clojure CLI tools
+ At least one editor/IDE with a few Clojure-specific add-ons:
  + GNU Emacs, using the
    [`inf-clojure`](https://github.com/clojure-emacs/inf-clojure)
    package

Prerequisites:

+ You know how to start and use a terminal on a Linux or macOS system
  (e.g. the `Terminal` program in the Utiliities folder inside your
  Applications folder on macOS).
+ You know how to use a text editor to create files.

Perhaps in the future this may grow to include other editors/IDEs such
as the following, but right now we only mention them here:

+ Emacs plus [CIDER](https://cider.mx)
+ [Atom](https://atom.io) plus
  [Chlorine](https://atom.io/packages/chlorine)
+ [IntelliJ IDEA](https://www.jetbrains.com/idea/) plus
  [Cursive](https://cursive-ide.com) (free for non-commercial use,
  costs money to use commercially)

This is not really an editor/IDE, but it is a useful development tool
for Clojure:

+ Cognitect's [REBL](https://github.com/cognitect-labs/REBL-distro)
  (free for non-commercial use, costs money to use commercially)

If you have similarly detailed steps that work for Windows, I would
consider adding them here, or a link to them.


# Install a Java Development Kit (JDK) on Ubuntu Linux or macOS

I suggest that you create a new directory to contain all JDK versions
installed on your system.  The commands below will install one in a
directory named `jdks` in your home directory.  Choose another if you
prefer.

JDK 11 is a solid choice for many purposes as of January 2020.  See
["Installing other JDK versions"](#installing-other-jdk-versions)
below for other versions.

```bash
cd
mkdir jdks
cd jdks
```

Ubuntu Linux:
```bash
sudo apt-get install --yes curl
curl -O https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-fx-jdk11.0.5-linux_x64.tar.gz
tar xzf zulu11.35.15-ca-fx-jdk11.0.5-linux_x64.tar.gz
```

macOS:
```bash
curl -O https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-fx-jdk11.0.5-macosx_x64.tar.gz
tar xzf zulu11.35.15-ca-fx-jdk11.0.5-macosx_x64.tar.gz
```

Create a text file named `setup-zulu-11.sh` in the `jdks` directory
with these lines:

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
source $HOME/jdks/setup-zulu-11.sh
```

If later you want to try out other JDK versions, you can follow
similar steps as shown above, with different `.tar.gz` files
downloaded elsewhere, to install other versions on the same system.

Then you can choose which one to use in a particular shell by entering
a `source` command like the one above.  Simply create a separate bash
script like `setup-zulu-11.sh` for each JDK version on your system,
naming each script as you prefer.  Not everyone needs this capability,
but sometimes it can be useful to quickly experiment with a different
JDK version.

The command `java -version` is a good way to verify which version of
the JVM is first in your command path.


# Install Clojure CLI tools

Note: It is easy to install [Leiningen](https://leiningen.org) and/or
[Boot](https://github.com/boot-clj/boot) on the same system with the
Clojure CLI tools, and pick which one to use for each Clojure project
independently.

TBD: Link to quick introduction about the basic differences between
Leiningen, Boot, and Clojure CLI tools, and how to determine which one
(or ones) a Clojure project uses.


## Install Clojure CLI tools on Ubuntu Linux

Here are the [getting started
instructions](https://clojure.org/guides/getting_started#_installation_on_linux)
on the Clojure web site to install Clojure's CLI tools.  I have copied
those instructions below for quick reference, plus added the first
`sudo apt-get` command (that command would require modification for
non-Debian-based Linux distributions).

```bash
sudo apt-get install --yes curl rlwrap
curl -O https://download.clojure.org/install/linux-install-1.10.1.492.sh
chmod +x linux-install-1.10.1.492.sh
sudo ./linux-install-1.10.1.492.sh
```


## Install Clojure CLI tools on macOS

Here are the [getting started
instructions](https://clojure.org/guides/getting_started#_installation_on_mac_via_homebrew)
on the Clojure web site to install Clojure's CLI tools.


# Install GNU Emacs


## Install GNU Emacs on Ubuntu Linux

```bash
sudo apt-get install --yes emacs
```


## Install GNU Emacs on macOS

You can get a copy of GNU Emacs that is as drag-and-drop easy to
install as any other macOS application on the [Emacs for Mac OS
X](https://emacsformacosx.com) site.


# Installing the Emacs `inf-clojure` package

These instructions are common for Ubuntu Linux and macOS, after one
has installed GNU Emacs.  They might also work for a GNU Emacs
installation on Windows, but I have not tested this.

There are many more packages one could install in Emacs, but I will
only explain how to install `inf-clojure` here, plus one other called
`clojure-mode`, which enables some color syntax highlighting and
auto-indenting that I find indispensable when editing Clojure code.

First, set up Emacs to access the collection of packages available on
MELPA (Milkypostman's Emacs Lisp Package Archive).  Instructions are
given here: https://melpa.org/#/getting-started

If you used Emacs to edit your `$HOME/.emacs` or
`$HOME/.emacs.d/init.el` initialization file, then one straigtforward
way to ensure that those changes take effect is to quit and restart
Emacs.

```bash
emacs
```

In the Emacs window, get a list of Emacs packages available for
installation by typing the following (in case you are not familiar
with Emacs abbreviations for key sequences, you can type `M-x` by
typing the Escape key followed by `x`, and `RET` means to type the
Return key):

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

See the main documentation for
[`inf-clojure`](https://github.com/clojure-emacs/inf-clojure) if you
explore the limits of what is described here, and would like to find
out what else it can do.

A widely-used Emacs add-on for Clojure development is
[CIDER](https://cider.mx).  It is definitely possible to install one
of `inf-clojure` and `CIDER`, then make changes to your Emacs setup to
use the other instead, but I do not know exactly what steps are
required to do so.


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
run the `clj` or `clojure` commands.

```clojure
# Example deps.edn file contents
{:aliases {
   ;; - start a Clojure/Java Socket REPL on port 50505
   :socket {:jvm-opts ["-Dclojure.server.repl={:port,50505,:accept,clojure.core.server/repl}"]}}}
```

Note: Using commas instead of spaces is a trick that helps avoid some
issues with string quoting/escaping.  It takes advantage of the fact
that commas are treated as whitespace by the Clojure reader.

With the above as your entire `deps.edn` file, or at least with that
`:socket` alias in the map that is the value associated the `:aliases`
key of your `deps.edn` file, you can start Clojure with this command:

```bash
clojure -A:socket
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


# Installing other JDK versions

The Zulu OpenJDK 11 recommended above has a couple of nice properties:

+ It is based upon OpenJDK source code, and is not encumbered with any
  license from Oracle.  The company Azul releases it under very
  permissive terms.  For full legal versions of such notices, do not
  rely on what I say here -- see Azul's web site regarding the [Zulu
  Community Edition
  downloads](https://www.azul.com/downloads/zulu-community).
+ It includes Java FX libraries (from OpenJFX source code), which
  makes it easy to run [Cognitect's
  REBL](https://github.com/cognitect-labs/REBL-distro) software.

As of January 2020, JDK 8 is a bit late in its support cycle, but is
still often the version best supported by some Clojure and Java
libraries, so you may find it helpful to install some JDK 8 release as
well as JDK 11.

JDK 8, 11, and 17 are LTS (Long Term Support) releases, supported for
years after they are released.  Other JDK versions are expected to be
supported for only 6 months after they are released.  See [this
artice](https://en.wikipedia.org/wiki/Java_version_history) for more
details.

The instructions below are for finding a version of a JDK from the
company Azul that includes the Java FX libraries.

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
