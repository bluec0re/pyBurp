@echo off

SET BURP_PATH=
SET BURP_JAR=burpsuite_pro_v1.4.05.jar
SET JYTHON_PATH=
SET PLUGIN_PATH=
SET CP="%PLUGIN_PATH%/burp_python.jar:%JYTHON_PATH%/jython.jar:%BURP_PATH%/%BURP_JAR%"

java -Xmx1536m -classpath "%CP%" burp.StartBurp --python-home="%JYTHON_PATH%/Lib" %*
