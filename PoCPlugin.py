# vim: set ts=4 sw=4 tw=79 fileencoding=utf-8:
from __future__ import absolute_import

from bluec0re import ICallback
from burp import IMenuItemHandler
from java.io import File
from javax.swing import JFileChooser, JOptionPane

class PocPlugin(ICallback):
    def __str__(self):
        return type(self).__name__

    def registerExtenderCallbacks(self, callbacks):
        callbacks.registerMenuItem('Save PoC', Handler())

class Handler(IMenuItemHandler):
    def menuItemClicked(self, menuItemCaption, messageInfo):
        if menuItemCaption == 'Save PoC':

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
