# Install GNU Emacs


## Install GNU Emacs on Ubuntu Linux

```bash
sudo apt-get install --yes emacs
```


## Install GNU Emacs on macOS

You can get a copy of GNU Emacs that is as drag-and-drop easy to
install as any other macOS application on the [Emacs for Mac OS
X](https://emacsformacosx.com) site.

If you wish to start Emacs from the command line on macOS, you could
type this whole command after installing Emacs in your Applications
folder:

```bash
/Applications/Emacs.app/Contents/MacOS/Emacs
```

However, defining a bash alias is one way to enable you to type a much
shorter command, e.g. add a line like this to the file `.bashrc` in
your home directory to enable the command `emacs` to work:

```bash
alias emacs="/Applications/Emacs.app/Contents/MacOS/Emacs"
```


# Brief notes on following Emacs instructions

Emacs documentation traditionally uses some Emacs-specific notation
when describing sequences of keys to type for controlling it.  Here
are the most commonly used ones:

+ `RET` means Return, and is the key often marked Return on most
  keyboards, or perhaps Enter if no key is marked Return.
+ `S-x` means "Shift x", and can be typed by holding down the Shift
  key while typing and releasing the `x` key.
+ `C-x` means "Control x", and can be typed by holding down the
  Control key while typing and releasing the `x` key.
+ `M-x` means "Meta x", and can be entered by typing and releasing the
  Escape key, followed by typing and releasing the `x` key.
  + Usually there is at least one modifier key on your keyboard that
    can be used, too, which depends upon your operating system, and
    perhaps some configuration on your system, your Emacs init file,
    and whether you are running in a virtual machine environment, over
    a telnet/SSH session, etc.
  + Here are some example ways of typing `M-x` without using the
    Escape key that have been tested with the particular versions of
    Emacs described above, and the default settings of the operating
    system.
    + macOS: Hold down either of the Option keys while typing and
      releasing `x`.
    + Ubuntu Linux running in a VirtualBox VM on macOS: Same as macOS.

Combinations of the above are sometimes used, e.g. `C-S-x` is holding
down Control and Shift while typing and releasing `x`.  If the
combination includes Meta, you can type and release the Escape key,
followed by the rest of the key combination, e.g. `C-M-x` can be typed
by this sequence:

+ Type and release the Escape key.
+ Hold down the Control key while typing and releasing `x`.

To quit Emacs, you can:

+ Type `C-x C-c`
+ macOS: Select menu "Emacs", entry "Quit Emacs"
+ Ubuntu Linux: Select menu "File", entry "Quit"

If you are instructed to edit the Emacs initialization file, it is in
your user account home directory, then the directory `.emacs.d`, and
inside of that directory, the file named `init.el`.  You may create
the file if it has not been created for you already.
