I was trying to take a small AppleScript script I had, and install it
in such a way that I could choose a key binding for it, no matter
which application was in the foreground.

I tried installing the AppleScript into the Services menu via
Automator, which did install an entry in the menu, but the AppleScript
did not have its desired effect.

Then I found the method described at the link below, and that worked.

    http://dovfrankel.com/post/49510291962/running-applescripts-from-automator

It is also much more convenient if you wish to tweak the AppleScript
later, because you edit the one _called_ by the Automator action, not
the Automator action itself.

Here is the template Automator action AppleScript, copied from the
article above:

    on run {input, parameters}
        run script file "Macintosh HD:Some Directory On Disk:a cool script.applescript"
    end run

The variation below lets you pass the input and parameters arguments
into your scripts from Automator as well:

    on run {input, parameters}
        run script file "path to script like above" with parameters {input, parameters
    end run
