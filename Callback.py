from bluec0re import ICallback

class Callback(ICallback):
    
    def __str__(self):
        return __name__ + '.' + type(self).__name__

    def toString(self):
        return str(self)

    def applicationClosing(self):
        print "closing..."
