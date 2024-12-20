# Notes on installing Ubuntu guest VMs in VirtualBox on a macOS or Windows 10/11 host

See also the article [`macos-utm-notes.md`](macos-utm-notes.md) for
notes on installing a guest Linux VM on an Apple Silicon Mac using the
UTM application.


# Install VirtualBox

Get it from https://www.virtualbox.org

I do not recall any difficulties in installing it on Windows 10, 11,
or macOS.  Thankfully the installer asks very few questions while
installing it.


# Finding the Linux installer image you want

Download the `.iso` file that installs the version of Linux that you
are interested in.  An Internet search for terms like these works for
me.  Use "amd64" for a host system with 64-bit Intel/AMD CPU, or
"arm64" for an Apple Silicon Mac.

Example search terms:

+ Ubuntu 20.04 arm64
+ Ubuntu 24.04 amd64

An installer for Desktop Linux leads to fewer steps you need to do in
order to get a GUI Desktop.  As of 2024, I have only been able to find
Server Linux installers for arm64 systems.  It is not difficult to
install that, and then later install the GUI Desktop.


# Creating a new VM

Start VirtualBox.  While there are command line ways to do all of this
with VirtualBox (I believe), I have never used those.  The VirtualBox
GUI takes a few minutes to use when creating a new VM, and installing
the VM takes the computer a while longer.  I estimate around 30 to 45
minutes total time to create a new VM where you install Linux from an
`.iso` file.

A nice thing about VirtualBox is that once you create a VM for the
operating system you want, if you have enough free disk space to keep
around that original VM, it is very quick (30 seconds or less) to
create a copy of that VM, and then install a bunch of software on that
copy.  As long as you leave the original VM there, it will not change,
and you can create copies of it whenever you want to try experimenting
with it.  Did you accidentally mess up the state of that VM's
system-wide configuration files, or install some weird combination of
software that seems to conflict with each other?  You can abandon that
VM image, deleting it whenever you no longer find its contents useful,
and create more clones of the original base OS for further
experiments.

In the VirtualBox GUI window:

+ Click the button "New"
+ In the window that appears, give a unique name to your VM,
  e.g. "Ubuntu 24.04 base OS".
+ Select the location of the `.iso` installer file that you
  downloaded.
+ I prefer to check the box "Skip Unattended Installation", and these
  instructions will assume you are doing so, too.
+ Under "Hardware" choose the amount of RAM, number of virtual CPUs,
  and hard disk space you want.
  + In 2024, I rarely want to create a VM with less than 4 GBytes
    (4096 MBytes) of RAM.  I typically select 4 processors, and 60
    GBytes of disk space.  I do _not_ click the "Pre-allocate Full
    Size" check box, since then it would immediately create a file
    that was 60 GBytes in size.  If you do not check that box,
    VirtualBox creates a disk image file that is only as large as it
    needs to be to store the files currently existing within the VM's
    file system, not the full size it might grow to later.
  + With VirtualBox, changing the RAM available to a VM after you
    create it is quick and easy, either increasing it, or decreasing
    it.  Simply shut down the VM, select the VM in the GUI, click
    "Settings", change the RAM setting, and start the VM image again.
    As long as your host OS has enough free RAM, VirtualBox can use it
    for the running VM image.  Thus, you do not need to think too hard
    when choosing the initial RAM size while creating the VM image.
  + I believe it is possible to increase the disk space allocated to
    the VM later, but I have not personally done so, and you will not
    find instructions to do so here.  It is definitely more steps than
    changing the RAM size later.  It also might be possible to
    decrease it later, but only if you can find and run the proper
    utility programs in the guest VM to shrink its files into a subset
    of the available physical disk space.  It is thus more important
    to think of the maximum disk space you expect to use within the
    VM, _or_ copy files out of it to another system to free up disk
    space during the lifetime of that VM.  Fortunately you can pick a
    large size, and choose the option not to preallocate it all when
    the VM is created.  It might be easier to create a new VM and copy
    files from the old one to the new one vs. increasing the disk
    space available to an old one.
+ Click the "Finish" button.  This closes the window you were working
  on, and a new VM image with the name you gave it has now been
  created.  It does not have the OS installed yet.
+ Select that new image and click on the button "Settings".
+ Click "General".
  + Under the "Advanced" tab, change "Shared Clipboard" to
    "Bidirectional".
+ Click "Display".
  + Under the "Screen" tab, change "Video Memory" from 16 MB to 32 MB.
+ If you want to create a shared folder on your host OS that is
  readable and writable from the guest OS, too, click "Shared Folder".
  + Click on the icon that looks like a folder with a "+" symbol on it.
  + Change "Folder Path" to choose the host OS folder you want to share.
  + Check the box for "Auto-mount".
  + If you want the guest OS to only be able to read this folder, but
    not write to it, check the box for "Read-only".
  + Click "OK" button.
+ Back in the main settings window for the VM image, click the OK
  button.


# Common steps for starting any VM image, including a new installer one

+ Select the VM image and click the "Start" button.
+ If the text is uncomfortably small for reading, select the
  VirtualBox menu item View -> Virtual Screen 1 -> Scale to 200%.
  Adjust the scale choice to your reading comfort.


# Installation steps

These depend upon the particular guest OS that you are installing.

## Ubuntu Server 24.04, 24.10

In the initial boot menu, the default choice "Try or Install Ubuntu"
should be highlighted.  Press return to continue.

Choose language.  See on-screen instructions for moving the choice
around.

Choose keyboard language.

On the page "Choose the type of installation", I leave the default
choice "Ubuntu Server" selected with an "X" as is.  I press return for
selecting "Done" at the bottom of the screen.

Network Configuration: press return for Done.

Proxy address: I do not need one for my home network, so press return
for Done.

Ubuntu archive mirror configuration: Wait a few seconds for it to test
access to the default mirror archive.  Press return for Done, assuming
that it successfully finds a mirror system to connect to.

Guided storage configuration: This can be nerve-wracking for a new
user if you are installing multiple OS's on a physical system, but
here we have created a new virtual disk just for this guest OS's full
use, so we want to allocate all of that virtual disk for this OS's
purposes.  Leave the default "X" checked on "Use an entire disk".

I usually press tab until the cursor is on the "X" next to "Set up
this disk as an LLVM group", and press the space key to uncheck that
box.  Press tab a couple more times to select "Done" near bottom of
screen, then return to proceed.

Storage configuration: Look over the info on the screen if you are
curious, but I just press return to proceed.

A "Confirm destructive action" "window" pops up.  Again, this is where
I get nervious if I am ever installing Linux on a physical system
where I want another OS to remain on a different disk partition, but
VirtualBox is restricting this VM so that it can only see the virtual
disk(s) that VirtualBox created it, and nothing else in the host OS
file system, so confidently proceed by pressing tab to highlight the
"Continue" choice, and press return.

Enter your full name, desired system name, user name, password,
etc. pressing tab to advance through the different boxes.  When ready,
press tab until "Done" is highlighted at the bottom, then press
return.

Here you might see an "Upgrade to Ubuntu Pro" configuration page.  I
always continue without upgrading to Ubuntu Pro.

SSH Configuration: I do not install an openSSH server, tab to
highlight Done, and press return to continue.

Featured server snaps: I press tab until "Done" is highlighted at the
bottom, then return to proceed, as none of the snaps presented are
things I typically want to install.  Any of them can be installed
later after the base OS is installed, with appropriate install
commands.

Updating system: This can take many minutes to complete.  Grab a cup
of coffee.  Work on something else.  Check back occasionally.  You can
tell this step is done when the top of the screen says "Installation
complete!".  Press tab to highlight "Reboot Now" near bottom of screen
and press return to proceed.

If the screen says "Please remove the installation medium, then press
ENTER:", just press return/enter key to proceed.

You will see many boot progress messages appear, and hopefully in
under 1 minute you will see a login prompt that looks like:

```
<your-system-name> login:
```

Enter your user name and password to log in.

Update any of the base OS packages that have newer versions available
with these commands:

```bash
sudo apt update
sudo apt upgrade
```

Press return if prompted to confirm the installation of new packages.

If you want to install the default Ubuntu GUI desktop:

```bash
sudo apt-get install ubuntu-desktop-minimal
```

That downloads hundreds of MBytes of packages and installs them.  When
it is complete, enter the following command to reboot the system:

```bash
sudo reboot
```

It might "pause" during the boot messages for several minutes, before
you see a GUI login window.  See the section named "Installing desktop
GUI on Ubuntu Server Linux" below for some commands you can run that
disable a redundant network system service, one that significantly
slows down the system boot process if you leave it enabled.


## Ubuntu Desktop 24.04

In the initial boot menu, the default choice "Try or Install Ubuntu"
should be highlighted.  Press return to continue.

Choose your language.  Click Next button.

In "Accessibility", I usually do not change any of the defaults.  You
might want to.  Click Next button when ready to continue.

Select your keyboard layout.  Click Next button.

In "Internet connection" screen, you will likely see the default
choice as "Use wired connection", and there is a greyed-out
unselectable choice "No Wi-Fi devices connected".  This is the case
even if the host system has no wired network connection, only Wi-Fi.
The reason you see these choices is that as far as the VM is
concerned, it detects only a virtual wired Ethernet adapter, created
by VirtualBox, and does not see the host system's physical Wi-Fi
device.  This is normal.  Click the Next button to proceed.

In "Try or Install Ubuntu", select "Install Ubuntu".  Click the Next
button.

In "Type of installation", select "Interactive Installation".  At
least, that is what these instructions are written for.  If you want
to try an Automated installation, feel free to do so, but don't expect
the steps to be documented here.  Click the Next button.

In "Applications" window, select "Default selection", and click Next.

In "Optimise your computer" window, I leave the boxes unchecked, and
click Next.

In "Disk setup" window, select "Erase disk and install Ubuntu", and
click Next.

In "Create your account" window, enter your full name, desired system
name, user name, and password.  I prefer to uncheck the box next to
"Require my password to log in", as my host system is intended to keep
the computer secure with requiring a password to log in, with screen
lock after a timeout if you desire.  Repeating that for the guest OS
is just annoying to me.  Click Next button to continue.

In "Select your timezone" window, click on the map your approximate
location, or edit the contents of the text boxes labeled "Location" or
"Timezone".  Click Next to continue.

In "Ready to install" window, click "Install" button.

Installation can take several minutes.  Be patient.  Eventually you
should see a window titled "Installation complete".  Click the
"Restart now" button.

If in a boot screen you see the message "Please remove this
installation medium, then press ENTER:", just press return to
continue.


# General notes on VM settings

[I have verified that these settings are the same in VirtualBox 6.x
and 7.x running on a Windows 10/11 host OS, as well as many versions
of macOS, both Intel and Apple Silicon, from 10.14.x and later as
host.]

System -> Motherboard -> Base Memory: While there might be uses for a
Linux guest VM with only 1 GByte of RAM, I typically change that to 4
Gbyte when creating new VMs.

Disk space that is dynamically allocated is nice for having a smaller
host OS disk footprint, while the guest VM is using only a small
amount of disk space, and then it can grow over time as needed up to
the max size you pick when creating it.  I haven't measured the
performance penalty for dynamic vs. fixed, but it I haven't noticed a
big difference without measuring it.

System -> Processor -> Processor(s): If the host has multiple CPU
cores, I typically change this setting to 2 or 4 instead of the
default of 1.  The guest seems to definitely get benefits from having
at least 2 in many common use scenarios.

Display -> Screen -> Video Memory: The default size is 16 MB.  I plan
to consistently change that to 32 MB going forward, or perhaps to 64
MB for Ubuntu 22.04 VMs.  I have often experienced issues where an
Ubuntu Linux guest VM has the screen remain black after booting is
nearly complete, but before showing the desktop GUI.  Several VMs that
had that issue worked normally after I changed their Video Memory to
32 MB.  If that is the root cause and fix for that issue, then good to
make that change when creating a new guest VM.


# Additional install steps for Rocky Linux guest OS in VirtualBox

I often create VM images in VirtualBox that have the base OS
installation only, so that I can later create clones of that VM to
have some particular version of some particular Linux distribution set
up very quickly, without going through the install DVD/ISO process
again.  This works as long as cloning this base OS image works.

When making a clone of a Rocky Linux VM in VirtualBox, I found that
the clone would not boot.  I found the following Q&A with a bit of
searching on the Internet:

+ https://forums.virtualbox.org/viewtopic.php?f=1&t=108202

Following these steps suggested by someone there helped me create
clones of the Rocky Linux 9.2 VM in VirtualBox, and the clone VMs
would boot successfully:

```bash
sudo rm /etc/lvm/devices/system.devices
```

Edit the file `/etc/lvm/lvm.conf` to replace this line:

```
# use_devicesfile = 1
```

with this:

```
use_devicesfile = 0
```


# Ubuntu Linux Desktop settings I like

[The notes in this section are all specific to the guest OS, and
should independently of the host OS.]

## Dock icons

I often remove any icons in the dock that are not in the list below,
and add any that are not in the list below so they are locked in the
dock, in this order from top to bottom:

+ MATE Terminal
+ Firefox
+ Ubuntu Software
+ System Settings
+ Synaptic Package Manager
+ Software Updater

Doing so requires installing these packages:
```
sudo apt-get install git mate-terminal synaptic
```


## Ubuntu 20.04 Desktop Linux

Settings -> Appearance: reduce Icon size from default 48 to 24 pixels

Settings -> Privacy -> Screen -> Automatic Screen Lock: change to
Off.  Same reason as for "Security & Privacy" settings for Ubuntu
16.04, but it has been moved to a different "place" in Ubuntu 18.04.

Settings -> Power -> Power Saving -> change Blank screen to "Never".


## Ubuntu 22.04 Desktop Linux

Settings -> Appearance: reduce Icon size from default 48 to 24 pixels

Settings -> Privacy -> Screen Lock -> Automatic Screen Lock: change to
Off.  Same reason as for "Security & Privacy" settings for Ubuntu
16.04, but it has been moved to a different "place" in Ubuntu 18.04.

Settings -> Power -> Power Saving Options -> change Screen Blank to
"Never".

I have found with Ubuntu 22.04 in VirtualBox 6.1.x that the GUI is
often sluggish in moving around or resizing windows, and often the
keys will auto-repeat when I do not want them to.  One possible
culprit is the Wayland server.  Some on-line articles I have found
suggest using X windows instead.

Edit the file /etc/gdm3/custom.conf as root:

```bash
$ sudo vi /etc/gdm3/custom.conf
```

If there is a line `WaylandEnable=true` change `true` to `false`.
Otherwise explicitly add a line `WaylandEanble=false`.

Sources:

+ https://askubuntu.com/questions/1410256/how-do-i-use-x-instead-of-wayland-on-22-04
+ https://askubuntu.com/questions/1402124/problems-dsplaying-windows-vm-on-virtualbox-on-22-04-with-wayland


## Ubuntu 24.04 Desktop Linux

Settings -> Ubuntu Desktop: reduce Dock Icon size from default 48 to
24 pixels

Settings -> Privacy & Security -> Screen Lock -> Blank Screen Delay:
change to "Never".

Settings -> Privacy & Security -> Screen Lock -> Automatic Screen
Lock: change to Off.

System -> Users: In user account that I plan to use, click "Unlock..."
button near upper right, enter password.  Enable "Automatic Login".

If you find that the GUI is often sluggish in moving around or
resizing windows, or often the keys will auto-repeat, one possible
culprit is the Wayland server.  Some on-line articles I have found
suggest using X windows instead.

Edit the file /etc/gdm3/custom.conf as root:

```bash
$ sudo vi /etc/gdm3/custom.conf
```

If there is a line `WaylandEnable=true` change `true` to `false`.
Otherwise explicitly add a line `WaylandEanble=false`.

Sources:

+ https://askubuntu.com/questions/1410256/how-do-i-use-x-instead-of-wayland-on-22-04
+ https://askubuntu.com/questions/1402124/problems-dsplaying-windows-vm-on-virtualbox-on-22-04-with-wayland


## Installing desktop GUI on Ubuntu Server Linux

Sometimes it is easier to find a version of Ubuntu server for a
particular Ubuntu version and processor architecture combination, but
you want to run the desktop GUI on it.

First install the server version of Ubuntu Linux that you want.

Then use one of these commands to install the desktop GUI.  The first
one installs less software, a bit faster and less storage required.
The second one installs more software, such as LibreOffice.

```bash
# You only need _one_ of these two commands.
sudo apt-get install ubuntu-desktop-minimal
sudo apt-get install ubuntu-desktop
```

Note that a system installed this way might end up with two different
system services running on it, which can significantly increase the
boot time, e.g. by 2 minutes.

+ https://askubuntu.com/questions/1217252/boot-process-hangs-at-systemd-networkd-wait-online/1501504#1501504

If you see the output shown below from the first two commands, you can
run the third to disable the service intended for use in managing
network interfaces on Ubuntu server:

```bash
$ systemctl is-enabled NetworkManager-wait-online.service systemd-networkd-wait-online.service
enabled
enabled

$ systemctl is-enabled NetworkManager.service systemd-networkd.service
enabled
enabled

$ sudo systemctl disable systemd-networkd.service
[sudo] password for andy:
Removed /etc/systemd/system/network-online.target.wants/systemd-networkd-wait-online.service.
Removed /etc/systemd/system/sockets.target.wants/systemd-networkd.socket.
Removed /etc/systemd/system/multi-user.target.wants/systemd-networkd.service.
```


## Debian 8 notes

I do not normally use Debian, except by way of using it through
Ubuntu, but I did recently try installing Debian 8.11 as a VM, and
found these setup steps useful for me to have a more familiar
environment for building other open source tools.

```bash
$ su - root
[ enter root password ]
# apt-get install sudo
# adduser andy sudo
# exit
$ echo '/usr/sbin:/sbin:$PATH' >> $HOME/.bashrc
```


## MATE Terminal settings

While I have found the MATE Terminal an improvement over the default
Terminal in the past, in more recent versions of Ubuntu and Fedora
Linux, there is an odd bug where every time you switch to/from a MATE
Terminal window, the MATE terminal window gets _smaller_.  Unless this
is fixed, I will not be using MATE Terminal.

I like the MATE Terminal application slightly better than the default
Terminal, because it is fairly straightforward from the menu settings
to rename tabs after you create them, and I do not know how to do this
from Terminal.  I do prefer the default colors used in the default
Terminal over the ones used by MATE Terminal in Ubuntu 18.04, though.
Here is how to change them to my preferred settings:

* From MATE Terminal, choose menu item Edit -> Profiles...
* With the profile named "Default" selected in the list (probably the
  only item in the list), click the Edit button on the right.
* Click General tab, check box next to "Use custom default terminal
  size".  Change rows to 40.  Leave columns at 80.
* Click Scrolling tab, change Scrollback value to 10,000 lines (or
  more, or check the box next to Unlimited).
* Click Colors tab, uncheck the box next to "Use colors from system
  theme".
  * Click box to right of "Background color:" label.  In window that
    appears, click + symbol in bottom left below the word "Custom".
    In the text box that contains a "#" character followed by 6
    letters and/or digits (hex digits), select its contents and type
    to replace it with: "#300A24".  Click "Select" button.
  * Leave the "Text color" setting at the default, which appears to be
    the lightest white possible.
* Click Close button, then in the original "Profiles" window, click
  Close again.

I found the "gpick" program to make it straightforward for me to point
the cursor at a pixel on the screen, and see what the 6-digit hex
string should be to make it that color.


# Resizing guest OS window

I have found combinations of versions of VirtualBox and Ubuntu guest
OS where this worked even while installing the Ubuntu guest OS,
without having to install the guest additions CD software (see next
section).  Just click and drag the bottom right corner of the
VirtualBox window containing the guest OS GUI to resize it, then wait
several seconds until the guest Ubuntu desktop changes to match it.

However, I have also found combinations of versions where resizing the
VirtualBox window to a larger size makes that window larger, but it
does not make the Ubuntu guest OS desktop any larger.  It simply
leaves a black background around the still-same-size desktop.
Choosing "Inserting Guest Additions CD image" from the VirtualBox
"Devices" menu seemed to trigger VirtualBox to resize the Ubuntu
desktop, even without installing the guest additions, at least once
Ubuntu was completely installed.  I am guessing that menu item somehow
causes VirtualBox to communicate with the Ubuntu desktop in a way that
causes it to resize its desktop.  There are likely other ways.

If the resizing does not work, note that there are times with the
default desktop resolution, during installation of the Ubuntu guest
OS, where clicking a button to proceed requires dragging that window
around a bit.  Inconvenient, but possible.


# Installing guest additions kernel module

This is needed in order either to get copy/paste between host and
guest OS working, or to get shared folders working.  You can skip this
if you do not want those features.


## Approach installing Guest Additions via VirtualBox CD image

[Thanks to Sean Adams for these working instructions.]

In a Terminal window, type this command to install essential packages
needed for compiling C programs, including the kernel extensions that
the Guest Additions contain:

```bash
# Ubuntu systems:
sudo apt install build-essential

# Fedora systems:
TODO

# Rocky systems:
sudo dnf install kernel-devel kernel-headers gcc make bzip2 perl elfutils-libelf-devel
```

Start VirtualBox and boot the VM without a CD/DVD in the "drive".  Log
in to the desktop (assuming you're using the desktop edition).  Now on
the host OS go to the Devices menu of VirtualBox.  Near the bottom of
that menu will be "Insert Guest Additions CD image...".  Select it.
In the VM, a CD will appear in the dock and a dialog should pop up
asking if you want to allow the software on the CD to autorun.  Let it
run.  A second dialog will ask you to authenticate (sudo) to install
the software.  Once it is done, shut down the VM.

See later sections below for how to enable copy/paste and shared
folders between host and guest OS.

The Guest additions change with every version of VirtualBox.  Be
prepared to re-install them whenever you upgrade VirtualBox.

Even minor updates trigger a message in the VM suggesting to update.
I'm not sure it is required, but I do it anyway.

Following the instructions above re-installs the guest additions even
if they have already been installed in the VM.


### Modifications for installing Guest Additions via VirtualBox CD image on Ubuntu Server

You still need to install the `build-essential` package, as above.
You can still go to the Devices menu of VirtualBox and choose the item
"Insert Guest Additions CD image...", as above.

Then to see its contents:

```bash
sudo mkdir /media/cdrom
sudo mount /dev/cdrom /media/cdrom
ls /media/cdrom
```

To start the installer:

```bash
sudo /media/cdrom/VBoxLinuxAdditions.run
```

Reboot for the changes to take effect:

```bash
sudo shutdown -r now
```


# Getting copy/paste working between host and guest OS

Prerequisite: You have successfully installed guest additions kernel
module, and shut down the VM afterwards.

+ While the VM is shut down, select the VM in "Oracle VM VirtualBox
  Manager" window.
+ Click "Settings" near top left of window.
+ In new window that appears, click "General" icon near top left.
+ In row of tabs below that, click "Advanced"
+ In popup menu next to "Shared Clipboard", select "Bidirectional"
+ Click "OK" button to close Settings window and save them.



# Set up shared folders between Ubuntu guest OS and host macOS

[Instructions in this section verified to work for Ubuntu, Fedora, and
Rocky Linux guest OS, and host OS ranging across many versions of
macOS and Windows 10/11.]

Prerequisite: You have successfully installed guest additions kernel
module, and shut down the VM afterwards.

I believe I had trouble with shared folder settings disappearing if I
tried creating them while the guest OS was running.  Shutting down the
guest OS and then creating the shared folder settings seemed to work
better, so do that first.

Menu item: Machine -> Settings... -> Shared Folders tab
Click icon on right side of window with + sign in it
Select the desired folder on host OS
Click to enable "Auto-mount"
Click OK to save settings.

The folder name I created was 'p4-docs', which I will use below for
sake of example.

Boot guest OS and log in.

```bash
% ls /media
```

should show a new directory sf_p4-docs/
Trying to list it will likely give 'Permission denied'.

Command to add a user to group `vboxsf`:

```bash
% sudo usermod -a -G vboxsf $USER
```

Log out and log back in, or shut down and reboot guest OS.  Run the
command `id` to confirm that the group `vboxsf` now shows up in the
output.  If so, then this command should now show you the contents of
the shared folder:

```bash
% ls /media/sf_p4-docs
```

A symbolic link to this shared folder can make it easy to access from
your home directory, e.g.:

```bash
% ln -s /media/sf_p4-docs/ ~/p4-docs
```


## Removing Emacs lock files on Windows host OS

When I share a folder from a Windows 10/11 host OS with an Ubuntu Linux
guest VM from VirtualBox, and then use Emacs in the Ubuntu guest VM to
edit text files, it often creates files whose names are the same as
the original, in the same directory, except the name is prefixed with
`.#`

These files contain one line of text that looks like this:

```
andy@linwin-vm.1820:1600715087
```

There is a way to prevent Emacs from creating them, but sometimes I
want to have multiple guest VMs running, either at the same time, or
taking turns editing the same text files, and such lock files may help
me accidentally clobber such changes.

https://www.reddit.com/r/emacs/comments/29zm3q/how_to_get_rid_of_filename_files_that_emacs_is/

The Ubuntu VMs do not seem to have permission to delete such files.
Attempting so fails with error messages like those below:

```bash
$ rm .#readme-delete-dot-sharp-emacs-lock-files.txt
rm: remove regular file '.#readme-delete-dot-sharp-emacs-lock-files.txt'? y
rm: cannot remove '.#readme-delete-dot-sharp-emacs-lock-files.txt': Operation not permitted

$ \rm -f .#readme-delete-dot-sharp-emacs-lock-files.txt
rm: cannot remove '.#readme-delete-dot-sharp-emacs-lock-files.txt': Operation not permitted
```

I can delete them from File Explorer in Windows 10/11, but if there
are many such files, in many directories, it is tedious to do it that
way.

I can delete them from a `cmd.exe` command prompt in the Windows host
machine.  The only way I have found so far that succeeds is like this:

```cmd.exe
C:\Users\jfingerh> cd "OneDrive - Intel Corporation"
C:\Users\jfingerh\OneDrive - Intel Corporation> cd Documents
C:\Users\jfingerh\OneDrive - Intel Corporation\Documents> cd p4-docs
C:\Users\jfingerh\OneDrive - Intel Corporation\Documents\p4-docs>
```

To delete them in only the current directory, use the command below.
The "/f" option means to force the deletion:

```
del /f .#*
```

To delete them in the current directory and all subdirectories, use
the command below.  The "/s" option means to delete files from all
subdirectories, too.

```
del /f /s .#*
```

You can also delete all Emacs auto-save files, with names ending in
the "~" character, using the similar command:

```
del /f /s *~
```

I have permission to delete those files from the Ubuntu VMs, though,
so my usual Linux commands work there.


# Updating installed packages on Linux systems

On Ubuntu with a desktop GUI, the "Software Updater" application that
can be found by searching for that name in the Activities search seems
to do a good job of searching for newer versions of packages that were
installed on the system via `apt-get`, and letting you install the
newer versions with a few button clicks.

On Fedora and Rocky with a desktop GUI, the "Software" application
"Updates" table sometimes does this, but sometimes seems to mention no
updates available even when the method below does find updates.  I do
not know why this difference exists.

Fedora/Rocky command line method of seeing what updated packages are
available, without installing them:

```bash
dnf check-update
```

To install new packages:

```bash
sudo dnf upgrade
```


# Removing older kernel packages on Linux systems

At least on Ubuntu, Fedora, and Rocky Linux, when a new kernel version
is available, and you update to add it, the older version that you
were using remains in the file system, taking up space.  I often like
to remove the older versions to save storage space.

For any version of Linux, you should not try to remove the packages
for the Linux kernel verion that is currently running, so after
updating, reboot, check the current version of the kernel that is now
running in the output of the `uname -a` command, and then delete older
unused kernel versions.


## Removing older kernel packages on Ubuntu Linux

If you are OK with keeping the current version of the kernel, and the
most recent older version, then this page:
+ https://linuxconfig.org/how-to-remove-old-kernels-on-ubuntu

claims that the following command will do so, but I have not verified
this myself yet:

```bash
sudo apt autoremove --purge
```

If you want to remove all kernel packages except the one currently in
use, read on.

See the current kernel version running:
```
uname -a
```

List all kernel package names in a file:
```bash
large-pkgs.py | grep linux > rm.sh
```

(`large-pkgs.py` is in `bin` directory of
https://github.com/jafingerhut/dotfiles )

Use a text editor to edit the file `rm.sh` and delete package names
for the current kernel version, leaving only package names you want to
remove.  Then put all package names on the same line and prepend it
with the command:

```
sudo apt purge <package names go here ...>
```

Then save that file, exit the editor, and run `source rm.sh`


## Removing older kernel packages on Fedora/Rocky Linux

Source: https://discussion.fedoraproject.org/t/old-kernels-removal/77046

```bash
$ rpm -q kernel-core
kernel-core-5.6.8-300.fc32.x86_64
kernel-core-5.6.10-300.fc32.x86_64
kernel-core-5.6.11-300.fc32.x86_64
```

Remove the version(s) you wish with a command like this:

```bash
sudo dnf remove kernel-core-5.6.8-300.fc32
```


# Customizations that I personally like to make on some Ubuntu VMs

Removed several icons from Launcher that I usually do not use
(e.g. LibreOffice), by right-clicking on them and choosing the popup
menu item to unlock them from the launcher.

Click on the top icon on the Launcher, which creates a text box.
Enter "terminal" to search for that program.  Click "Terminal" icon to
start that program.  Right-click on Terminal icon in Launcher and
select "Lock to Launcher" so it will always be there.

```bash
# Do this only if you want Andy Fingerhut's personalized dot
# files.  You probably want your own instead.  WARNING: Do not
# skip the export command.  Shell scripts executed in the step
# after that need it to be set, or else by default they will get
# files from a different Github repo belonging to someone else.

% sudo apt install curl
% export github_user=jafingerhut
% bash -c "$(curl -fsSL https://raw.github.com/$github_user/dotfiles/master/bin/dotfiles)" && source ~/.bashrc

# (optional) Save some disk space by removing large programs I won't use
% sudo apt purge thunderbird libreoffice-*
% sudo apt clean

# Useful packages I like to have, not installed by default.
% sudo apt install python-pip python3-pip
```


# Shrinking a guest OS disk image

Currently these options are covered here, although some of the links
to other articles may cover more cases:

+ guest OS: Ubuntu Linux, and perhaps any guest Linux using ext4 file system
+ host OS: macOS (many versions from 10.14.x and later), Windows 10/11

A StackOverflow Q&A on compacting/shrinking a guest OS VDI file
system:

    https://superuser.com/questions/529149/how-to-compact-virtualboxs-vdi-file-size

Another article that might cover cases not included here:

    https://www.howtogeek.com/312883/how-to-shrink-a-virtualbox-virtual-machine-and-free-up-disk-space/

The `dd` command will eventually fill up the disk with a file
containing nothing but 0 bytes.  Then delete it.

    dd if=/dev/zero of=/var/tmp/bigemptyfile bs=4096k
    rm /var/tmp/bigemptyfile

After shutting down the VM, run this command on a macOS host:

    VBoxManage modifymedium --compact /path/to/thedisk.vdi

On a Windows host with VirtualBox stored in the default directory
`C:\Program Files\Oracle\VirtualBox`, open a `cmd.exe` window by
entering `cmd` in start box search, and pressing enter.  Then in that
window:

    cd "C:\Program Files\Oracle\VirtualBox"
    VBoxManage list hdds

    # Replace the path name inside double quotes in the example
    # command below with the path to your guest OS's .vdi file
    VBoxManage modifymedium --compact "C:\path\to\disk.vdi"

Another way to do it is from within a `bash` terminal window, e.g. one
installed via the Windows version of Git.

    cd VirtualBox\ VMs
    cd Ubuntu\ 18.04\ SDE\ 9.4.0
    du -sh . ; /c/Program\ Files/Oracle/VirtualBox/VBoxManage.exe modifymedium --compact Ubuntu\ 18.04\ SDE\ 9.4.0.vdi


# Sample sshfs commands

If all you want is to share a directory between host and guest OS, I
would recommend the previous section to set that up.  Trying to use
sshfs for this with a host that is a laptop with a changing IP address
(e.g. by connecting to a different Wi-Fi access point) causes the
sshfs connection to break and require re-creating, which I find
annoying.

sshfs is nice in that you do not need to have root privileges on any
machines to use it -- only the ability to ssh from the machine
mounting the remote file system, to the machine where the file system
is located.

It is not so convenient when used to mount a host OS's file system on
a guest OS.

(a) One way is to use the IP address of the host OS's main network
    adapter.  This works well, except that this IP address changes if
    the host OS is on a laptop, and its Wi-Fi interface gets a new IP
    address after changing locations.

(b) Another way I have successfully used is to create a host-only
    network (instructions below).  This prevents the IP address from
    changing, but unfortunately does not work when the host OS is
    currently using Cisco AnyConnect with an active VPN connection,
    because that software prevents 'split tunneling', meaning that no
    IP traffic on the host can go to any network interface other than
    the virtual VPN interface, so no IP communication between guest
    and host during those times.

It is fine for mounting a file system of a machine with a consistent
IP address on the host or the guest.

```bash
# mounting
# sshfs [user@]host:[dir] mountpoint [options]

% MYIP="10.0.1.40"
% MYIP="192.168.56.1"
% sshfs jafinger@jafinger-aur.cisco.com:/auto/ins-asic-11/jafinger/forwarding/ins-asic $HOME/ins-asic

# unmounting
# fusermount -u <mountpoint>
% fusermount -u $HOME/ins-asic
```


# Create host-only adapter network between guest and host OS

Note: I never got this approach working to my satisfaction.  Once I
was able to get shared folders working between guest and host OS, that
gave me all I really wanted in terms of sharing files between guest
and host OS.

Create host-only adapter network between guest Ubuntu VM and host Mac
OS X, so that host OS will have consistent IPv4 address that guest
Ubuntu VM can use to sshfs mount directories physically stored in the
host Mac OS X file system, with a consistent IPv4 address that does
not depend on where the host Mac OS X laptop is, and thus which IPv4
address its Wi-Fi adapter happens to have.

In VirtualBox, create a Host-only adapter if you haven't done so
before.  I followed instructions here:

    https://www.virtualbox.org/manual/ch06.html#network_hostonly

* In Mac OS X VirtualBox application, choose menu item VirtualBox ->
  Preferences...

* Click Network
* Click Host-only Networks
* Click icon on right that has hover-text help "Adds new host-only network"
* I used default name "vboxnet0"
* In menu, right click vboxnet0 and choose "Edit Host-only Network"
* In window that appears, I saw under Adapter tab:

IPv4 Address: 192.168.56.1
IPv4 Network Mask: 255.255.255.0
IPv6 Address: <blank>
IPv6 Network Mask Length 0 (grayed out)

I left everything as shown above.  Under "DHCP Server" tab:

    Check box next to "Enable Server" was checked.
    Server Address: 192.168.56.100
    Server Mask: 255.255.255.0
    Lower Address Bound: 192.168.56.101
    Upper Address Bound: 192.168.56.254

click Settings for the VM.

System -> Processor -> change to 4 virtual CPUs.  1 is the default.

    Network -> Adapter 2
    enable check box next to "Enable Network Adapter"
    in popup menu next to "Attached to:" choose "Host-only Adapter"
    In popup menu next to "Name:" choose vboxnet0

Click OK.  Settings should be saved.

In host Mac OS X system, 'ifconfig' in a terminal should show an
interface "vboxnet0" with same IPv4 address as shown above
(192.168.56.1).

Boot up guest Ubuntu VM.

In guest OS, 'ifconfig' in a terminal should show eth1, the 2nd
network adapter (first is called eth0) with an IPv4 address somewhere
in the range 192.168.56.101 through 192.168.56.254 (probably the first
in that range).

From guest OS, should be able to ping host OS at 192.168.56.1.  Also
should be able to ssh to it, if host Mac OS X is enabled for "Remote
Login" under System Preferences -> Sharing

From host OS, should be able to ping guest OS at 192.168.56.101.  I
don't have my guest Ubuntu VM configured to allow ssh into it.

Note: Unfortunately, if the host is Mac OS X, and you run Cisco
AnyConnect Secure Mobility Client on that host to connect via VPN,
while you are connected via VPN, the host only network adapter will
not be able to send or receive packets between the host and the guest.
Bummer.  Shared folders seems like a better way to go, then, for
sharing files on the host file system with the guest OS.


# Mounting a VirtualBox VDI file containing an Ubuntu disk image on a newer Ubuntu VM

I had a VirtualBox VM with an installation of Ubuntu 16.04 that after
months of perfectly good use, one day it would not boot.  There were
some files that I wanted to retrieve from the disk image.  I searched
for a way to mount this non-working VM's disk image on a fresh new
working Ubuntu 16.04 VM, and found this way that worked for me:

Source: https://askubuntu.com/questions/19430/mount-a-virtualbox-drive-image-vdi

The answer by use "Maxime R."

Use qemu-nbd, the process is explained on serverfault and in this blog.

    'serverfault' link: https://serverfault.com/questions/210684/how-do-you-mount-a-vdi-image-on-linux/210685#210685
    'this blog' link: https://bethesignal.org/blog/2011/01/05/how-to-mount-virtualbox-vdi-image/
    Both of those links gave an error when I tried to access them on
    2018-Sep-03.

Basically, you'll have to install `qemu` if needed:

    sudo apt-get install qemu

Then you'll need to load the _network block device_ module:

    sudo rmmod nbd
    sudo modprobe nbd max_part=16

Attach the .vdi image to one of the nbd you just created:

    sudo qemu-nbd -c /dev/nbd0 drive.vdi

Now you will get a /dev/nbd0 block device, along with several
/dev/nbd0p* partition device nodes.

    sudo mount /dev/nbd0p1 /mnt

Once you are done, unmount everything and disconnect the device:

    sudo umount /dev/nbd0
    sudo qemu-nbd -d /dev/nbd0


# Changing Ubuntu host name

I found these instructions worked for me, at least on Ubuntu 16.04 and
18.04 Linux VMs I have used.

Instructions found here: https://www.cyberciti.biz/faq/ubuntu-18-04-lts-change-hostname-permanently/

To see the current host name, use the following command.  You will need
to know this for the last step below:

    hostnamectl

To permanently change the host name to 'linwin-vm':

    sudo hostnamectl set-hostname linwin-vm

Now use this command to edit the /etc/hosts file, replacing all
occurrences of the old hostname with linwin-vm:

    sudo vi /etc/hosts

Reboot the system for the changes to take effect.


# A little bit of Ubuntu package managment


## See a list of installed package names and sizes, sorted by size

I have a small Python3 program in my repo
https://github.com/jafingerhut/dotfiles in the `bin` directory called
`large-pkgs.py` that does exactly this:

```bash
large-pkgs.py
```

Also `installed-pkgs.py`.


# Ubuntu Linux Desktop settings I like (historical)

These are for older versions of Ubuntu Linux that I might not use
again.


## Ubuntu 16.04 Desktop Linux

Settings -> Appearance -> Launcher icon size: reduce from default 48
to 24 pixels

Settings -> Security & Privacy -> uncheck "Waking from suspend" and
"Returning from blank screen".  My host OS has security settings that
require me to enter a password.  I do not need the annoyance of a
separate layer of password entering for the guest OS's, too.

Settings -> Brightness & Lock -> move "Lock" slider to "Off" position,
and change "Turn screen off when inactive for:" to "Never".


## Ubuntu 18.04 Desktop Linux

Settings -> Dock -> Icon size: reduce from default 48 to 24 pixels

Settings -> Privacy -> Screen Lock -> Automatic Screen Lock: change to
Off.  Same reason as for "Security & Privacy" settings for Ubuntu
16.04, but it has been moved to a different "place" in Ubuntu 18.04.

Settings -> Power -> Power Saving -> change Blank screen to "Never".
