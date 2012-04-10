#!/bin/sh

BURP_PATH=
BURP_JAR=burpsuite_pro_v1.4.05.jar
JYTHON_PATH=
PLUGIN_PATH=
CP="$PLUGIN_PATH/burp_python.jar:$JYTHON_PATH/jython.jar:$BURP_PATH/$BURP_JAR"

if [[ ! -d "$JYTHON_PATH/Lib" || ! -f "$JYTHON_PATH/jython.jar" ]]
then
    echo -e "[ \e[31m-\e[0m ] Invalid jython path \e[36m$JYTHON_PATH\e[0m"
    exit 1
fi

if [[ ! -d "$BURP_PATH" || ! -f "$BURP_PATH/$BURP_JAR" ]]
then
    echo -e "[ \e[31m-\e[0m ] Invalid burp path \e[36m$BURP_PATH\e[0m \e[93mOR\e[0m"
    echo -e "[ \e[31m-\e[0m ] Invalid burp jar \e[36m$BURP_JAR\e[0m"
    exit 1
fi

if [[ ! -f "$PLUGIN_PATH/autoload.py" && ! -f "autoload.py" ]]
then
    echo -e "[ \e[31m-\e[0m ] autoload.py not found in \e[36m$PLUGIN_PATH\e[0m or \e[36m$(pwd)\e[0m"
fi

java -Xmx1536m -classpath $CP burp.StartBurp --python-home=$JYTHON_PATH/Lib $@ 
