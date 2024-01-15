# Notes on installing UTM on an Apple Silicon Mac

I learned what I know about using UTM on a Mac system with an Apple
Silicon CPU from this article:

+ https://andrewbaisden.medium.com/how-to-install-ubuntu-linux-on-apple-silicon-macbooks-7424b739b389

I have tested these instructions on the following kind of system:

+ MacBook Pro, 2023 model with Apple M2 Pro CPU
+ macOS Venture 13.6.3
+ UTM version 4.4.5
  + https://mac.getutm.app

I was able to successfully install and run these arm64 versions of
Linux:

+ Ubuntu 20.04 Server, and Desktop
  + I installed Desktop by first installing Server, then `sudo apt-get
    install ubuntu-desktop-minimal`
  + Other instructions for installing and Ubuntu Desktop GUI on Ubuntu
    server can be found here:
    https://phoenixnap.com/kb/how-to-install-a-gui-on-ubuntu
+ Ubuntu 22.04 Desktop
  + ISO image `jammy-desktop-arm64.iso` downloaded from here:
    https://cdimage.ubuntu.com/jammy/daily-live/current/


## Steps to create a new Ubuntu VM in UTM, for arm64 guest OS

These instructions should work for any Linux guest OS that expects an
arm64 CPU.

+ Run the UTM application.
+ Click button "Create a New Virtual Machine"
+ Click button "Virtualize"
+ Click button "Linux"
+ In window titled "Linux":
  + Enable check box next to "Use Apple Virtualization"
  + Disable check box "Boot from kernel image"
  + Disable check box "Enable Rosetta"
  + Click button "Browse.." and then find and select your Ubuntu ISO image file
  + Click button "Continue"
+ In window titled "Hardware":
  + Change memory size to desired value (I used 8192 MB)
  + Change CPU Cores to desired value, or leave as default (I used 4)
  + Click button "Continue"
+ In window titled "Storage":
  + Change size to desired value (I used 64 GB)
  + Click button "Continue"
+ In window titled "Shared Directory":
  + Click button "Browse" and select a directory to share between host
    macOS and the guest OS.  I chose a directory I created with name
    "p4-docs" that was inside my "Documents" folder.
  + Click button "Continue"
+ In window titled "Summary":
  + Change value of "Name" to what you want.  I used "Ubuntu 22.04".
  + Click button "Save"

Back in the main UTM window, there should be a new entry in the left
column with the name you chose.

+ Click its name to select it, if it is not already.
+ Right-click or control-click on the name of the VM.  Select the
  choice "Edit" in the pop-up menu that appears.
+ In the window that appears, click on "Display" in the left column.
  + Enable check box next to "HiDPI (Retina)".
  + Click button "Save"
+ Click the "play" button that looks like a triangle to start it.

Go through the normal steps to install Ubuntu using its GUI installer.
This web page has some explicit steps on how to do so, if you have not
done so before:

+ https://andrewbaisden.medium.com/how-to-install-ubuntu-linux-on-apple-silicon-macbooks-7424b739b389

I use the steps up to and including "Make sure Ubuntu has the latest
packages", but the rest I instead use the setup steps in the article
[`macos-virtualbox-ubuntu-install-notes.md`](macos-virtualbox-ubuntu-install-notes.md)
(at least the ones that are not specific to VirtualBox).


## Enabling shared folder between host macOS and UTM guest host

Instructions with steps for other scenarios besides the one described
below can be found here:

+ https://docs.getutm.app/guest-support/linux/#macos-virtiofs

```bash
sudo mkdir /media/shared
```

Edit the system file `/mnt/fstab` so that it is mounted on every
reboot:

```bash
sudo vi /etc/fstab
```

Add this line:
```
share	/media/shared	virtiofs	rw,nofail	0	0
```

The reboot the system.  To create a symbolic link to the shared directory:

```bash
ln -s /media/shared/p4-docs $HOME/p4-docs
```


## Shrinking a guest OS disk image

TODO: Test out the instructions here to see if they work:

+ https://docs.getutm.app/settings-qemu/drive/resize-and-compress/

When I installed UTM on my macOS system, the VM images were stored in
directories inside of this one:

+ `~/Library/Containers/com.utmapp.UTM/Data/Documents`

I looked around in the UTM GUI for options to shrink/compress the
drive of the VMs I have created, but did not find any.

I tried running my little bash script `zero-empty-disk-space.sh`:

+ https://github.com/jafingerhut/dotfiles/blob/master/bin/zero-empty-disk-space.sh

that I use just before shrinking VirtualBox drive files on the host
OS, but the only effect that seemed to have was to _increase_ the size
of the `.img` file that UTM creates for the VM's drive to be the
maximum size (64 GB in my scenario).
