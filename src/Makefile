include ../Makefile.inc

CP="$(BURP_PATH)/$(BURP_JAR):$(JYTHON_PATH)/jython.jar:."

CLASS_FILES=burp/BurpExtender.class bluec0re/ICallback.class bluec0re/JythonObjectFactory.class


burp_python.jar: $(CLASS_FILES)
	$(JAR) cvf $@ $^

%.class: %.java
	$(JAVAC) -classpath $(CP) $?


.PHONY: clean

clean:
	rm -f burp/*.class bluec0re/*.class *.class burp_python.jar
