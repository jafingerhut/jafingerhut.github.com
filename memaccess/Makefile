CFLAGS=-Wall -O2
#CFLAGS=-Wall -ggdb

all: memaccess memaccess2 lfsr32

memaccess: memaccess.c
	$(CC) $(CFLAGS) -o $@ $<

memaccess2: memaccess2.c
	$(CC) $(CFLAGS) -o $@ $<

lfsr32: lfsr32.c
	$(CC) $(CFLAGS) -o $@ $<

clean:
	/bin/rm -f memaccess memaccess2 lfsr32
