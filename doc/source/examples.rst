.. pyWCFBin documentation master file, created by
   sphinx-quickstart on Tue Dec 20 14:29:37 2011.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Examples
========
.. highlight:: python
    :linenothreshold: 5

Plugin for loading Burp state files from commandline:

.. literalinclude:: ../../CmdlinePlugin.py

Plugin for saving request/response items in a file (without the Burp-XML around it):

.. literalinclude:: ../../PoCPlugin.py

The Java class which have to be implemented by the Python scripts (based on 
`IBurpExtender <http://portswigger.net/burp/extender/burp/IBurpExtender.html>`_):

.. literalinclude:: ../../bluec0re/ICallback.java
    :language: java

A startscript for Burp Suite with autoloading **CmdlinePlugin** and **PoCPlugin**:

.. literalinclude:: ../../burp.sh
    :language: bash
