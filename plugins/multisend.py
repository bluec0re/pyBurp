
from burp import IMenuItemHandler
from bluec0re import ICallback

class MultiSend(ICallback, IMenuItemHandler):

    def registerExtenderCallbacks(self, callbacks):
        self.callbacks = callbacks
        self.i = 0
        callbacks.registerMenuItem('Send to intruder (all Items)', self)
        callbacks.registerMenuItem('Send to repeater (all Items)', self)

    def menuItemClicked(self, caption, msginfo):
        if caption == 'Send to intruder (all Items)':
            for req_resp in msginfo:
                req = req_resp.getRequest()
                self.callbacks.sendToIntruder(req_resp.getHost(),
                                              req_resp.getPort(),
                                              req_resp.getProtocol() == 'https',
                                              req)
        elif caption == 'Send to repeater (all Items)':
            for req_resp in msginfo:
                self.i += 1
                req = req_resp.getRequest()
                self.callbacks.sendToRepeater(req_resp.getHost(),
                                              req_resp.getPort(),
                                              req_resp.getProtocol() == 'https',
                                              req,
                                              "Request " + self.i)

