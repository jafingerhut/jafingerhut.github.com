At least on Ubuntu 20.04 and 22.04 systems, the tmux package seems to
be installed by default on AWS systems.


Starting tmux server:

```bash
tmux new -sinstall
```

Start processes inside of that pane.

Can detach with keystrokes: `C-b d`

Can reattach with:

```bash
tmux attach -t install
```

The 'install' is a tmux "session name", if I recall the terminology
correctly, of which there can be several running on the same system.
If you only ever run one such tmux session, you can omit the
`-sinstall` and `-t install` command line options, and those commands
will use some default session.

```
C-b "   split current pane into two, with one above the other
C-b %   split current pane into two, with one to the right of the other

C-b q <number>    change to the pane whose number appears on top of it
```
