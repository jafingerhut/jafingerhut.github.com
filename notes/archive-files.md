# Listing, extracting, and creating archives in several formats

This article contains a few notes on commands that can be run on a
Fedora Linux or Ubuntu Linux system to list or extract the contents of
a few different archive file formats.

Some packages to install to be able to unpack certain file formats:
```bash
# Fedora
sudo dnf -y install p7zip

# Ubuntu
sudo apt-get install -y p7zip-full
```

To list-contents-of/unpack `.zip` file:
```bash
unzip -v archivename.zip       # list
unzip -e archivename.zip       # extract
zip -r archivename.zip directoryorfile1 [ directoryorfile2 ... ]  # create archive
unzip -h    # get help on other command line options
zip -h      # get help on other command line options
```

The file name suffixes mentioned below are the common ones you will
find in use, but note that there is no requirement that the file names
have such suffixes.  Using a command like `file <archivename>` can
sometimes determine which of these file formats a file is stored in.

To list-contents-of/extract `.tar.gz` or `.tgz` file:
```bash
tar tzvf archivename.tgz       # list
tar xkzf archivename.tgz       # extract
tar czf archivename.tgz directoryorfile1 [directoryorfile2 ...]  # create archive
tar --help
```

To list-contents-of/extract `.tar` file:
```bash
tar tvf archivename.tar       # list
tar xkf archivename.tar       # extract
tar cf archivename.tar directoryorfile1 [directoryorfile2 ...]  # create archive
tar --help
```

To list-contents-of/unpack `.7z` file:
```bash
7za l archivename.7z           # list
7za x archivename.7z           # extract files with full paths
7za a archivename.7z directoryorfile1 [directoryorfile2 ...]  # create archive
7za -h    # get help on other command line options
```

To list-contents-of/unpack a cpio file:
```bash
cpio -l < archivename          # list
cpio -i < archivename          # extract
cpio --help    # get help on other command line options
```
I do not know if there is a traditional file name suffix for cpio
archives, but at least some of them use `.img` as a suffix, and others
use `.cpio`.  There may be others.
