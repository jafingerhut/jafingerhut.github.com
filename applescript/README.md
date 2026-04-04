To enable running an AppleScript on a macOS system when a particular
key combination is typed:

+ Run the `Automator` application
+ Create a new "Quick Action"
+ Make sure it receives "no input" at all programs, i.e.
  + Near top of window, after text "Workflow receives current", click
    popup menu and choose "no input".  In the popup menu to the right
    of that one, choose "any application"
+ Double-click "Run Apple Script" in the second column of choices at
  left of window.  This should cause a box titled "Run AppleScript" to
  appear in the main panel.
+ Replace text "(* Your script goes here *)" with your AppleScript code.
+ Save with a name you will know what it means.

+ Now go to System Settings > Keyboard > Keyboard Shortcuts.
+ Select Services from the sidebar.
+ Find your service, perhaps under a top-level item called "General".
+ Add a shortcut by double clicking "(none)" and typing the key
  combination you wish to trigger running the script.

I did not need to do the following step on macOS Sonoma 14.3.1, but am
recording it here in case it becomes useful to know:

+ Finally go to System Settings > Privacy & Security > Privacy >
  Accessibility and add Automator and the preferred app to run the
  shortcut.

I learned how to do this from an answer here:

+ https://apple.stackexchange.com/questions/175215/how-do-i-assign-a-keyboard-shortcut-to-an-applescript-i-wrote
