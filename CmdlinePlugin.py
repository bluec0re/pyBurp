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
        self.autosave = None
        self.callbacks = callbacks
        if len(self.args) > 1 and not self.args[-1].startswith('-'):
            fname = self.args[1].strip("'")
            f = File(fname)
            if f.exists():
                callbacks.issueAlert('Loading %s..' % fname)
                callbacks.restoreState(f)
                conf = callbacks.saveConfig()
                self.autosave = True if conf['suite.doAutoSave'] else False
                conf['suite.doAutoSave'] = 'false'
                active_type = conf['scanner.activecustomscopetype']
                conf['scanner.activecustomscopetype'] = '0'
                callbacks.loadConfig(conf)
                print "Deactivate doAutoSave (%s -> false)" % (self.autosave,)
                print "Deactivate active scanner (%s -> 0)" % (active_type,)
        if self.autosave is None:
            conf = callbacks.saveConfig()
            print "Activate doAutoSave (%s -> true)" % (conf['suite.doAutoSave'],)
            conf['suite.doAutoSave'] = 'true'
            active_type = conf['scanner.activecustomscopetype']
            conf['scanner.activecustomscopetype'] = '0'
            print "Deactivate active scanner (%s -> 0)" % (active_type,)
            callbacks.loadConfig(conf)


    def toString(self):
        return 'CmdlinePlugin'

    def applicationClosing(self):
        if self.autosave is not None:
            conf = callbacks.saveConfig()
            conf['suite.doAutoSave'] = self.autosave
            callbacks.loadConfig(conf)
            print "Reset doAutoSave to %s" % (self.autosave,)
            
