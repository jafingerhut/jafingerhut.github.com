# Notes on installing Ubuntu guest VMs in VirtualBox on a macOS host

2017-Apr-07: On Mac OS X 10.11.6 system,
installed VirtualBox 5.1.18 r114002,
and created new Ubuntu 14.04.1 64-bit VM

Update from 2017-Nov-07:
On macOS Sierra 10.12.6 system,
installed VirtualBox 5.1.30 r118389,
and created Ubuntu 16.04.3 64-bit desktop VM

Update from 2018-Apr-20:
On macOS Sierra 10.12.6 system,
installed VirtualBox 5.2.8 r121009,
and created Ubuntu 16.04.4 64-bit desktop VM
and also an Ubuntu 14.04.5 64-bit desktop VM (using VirtualBox CD
image to add guest additions)


# General notes on VM settings

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
to consistently change that to 32 MB going forward.  I have often
experienced issues where an Ubuntu Linux guest VM has the screen
remain black after booting is nearly complete, but before showing the
desktop GUI.  Several VMs that had that issue worked normally after I
changed their Video Memory to 32 MB.  If that is the root cause and
fix for that issue, then good to make that change when creating a new
guest VM.


# Ubuntu Linux Desktop settings I like

## Ubuntu 16.04 Desktop Linux

Settings -> Appearance -> Launcher icon size: reduce from default 48
to 24 pixels

Settings -> Security & Privacy -> uncheck "Waking from suspend" and
"Returning from blank screen".  My host OS has security settings that
require me to enter a password.  I do not need the annoyance of a
separate layer of password entering for the guest OS's, too.


## Ubuntu 18.04 Desktop Linux

Settings -> Dock -> Icon size: reduce from default 48 to 24 pixels

Settings -> Privacy -> Screen Lock -> Automatic Screen Lock: change to
Off.  Same reason as for "Security & Privacy" settings for Ubuntu
16.04, but it has been moved to a different "place" in Ubuntu 18.04.


# Resizing guest OS window

I found this to work with VirtualBox and Ubuntu 16.04 guest OS even
while installing the Ubuntu guest OS.  That is, it does not require
first installing the guest additions kernel module.  That is a very
good thing, because while installing the Ubuntu desktop guest OS, one
of the GUI windows for selecting your type of keyboard is so wide it
does not fit in the default window width, and it is difficult to click
on the "OK" button to proceed unless you first make the window wider.

Just click and drag the bottom right corner of the VirtualBox window
containing the guest OS GUI to resize it, then wait several seconds
until the guest Ubuntu desktop changes to match it.


# Installing guest additions kernel module

This is needed in order either to get copy/paste between host and
guest OS working, or to get shared folders working.  You can skip this
if you do not want those features.

The older approach I used for Ubuntu 14.04 is described in its own
subsection later below, involving installing virtualbox-guest-dkms and
virtualbox-guest-x11 packages.


## Approach installing Guest Additions via VirtualBox CD image

[Thanks to Sean Adams for these working instructions.]

In a Terminal window, type this command to install essential packages
needed for compiling C programs, including the kernel extensions that
the Guest Additions contain:

    sudo apt install build-essential

Start VirtualBox and boot the Ubuntu VM without a CD/DVD in the
"drive".  Log in to the desktop (assuming you're using the desktop
edition).  Now on the Mac go to the Devices menu of VirtualBox.  The
last item will be "Insert Guest Additions CD image...".  Select it.
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


## Approach installing virtualbox-guest-* packages

I was able to get host/guest copy and paste working using this
approach for Ubuntu 14.04.1 guest OS.  See above for steps for an
Ubuntu 16.04 guest OS.

    # This might be necessary to get copy/paste to work between guest
    # and host OS
    sudo apt install virtualbox-guest-dkms

Reboot system for newly installed kernel modules (installed by
virtualbox-guest-dkms package) to take effect.  Larger display size
choices are now available in System Settings -> Displays

To get other custom display sizes not on that menu, just click and
drag the bottom right corner of the VirtualBox window to resize it,
then wait several seconds until the guest Ubuntu desktop changes to
match it.

Way more details than one probably would like on this issue:

    http://askubuntu.com/questions/451805/screen-resolution-problem-with-ubuntu-14-04-and-virtualbox/595192#595192

With Ubuntu 14.04 I could get copy and paste between guest and Mac OS
X to work.  With Ubuntu 16.04.2 I could not using this approach.
Fortunately the approach using the guest additions CD image described
in the previous section does work for Ubuntu 16.04.


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

Prerequisite: You have successfully installed guest additions kernel
module, and shut down the VM afterwards.

I was able to get shared folders working for both Ubuntu 14.04 and
Ubuntu 16.04 guest OS's using these instructions.

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

Command to add a user to a group:

```bash
% sudo usermod -a -G <groupName> <userName>
```

In this particular case for the group 'vboxsf':

```bash
% sudo usermod -a -G vboxsf $USER
```

Log out and log back in, or shut down and reboot guest OS.  Run the
command 'id' to confirm that the group 'vboxsf' now shows up in the
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


# Solving screen flicker issue with Ubuntu 17.10 guest VM in VirtualBox

Found this solution here: https://forums.virtualbox.org/viewtopic.php?f=8&t=85110

This is the comment that I found helped:

    I found this to be a bug with the new Ubuntu 17.10 in Sierra and
    VBox 5.2.0 and fixed it by selecting Xorg instead of default
    Wheland window system. You will find this option in the Ubuntu
    login dialog under the gear.


# Shrinking a guest OS disk image

A StackOverflow Q&A on compacting/shrinking a guest OS VDI file
system:

    https://superuser.com/questions/529149/how-to-compact-virtualboxs-vdi-file-size


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


# Notes on VirtualBox and VMware Fusion on OSX 10.6.8 Snow Leopard

The only reason for this section is that I have an older MacPro model
MacPro2,1 that cannot run any more recent versions of OSX than 10.6.8
(maybe 10.7.x, maybe not).  It isn't the fastest machine as of 2017,
but it does have 32 GB of RAM, which is still the most RAM in one
machine I own.  I can run Linux on it as the host OS, but I am curious
to know about running recent versions of Linux as a guest VM.

I have a copy of VMware Fusion 3.1.4 that I bought years ago, and
installed there.  I found recently that when upgrading Ubuntu 16.04 to
the most recent version as of Oct 2017, the GUI no longer worked.
Only console-based access was available in the VM, which is annoying.

Error message I see during boot of guest before GUI fails:

    nsc-ircc, Wrong chip version 00


I believe that VirtualBox 4.3.40 is the latest version supported on
OSX 10.6.8 hosts.  I have a copy of that installed, and I can run the
most recent 2017-Oct versions of Ubuntu 16.04 in it, but with at most
2 GB of guest OS RAM.  This is usable, but I'd like to do better.  I
have tried it with 2.5 GB and 3 GB of guest OS RAM, and the Ubuntu
install steps failed with an error from VirtualBox about not enough
memory being available on the host OS.  There is certainly enough
physical RAM, so this might be some kind of limitation with virtual
memory in that version of VirtualBox.  Not sure why that happens.

I did a little bit of Google searching for the error message, and some
people experienced problems on 64-bit Windows host OS's with guest
Linux VM, when the host had a particular range of versions of the
Google Chrome browser, which installed some 64-bit application that
may have been preventing VirtualBox from allocating as much physically
contiguous RAM as it wanted.  That is just a guess from reading the
descriptions of the problem briefly.



I found a VMware Fusion page describing which versions of VMware
Fusion are supported on which versions of Mac OSX, and found that
VMware Fusion 5 is the latest one supported, and perhaps only for OSX
10.6.8 (i.e., no earlier versions of OSX 10.6.x supported).

I would have tried that version of VMware Fusion on that old Mac to
see if it allows me to run the latest versions of Ubuntu 16.04, with
GUI, and relatively large guest OS memory (e.g. 8 GB or maybe even
more), but I do not own a copy, and I don't think it is easy in 2017
to get a free trial copy (and maybe not even to buy one, since it is
fairly old).


Of course I can also just run Ubuntu as the host OS, too.


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
