CFLAGS=-Wall -O2

all: memaccess lfsr32

memaccess: memaccess.c
	$(CC) $(CFLAGS) -o $@ $<

lfsr32: lfsr32.c
	$(CC) $(CFLAGS) -o $@ $<

clean:
	/bin/rm -f memaccess
