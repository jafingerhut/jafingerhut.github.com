# Attempts to configure Emacs to use a Clojure LSP


## Try 1, failed

Ubuntu 20.04 desktop Linux system.

Note: I started with the emacs-gtk package PLUS the `.emacs.d` file
installed by my dotfiles repo in
https://github.com/jafingerhut/dotfiles, which is quite a lot of
non-default GNU Emacs configuration.

TODO: If I do not find another successful path, I should try the steps
below again, with an empty `.emacs.d` directory.

Used these instructions to install Emacs lsp-mode:
https://emacs-lsp.github.io/lsp-mode/page/installation/#manually-via-melpa

Tried instructions on this page to install clj-kondo.lsp server:
https://emacs-lsp.github.io/lsp-mode/page/languages/

I tried:

    M-x lsp-install-server

but saw this error in the minibuffer:

    Symbol's function definition is void: -compose


## Try 2, successful

Ubuntu 20.04 desktop Linux system.  I installed Emacs 26.3 via the
command:

```
sudo apt install emacs-gtk
```

(On Ubuntu 18.04 desktop Linux system, the package name was `emacs`,
and it installed Emacs version 25.2.2.)

But _without_ my dotfiles repo, and starting from an empty `.emacs.d`
directory.

I read this page and tried to follow some of the steps there.  Exactly
which ones are detailed further below.

    https://emacs-lsp.github.io/lsp-mode/tutorials/clojure-guide/

I copied the Elisp code in this section:

    https://emacs-lsp.github.io/lsp-mode/tutorials/clojure-guide/#basic-configuration

and added it to my system's file `$HOME/.emacs.d/init.el`.  The exact
text I put in that file is also copied below, in case the contents of
the web page at the link above changes after 2022-Apr-18:

```
(require 'package)
(add-to-list 'package-archives '("melpa" . "http://melpa.org/packages/") t)
(package-initialize)

(setq package-selected-packages '(clojure-mode lsp-mode cider lsp-treemacs flycheck company))

(when (cl-find-if-not #'package-installed-p package-selected-packages)
  (package-refresh-contents)
  (mapc #'package-install package-selected-packages))

(add-hook 'clojure-mode-hook 'lsp)
(add-hook 'clojurescript-mode-hook 'lsp)
(add-hook 'clojurec-mode-hook 'lsp)

(setq gc-cons-threshold (* 100 1024 1024)
      read-process-output-max (* 1024 1024)
      treemacs-space-between-root-nodes nil
      company-minimum-prefix-length 1
      lsp-lens-enable t
      lsp-signature-auto-activate nil
      ; lsp-enable-indentation nil ; uncomment to use cider indentation instead of lsp
      ; lsp-enable-completion-at-point nil ; uncomment to use cider completion instead of lsp
      )
```

Then I ran `emacs &` from a terminal window.

The message buffer showed many, many messages of Emacs downloading
things from elpa.org and melpa.org, and compiling Elisp files, these
Emacs packages had been installed on my system:

```
$ ls ~/.emacs.d/elpa | cat
ace-window-20200606.1259
archives
avy-20220102.805
cfrs-20220129.1149
cider-20220417.951
clojure-mode-20220418.2015
company-20220406.2323
dash-20220417.2250
epl-20180205.2049
f-20220405.1534
flycheck-20220328.1518
gnupg
ht-20210119.741
hydra-20220102.803
lsp-mode-20220419.602
lsp-treemacs-20220328.625
lv-20200507.1518
map-3.2.1
map-3.2.1.signed
markdown-mode-20220406.410
parseclj-20220328.558
parseedn-20220207.1352
pfuture-20211229.1513
pkg-info-20150517.1143
posframe-20220124.859
queue-0.2
queue-0.2.signed
s-20210616.619
seq-2.23
seq-2.23.signed
sesman-20210901.1134
spinner-1.7.4
spinner-1.7.4.signed
treemacs-20220411.1944
```

(34 lines of output, minus 6 that appear not to be packages)

(On Ubuntu 18.04 with Emacs v25.2.2, instead I got a buffer named
`*Warnings*` that appeared containing the message `error: Package
'emacs-26.1' is unavailable.  It appears that one or more of the
packages above does not work with Emacs v25.)

Next I modified the instructions in this section of the web page

    https://emacs-lsp.github.io/lsp-mode/tutorials/clojure-guide/#manually

which suggested setting this variable in order to manually configure
the command that the Emacs LSP client code uses to run an LSP server
process for Clojure:

```
(setq lsp-clojure-custom-server-command '("bash" "-c" "/path/to/clojure-lsp"))
```

I wanted to try out clj-kondo.lsp instead of clojure-lsp, so I
modified that line to the following instead, and added the line below
to my `$HOME/.emacs.d/init.el` file:

```
(setq lsp-clojure-custom-server-command '("java" "-jar" "/home/andy/clj-kondo.lsp-standalone.jar"))
```

Build my slightly modified version of `clj-kondo.lsp` using these commands:

```bash
git clone https://github.com/jafingerhut/clj-kondo.lsp
cd clj-kondo.lsp
git checkout andy-extra-debug
./script/ubuntu-build-setup.sh
bb vscode-server
cp vscode-extension/clj-kondo.lsp-standalone.jar $HOME
```

Get a copy of a repo with some Clojure code in it, that I know has
some warnings found by the `clj-kondo.lsp` LSP server:

```bash
git clone https://github.com/jafingerhut/clojure-cheatsheets
```

Quit Emacs, and start it again so that it reads the edited version of
my `init.el` file.

```
emacs &
```

In the Emacs window, open a particular `.clj` source file:

    C-x C-f clojure-cheatsheets/src/clj-jvm/src/generator/generator.clj

Before that file was opened and displayed within Emacs, I saw the
following multi-line text in the message buffer near the bottom of the
window (some of the lines are only visible if you make the window wide
enough to display the entirety of the longer lines):

```
generator.clj is not part of any project. Select action:

i==>Import project root ~/clojure-cheatsheets/.
I==>Import project by selecting root directory interactively.
.==>Import project at current directory /home/andy/clojure-cheatsheets/src/clj-jvm/src/generator/.
d==>Do not ask again for the current project by adding ~/clojure-cheatsheets/ to lsp-session-folders-blacklist.
D==>Do not ask again for the current project by selecting ignore path interactively.
n==>Do nothing: ask again when opening other files from the current project.
```

I tried pressing the key `.`.

The file `generator.clj` opened in an Emacs buffer.  It ran
`clj-kondo.lsp`, I believe, and the message buffer changed to:

```
LSP :: clojure-lsp:7096 initialized successfully in folder: (/home/andy/clojure-cheatsheets/src/clj-jvm/src/generator)
```

There were several lines in the first page of the file, including
lines 17, 19, and 21, that had an icon similar to ">>" in the left
margin of the window, and those lines had some red squiggly underlines
beneath some of the text of the Clojure source code.

Moving the cursor over one of those underlined sections of the file
caused some popup text to appear, with a message that I believe came
from clj-kondo.lsp.

I selected the Emacs menu item Tools -> Syntax Checking -> Show all
errors, and a new buffer appeared named `*Flycheck errors*`, which
showed several lines of warning messages.  Clicking on any of those
lines caused the other buffer containing `generator.clj` to jump to
the line and column with that warning.


## Try 2b, successful

As a minor variation on Try 2, creating a bash script called
`$HOME/clj-kondo-wrapper.sh` that contained the following:

```
#! /bin/bash

tee $HOME/clj-kondo-stdin.txt | java -jar $HOME/clj-kondo.lsp-standalone.jar | tee $HOME/clj-kondo-stdout.txt
```

and then using this line in the `init.el` file to use that bash
script:

```
(setq lsp-clojure-custom-server-command '("bash" "-c" "/home/andy/clj-kondo-wrapper.sh"))
```

caused all standard input and output of the LSP server process to be
written to these files:

+ `$HOME/clj-kondo-stdin.txt`
+ `$HOME/clj-kondo-stdout.txt`

Very handy when learning what the communication protocol is between
the LSP server and the editor.


## Try 2c, successful

As another minor variation, if your `init.el` file contains NO line to
define a custom LSP server command for Clojure, e.g. by removing all
lines that assign a value to the symbol
`lsp-clojure-custom-server-command` like this one:

```
(setq lsp-clojure-custom-server-command '("java" "-jar" "/home/andy/clj-kondo.lsp-standalone.jar"))
```

then loading a Clojure file will start the default Clojure LSP server,
which on 2022-Apr-19 on my system was put here:

```
$HOME/.emacs.d/.cache/lsp/clojure/clojure-lsp
```

By loading a Clojure file, and then doing `M-x lsp-treemacs-symbols`,
it creates a narrow buffer to the left of the code buffer named "LSP
Symbols".  Double-clicking on a name in that list will jump your
cursor to the Clojure buffer at the definition of that name, e.g. a
Clojure function or macro definition.



## Try 3, successful

This is the same as Try 2, except I am trying to reduce the list of
Emacs packages a little bit.  I am removing the `cider` and `company`
packages.

```
(require 'package)
(add-to-list 'package-archives '("melpa" . "http://melpa.org/packages/") t)
(package-initialize)

(setq package-selected-packages '(clojure-mode lsp-mode lsp-treemacs flycheck))

(when (cl-find-if-not #'package-installed-p package-selected-packages)
  (package-refresh-contents)
  (mapc #'package-install package-selected-packages))

(add-hook 'clojure-mode-hook 'lsp)
(add-hook 'clojurescript-mode-hook 'lsp)
(add-hook 'clojurec-mode-hook 'lsp)

(setq gc-cons-threshold (* 100 1024 1024)
      read-process-output-max (* 1024 1024)
      treemacs-space-between-root-nodes nil
      company-minimum-prefix-length 1
      lsp-lens-enable t
      lsp-signature-auto-activate nil
      ; lsp-enable-indentation nil ; uncomment to use cider indentation instead of lsp
      ; lsp-enable-completion-at-point nil ; uncomment to use cider completion instead of lsp
      )
```

Then I ran `emacs &` from a terminal window.

The message buffer showed many, many messages of Emacs downloading
things from elpa.org and melpa.org, and compiling Elisp files, these
Emacs packages had been installed on my system:

```
$ ls ~/.emacs.d/elpa | cat
ace-window-20200606.1259
archives
avy-20220102.805
cfrs-20220129.1149
clojure-mode-20220418.2015
dash-20220417.2250
epl-20180205.2049
f-20220405.1534
flycheck-20220328.1518
gnupg
ht-20210119.741
hydra-20220102.803
lsp-mode-20220419.602
lsp-treemacs-20220328.625
lv-20200507.1518
markdown-mode-20220406.410
pfuture-20211229.1513
pkg-info-20150517.1143
posframe-20220124.859
s-20210616.619
spinner-1.7.4
spinner-1.7.4.signed
treemacs-20220411.1944
```

(23 lines of output, minus 3 that appear not to be packages)

The rest of the steps from Try 2 above went as well as they did for
Try 2.
