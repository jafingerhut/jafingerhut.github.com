# Using a Socket REPL to connect to Clojure from an editor/IDE

Right now this document limits its instructions for editor/IDE to
Emacs only.  I have successfully done this with the Atom editor plus
the Chlorine add-on package, too, and may add instructions for doing
that in the future.

Prerequisites: You know how to start and use a terminal on a Linux or
macOS system (the `Terminal` program in the Utiliities folder inside
your Applications folder on macOS, for example).


## Ubuntu Linux

I have tested these steps starting from a freshly installed Ubuntu
18.04 Desktop Linux system, the amd64 architecture.  I believe similar
steps should work on most other Linux distributions, if you replace
the `sudo apt-get` commands below with whatever works on your Linux
distribution to install GNU Emacs.


### Install a Java VM on Ubuntu Linux

If you want to get started with the fewest steps possible on an Ubuntu
Linux system, you can use this command to install OpenJDK 11:

```bash
$ sudo apt-get install --yes openjdk-11-jre-headless
```

The rest of the instructions in this section are a short list of steps
for installing one particular version of Zulu OpenJDK 11 on your
system.

However, if later you decide you want to try out additional JDK
versions, you can follow these instructions again to install other
versions on the same system, side-by-side with the first JDK you
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

The steps here suggest you create a new directory named `$HOME/jdks`,
and to install some files there.  Customize that directory if you wish
the files to be installed elsewhere on your system.

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

The next commands have different file names depending upon whether you
are running Ubuntu Linux or macOS.  Both sets of instructions are
mingled together here because they are otherwise identical.

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


### Install Clojure on Ubuntu Linux

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

Note: It is possible to install [Leiningen](https://leiningen.org)
and/or [Boot](https://github.com/boot-clj/boot) on the same system
with the Clojure CLI tools, and pick which one to use for each Clojure
project independently.


### Install GNU Emacs on Ubuntu Linux

I have not tried, but perhaps some of the steps below will work for
"enhanced" versions of Emacs that come with some packages and
customizations pre-installed (e.g. [Doom
Emacs](https://github.com/hlissner/doom-emacs) or
[spacemacs](https://www.spacemacs.org)).  I can list at least some
variations here if you know (and have tested, starting from a freshly
installed Ubuntu Linux system) any differences in the instructions for
them.

```bash
$ sudo apt-get install --yes emacs
```


### Install the `inf-clojure` package in Emacs on Ubuntu Linux

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


### Starting a JVM with Clojure listening on a socket REPL connection using the Clojure CLI tool on Ubuntu Linux

All of the setup in the previous sections need only be done once for a
particular computer -- or per user account on a computer, for the
Emacs init file customizations.

The options described in this section can be specified differently
every time you start up a Java VM process.

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


## macOS


### Install a Java VM on macOS

See the section ["Install a Java VM on Ubuntu
Linux"](#install-a-java-vm-on-ubuntu-linux) for one way to install one
JDK on your system, leaving open the option to later have multiple
JDKs installed on your system, and easily choose which one you want to
use independently in each terminal/shell.


### Install Clojure on macOS

Here are the [getting started
instructions](https://clojure.org/guides/getting_started#_installation_on_mac_via_homebrew)
on the Clojure web site to install Clojure's CLI tools.


### Install GNU Emacs on macOS

You can get a copy of GNU Emacs for macOS on the [Emacs for Mac OS
X](https://emacsformacosx.com) site.


### Install the `inf-clojure` package in Emacs on Ubuntu Linux

These instructions for macOS are identical to those for Ubuntu Linux
in [this
section](#install-the-inf-clojure-package-in-emacs-on-ubuntu-linux).


### Starting a JVM with Clojure listening on a socket REPL connection using the Clojure CLI tool on macOS

These instructions for macOS are identical to those for Ubuntu Linux
in [this
section](#starting-a-jvm-with-clojure-listening-on-a-socket-repl-connection-using-the-clojure-cli-tool-on-ubuntu-linux).


## Windows

I do not use Windows, so do not have any detailed steps or
recommendations here.  If you have similarly detailed steps that work,
I would consider adding them here, or linking to them from here.


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
