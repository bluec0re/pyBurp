Use Python for Burp plugins
---------------------------
Burp [`<http://portswigger.net/burp/>`_] has an inbuilt plugin functionality. Because Burp is written in Java, it
only supports Java classes as plugins. Additionaly, Burp allows only to use one
plugin at the same time and it has to be loaded on start-up. 

Now we have written a Burp-Python proxy (pyBurp) which enables us to load and unload
multiple plugins at runtime and written in Python.

The only restriction is that we have to use Jython for compiling and
interpreting the python scripts. Actually we have tested it with Jython 2.5.2.
Other versions might work, but it isn't guaranteed.


To use this plugin, you should follow these steps:

#. download and extract/install `jython <www.jython.org>`_ (2.5.2)
#. update the paths in the *build.xml* for your Burp copy and jython.jar
    #. run ``ant`` (requires a jdk installed, you can also download a
           precompiled jar from `here <http://www.ernw.de/download/burp_python.jar>`_)
#. update the paths in the *burp.sh* corresponding to 2.
#. start Burp with *burp.sh*
    * Burp should have told you on which port he waits for load and unload commands (eg. 55666)
#. Load a Pythonplugin:
    
.. code-block:: none
    :emphasize-lines: 2,4,5,8,11

    $ nc localhost 55666            # or some other port reported at startup
    pwd                             # shows you the current workingdir to load from
    /home/foo
    cd /home/foo/sources/pyBurp     # change the current workingdir
    add PoCPlugin                   # load the PoC-Plugin
    adding PoCPlugin
    done
    list                            # show loaded Plugins
    Callback list:
    1: PocPlugin
    quit                            # quit communication
    Bye
    $

#. Remove a Pythonplugin:

.. code-block:: none
    :emphasize-lines: 2,5,8

    $ nc localhost 55666            # or some other port reported at startup
    list                            # show loaded Plugins
    Callback list:
    1: PoCPlugin
    rm 1                            # remove Plugin with no. 1
    removing PoCPlugin
    done
    quit                            # quit communication
    Bye
    $

Possible commands:

======= ======================== ===========
Command Syntax                   Description
======= ======================== ===========
add     add <plugin name>        loads a plugin
list    list                     lists all loaded plugins
rm      rm <nr of loaded plugin> unloads a plugin
cd      cd <absolute path>       change current load path
pwd     pwd                      prints current load path
======= ======================== ===========

