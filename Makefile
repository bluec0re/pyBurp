include Makefile.inc

burp_python.jar: 
	$(MAKE) -C src $@
	mv src/$@ .

.PHONY: clean

clean:
	$(MAKE) -C src clean
	rm -f burp_python.jar
