# vim: set ts=4 sw=4 tw=79 fileencoding=utf-8:
from __future__ import absolute_import

from bluec0re import ICallback
from burp import IMenuItemHandler
from java.io import File
from javax.swing import JFileChooser, JOptionPane

class CmdlinePlugin(ICallback):
    def __str__(self):
        return type(self).__name__

    def setCommandLineArgs(self, args):
        self.args = args

    def registerExtenderCallbacks(self, callbacks):
        self.autosave = None
        self.callbacks = callbacks
        if len(self.args) > 0 and not self.args[-1].startswith('-'):
            fname = self.args[-1].strip("'")
            f = File(fname)
            if f.exists():
                callbacks.issueAlert('Loading %s..' % fname)
                callbacks.restoreState(f)
                conf = callbacks.saveConfig()
                self.autosave = conf['suite.doAutoSave']
                conf['suite.doAutoSave'] = 'false'
                active_type = conf['scanner.activecustomscopetype']
                conf['scanner.activecustomscopetype'] = '0'
                callbacks.loadConfig(conf)
                print "Deactivate doAutoSave (%s -> false)" % (self.autosave,)
                print "Deactivate active scanner (%s -> 0)" % (active_type,)
            else:
                print "File %s not found" % fname
        if self.autosave is None:
            conf = callbacks.saveConfig()
            print "Activate doAutoSave (%s -> true)" % (conf['suite.doAutoSave'],)
            conf['suite.doAutoSave'] = 'true'
            conf['suite.autoSaveFolder'] = '/mnt/ernw/burp_backup'
            conf['suite.autoSaveInterval'] = '45'
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
            
class PocPlugin(ICallback):
    def __str__(self):
        return type(self).__name__

    def registerExtenderCallbacks(self, callbacks):
        callbacks.registerMenuItem('Save PoC', Handler())

    def toString(self):
        return 'PoCPlugin'

class Handler(IMenuItemHandler):
    def __init__(self, *args, **kwargs):
        super(Handler, self).__init__(*args, **kwargs)
        self.dir = None

    def menuItemClicked(self, menuItemCaption, messageInfo):
        if menuItemCaption == 'Save PoC':

            if self.dir:
                fc = JFileChooser(self.dir)
            else:
                fc = JFileChooser()

            returnVal = fc.showSaveDialog(None)

            if returnVal == JFileChooser.APPROVE_OPTION:
                file = fc.getSelectedFile()

                try:
                    mode = 'w'
                    if file.exists():
                        res = JOptionPane.showConfirmDialog(None, 
                                "File exists. Append?")
                        mode = {
                                JOptionPane.YES_OPTION : 'a',
                                JOptionPane.NO_OPTION : 'w',
                                JOptionPane.CANCEL_OPTION : None,
                                }[res]

                    if not mode:
                        return
                    fp = open(str(file.getAbsolutePath()), mode)
                    for req_resp in messageInfo:
                        req = req_resp.getRequest()
                        resp = req_resp.getResponse()
                        fp.write(req.tostring())
                        fp.write('\r\n\r\n')
                        fp.write(resp.tostring())
                        fp.write('\r\n\r\n')
                    fp.close()
                except Exception, e:
                    JOptionPane.showMessageDialog(None, 
                            "Error during save: " + str(e), "Error",  
                            JOptionPane.ERROR_MESSAGE)
                    raise

                JOptionPane.showMessageDialog(None, 
                            "File was successfully saved", "Ok",  
                            JOptionPane.INFORMATION_MESSAGE)
                self.dir = fc.getCurrentDirectory()
