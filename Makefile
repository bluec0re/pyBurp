JAVAC=javac
JAR=jar
CP=/home/bluec0re/Apps/burpsuite_pro_v1.4.05.jar:/opt/jython/jython.jar:. 

burp_python.jar: burp/BurpExtender.class bluec0re/ICallback.class
	$(JAR) cvf $@ bluec0re/*.class burp/*.class

burp/BurpExtender.class: burp/BurpExtender.java
	$(JAVAC) -classpath $(CP) $^

bluec0re/ICallback.class: bluec0re/ICallback.java
	$(JAVAC) -classpath $(CP) $^

.PHONY: clean

clean:
	rm -f burp/*.class bluec0re/*.class *.class burp_python.jar
