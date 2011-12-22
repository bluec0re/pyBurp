# vim: set ts=4 sw=4 tw=79 fileencoding=utf-8:
from __future__ import absolute_import

from bluec0re import ICallback
from java.io import File

class CmdlinePlugin(ICallback):
    def __str__(self):
        return type(self).__name__

    def setCommandLineArgs(self, args):
        self.args = args

    def registerExtenderCallbacks(self, callbacks):
        if len(self.args) > 1 and not self.args[-1].startswith('-'):
            f = File(self.args[-1])
            if f.exists():
                callbacks.restoreState(f)
    def toString(self):
        return 'CmdlinePlugin'
