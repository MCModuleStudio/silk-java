CC       = gcc.exe
LINKOBJ  = Native.o
OBJ      = $(LINKOBJ)
INCS     = -Isilk/interface "-IC:\Program Files\Java\jdk-1.8\include" -I"C:\Program Files\Java\jdk-1.8\include\win32"
LIBS     = -Lsilk -lSKP_SILK_SDK
BIN      = silk-java.dll
CFLAGS   = $(INCS) -m64 -std=c11 -O3
RM       = rm -f

.PHONY: all all-before all-after clean clean-custom silk

all: all-before $(BIN) all-after

clean: clean-custom
	${RM} $(OBJ) $(BIN)

# clean-custom:
# 	cd silk && $(MAKE) clean

$(BIN): $(LINKOBJ) silk
	gcc -shared $(LINKOBJ) -static -o $(BIN) $(LIBS) -Wl,--add-stdcall-alias

silk:
	cd silk && $(MAKE) -j 8 lib

%.o: %.c
	$(CC) -c $(notdir $(basename $@)).c $(CFLAGS) -o $@