package bluec0re;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.net.URLDecoder;
import java.io.File;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyList;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;
import org.python.core.PySystemState;

/**
 * Object factory implementation that is defined
 * in a generic fashion.
 *
 */

public class JythonObjectFactory {
    private static JythonObjectFactory instance = null;
    private static PyObject pyObject = null;

    protected JythonObjectFactory() {

    }
    /**
     * Create a singleton object. Only allow one instance to be created
     */
    public static JythonObjectFactory getInstance(){
        if(instance == null){
            instance = new JythonObjectFactory();
        }

        return instance;
    }

    private static String getJarPath() {
        String path = JythonObjectFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch(java.io.UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String getJarFolder() {
        String path = getJarPath();
        int idx = path.lastIndexOf(File.separatorChar);
        if(idx == -1)
            idx = path.lastIndexOf('/');
        return path.substring(0, idx);
    }

    /**
     * The createObject() method is responsible for the actual creation of the
     * Jython object into Java bytecode.
     */
    public static ArrayList<Object> createObject(Object interfaceType, String moduleName){
        Object javaInt = null;
        // Create a PythonInterpreter object and import our Jython module
        // to obtain a reference.
        PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());
        PySystemState sys = interpreter.getSystemState();

        // add the library paths to sys.path
        String home = System.getProperty("python.home");
        int idx = home != null ? home.indexOf(':') : -1;
        while(idx != -1) {
            sys.path.append(new PyString(home.substring(0, idx)));
            home = home.substring(idx);
        }
        if (home != null && !"".equals(home)) {
            sys.path.append(new PyString(home));
        }

        // append jar location to sys.path
        sys.path.append(new PyString(getJarFolder()));

        // append current working dir to sys.path
        sys.path.append(new PyString(System.getProperty("user.dir")));

        // load modules
        interpreter.exec("import inspect\nfrom bluec0re import ICallback");
        interpreter.exec("import " + moduleName);

        // get list of ICallback implementing classes
        PyList list = (PyList)interpreter.eval("[v for v in inspect.getmembers(" + moduleName + ", inspect.isclass) if issubclass(v[1], ICallback) and v[1] != ICallback]"); 

        ArrayList<Object> classes = new ArrayList<Object>();

        for(int i = 0; i < list.__len__(); i++) {
            pyObject = ((PyTuple)list.get(i)).pyget(1);
            try {
                // Create a new object reference of the Jython module and
                // store into PyObject.
                PyObject newObj = pyObject.__call__();
                // Call __tojava__ method on the new object along with the interface name
                // to create the java bytecode
                javaInt = newObj.__tojava__(Class.forName(interfaceType.toString().substring(
                            interfaceType.toString().indexOf(" ")+1,
                            interfaceType.toString().length())));
                classes.add(javaInt);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JythonObjectFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return classes;
    }

}
