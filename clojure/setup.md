# Introduction

These are instructions for starting from a computer with one of these
operating systems:

+ Ubuntu Linux
+ macOS

and install some or all of the following software.  All software
mentioned is open source unless stated otherwise.

+ A Java development kit (JDK)
  + You may never write a line of Java code yourself, but some Java
    runtime environment is required to develop and run Clojure or
    ClojureScript.
+ Clojure CLI tools

There are companion follow-up articles that explain how to set up the
following editor/IDE environments, at least at a basic level, but
enough to have some useful Clojure-specific capabilities.  I recommend
you finish following the instructions in this article before going to
any of them.

+ [Emacs + inf-clojure](setup-gnu-emacs-with-inf-clojure.md): GNU
  Emacs, using the
  [`inf-clojure`](https://github.com/clojure-emacs/inf-clojure)
  package

Prerequisites:

+ You know how to start and use a terminal on a Linux or macOS system
  (e.g. the `Terminal` program in the Utiliities folder inside your
  Applications folder on macOS).
+ You know how to use a text editor to create files.
+ Most software installation steps require Internet access.

There are a couple of things about these instructions that might be
distinctive:

+ They emphasize REPL-Driven Development.
  + One part of that is setting things up to the point where you can
    edit Clojure code, and with the cursor in or just after some
    expression, type one key to send it to a live Clojure REPL session
    and evaluate it (or at most type only a few keys).  No explicit
    copy and paste required by you -- the environment does it for you.
+ (Future planned addition) Describe how to use Cognitect's
  [REBL](https://github.com/cognitect-labs/REBL-distro) -- Read Eval
  Browse Loop
+ They give easy to follow instructions for installing multiple
  versions of JDK on your system.
  + You might not need that now, but it is often useful in the long
    run.

Recommendation: Stuart Halloway's talk ["REPL-Driven
Development"](https://github.com/matthiasn/talk-transcripts/blob/master/Halloway_Stuart/REPLDrivenDevelopment.md)

Perhaps in the future the companion editor/IDE articles may grow to
include others such as the following, but right I only list them here:

+ Emacs plus [CIDER](https://cider.mx)
+ [IntelliJ IDEA](https://www.jetbrains.com/idea/) plus
  [Cursive](https://cursive-ide.com) (free for non-commercial use,
  costs money to use commercially)
+ [Vim](https://www.vim.org) plus
  [vim-fireplace](https://github.com/tpope/vim-fireplace) or
  [vimclojure](https://github.com/vim-scripts/VimClojure)
+ [Visual Studio Code](https://code.visualstudio.com) plus
  [Calva](https://github.com/BetterThanTomorrow/calva)
+ [Atom](https://atom.io) plus
  [Chlorine](https://atom.io/packages/chlorine)

This is not really an editor/IDE, but it is a useful development and
debugging tool for Clojure:

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

The commands `java -version` and `which java` are good ways to verify
which version of the JVM is first in your command path.


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
curl -O https://download.clojure.org/install/linux-install-1.10.1.536.sh
chmod +x linux-install-1.10.1.536.sh
sudo ./linux-install-1.10.1.536.sh
```


## Install Clojure CLI tools on macOS

Here are the [getting started
instructions](https://clojure.org/guides/getting_started#_installation_on_mac_via_homebrew)
on the Clojure web site to install Clojure's CLI tools.


## Troubleshooting

The command `clj -Sdescribe` shows version and other information about
your Clojure CLI tools installation.  There is no command line option
like `-v` or `--version` for the Clojure CLI tools.


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
an alias that enables you to add this command line option when the
`clj` or `clojure` command starts the JVM.

```clojure
# Example deps.edn file contents
{:aliases {
   ;; - start a Clojure/Java Socket REPL on port 50505
   :socket {:jvm-opts ["-Dclojure.server.repl={:port,50505,:accept,clojure.core.server/repl}"]}}}
```

Note: Using commas instead of spaces is a trick that helps avoid some
issues with string quoting/escaping.  It takes advantage of the fact
that commas are treated as whitespace by the Clojure reader.

With the above as your entire `deps.edn` file, you can start Clojure
with this command:

```bash
clojure -A:socket
```

and the Java VM will be started listening on TCP port 50505 for socket
REPL connections.

Note: The keys in the `:aliases` map of your `deps.edn` file have
names that are all up to you.  `:socket` is just an example alias name
here.  You can have as many such keys as you wish, and indicate that
you want to use more than one by providing them one after the other on
the `clojure` command line, e.g. here is a command that indicates that
all of the aliases `:socket`, `:1.9`, and `:rebl-11` should be used:

```bash
clojure -A:socket:1.9:rebl-11
```

Sean Corfield publishes an example `deps.edn` file that defines many
useful aliases in his [dot-clojure
repository](https://github.com/seancorfield/dot-clojure).


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
