.. pyWCFBin documentation master file, created by
   sphinx-quickstart on Tue Dec 20 14:29:37 2011.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to pyWCFBin's documentation!
====================================

Contents:

.. toctree::
   :maxdepth: 2

   blogpost.rst

Examples
========
.. highlight:: python
    :linenothreshold: 5

Plugin for loading Burp state files from commandline:

.. literalinclude:: ../../CmdlinePlugin.py

Plugin for saving request/response items in a File (without the Burp-XML around it):

.. literalinclude:: ../../PoCPlugin.py

The Java class who have to be implemented by the Python scripts\:

.. literalinclude:: ../../bluec0re/ICallback.java
    :language: java


Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

