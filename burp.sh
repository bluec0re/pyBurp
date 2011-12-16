#!/bin/sh

BURP_PATH=
JYTHON_PATH=
PLUGIN_PATH=
CP="$PLUGIN_PATH/burp_python.jar:$JYTHON_PATH/jython.jar:$BURP_PATH/burpsuite_pro_v1.4.05.jar"

java -Xmx1536m -classpath $CP burp.StartBurp --python-home=$JYTHON_PATH/Lib $@ |& while read line
do
    echo "$line" | grep Listen > /dev/null
    if [[ $? -eq 0 ]]
    then
        PORT=$(echo "$line" | sed 's/.*://')
        echo -e "cd $PLUGIN_PATH\nadd CmdlinePlugin\nadd PoCPlugin\ncd $(pwd)\nquit\n" | \
            nc localhost $PORT
    fi
    echo "$line"
done
