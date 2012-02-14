from bluec0re import ICallback

class Test(ICallback):
    
    def __str__(self):
        return __name__ + '.' + type(self).__name__

    def toString(self):
        return str(self)

    def processProxyMessage(self, *args):
        for arg in args:
            if arg != args[10]:
                print arg

        return args[10]

    def setCommandLineArgs(self, args):
        for a in args:
            print a
    
    def registerExtenderCallbacks(self, callbacks):
        print callbacks

    def processHttpMessage(self, toolName, is_request, msg_info):
        print toolName, is_request, msg_info

    def newScanIssue(self, issue):
        print issue

    def applicationClosing(self):
        print "closing..."
        
class Test2(ICallback):
    
    def __str__(self):
        return __name__ + '.' + type(self).__name__

    def toString(self):
        return str(self)


