
CC       = gcc
SHARED   = -shared
CFLAGS   = -O3 -fPIC
LDFLAGS  = 
CPPFLAGS = 
LIBNAME  = libGranolaserver
LIBEXTENSION = so


JAVA_INCDIR      = $(JAVA_HOME)/include
JAVA_INCDIR_PLAF = $(dir $(wildcard $(JAVA_INCDIR)/*/jni_md.h))
CPPFLAGS += -I$(JAVA_INCDIR) -I$(JAVA_INCDIR_PLAF)

SRC := $(shell find . -name "*.c" -print)
OBJ := $(SRC:%.c=%.o)

CLEAN_EXTENSIONS = o $(LIBEXTENSION)

LIBDIRECTBUFFER = $(LIBNAME).$(LIBEXTENSION)

all: $(LIBDIRECTBUFFER)

%.o: %.c
	$(CC) $(CFLAGS) $(CPPFLAGS) -c $< -o $@

$(LIBDIRECTBUFFER): $(OBJ)
	$(CC) $(SHARED) -o $@ $^ $(LDFLAGS)

clean:
	for extension in $(CLEAN_EXTENSIONS); do \
		find . -name "*.$$extension" | xargs rm -f ; \
	done
	find . -name "*~" | xargs rm -f
