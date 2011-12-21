Use Python for Burp plugins
---------------------------
Burp has an inbuilt plugin functionality. Due to it is written in Java, the
plugins also have to be written in Java. Additional there could be only one
Plugin, which is only loaded at start-up. 

Now we have written a Burp-Python proxy which enables us to hotload and unload
multiple plugins at a time.

The only restriction is that we have to use Jython for compiling and
interpreting the python scripts. Actually we have tested it with Jython 2.5.2.
Other version might work, but it isn't guaranteed.


To use this plugin, you have should follow these steps:

#. install jython (2.5.2)
#. update the paths in the *Makefile* for your Burp copy and jython.jar
#. update the paths in the *burp.sh* corresponding to 2.
#. start Burp with *burp.sh*
    * Burp should now told you on which port he waits for load and unload commands (eg. 55666)
#. Load a Pythonplugin::
    
    $ nc localhost 55666             # or some other port reported at startup
    pwd                              # shows you the current workingdir to load from
    /home/bluec0re
    cd /home/bluec0re/sources/pyBurp # change the current workingdir
    add PoCPlugin                    # load the PoC-Plugin
    adding PoCPlugin
    done
    list                             # show loaded Plugins
    Callback list:
    1: org.python.proxies.PoCPlugin$PocPlugin$1@432dbb4b
    quit                             # quit communication
    Bye
    $

#. Remove a Pythonplugin::

    $ nc localhost 55666             # or some other port reported at startup
    list                             # show loaded Plugins
    Callback list:
    1: org.python.proxies.PoCPlugin$PocPlugin$1@432dbb4b
    rm 1                             # remove Plugin with no. 1
    removing org.python.proxies.PoCPlugin$PocPlugin$1@432dbb4b
    done
    quit                             # quit communication
    Bye
    $

