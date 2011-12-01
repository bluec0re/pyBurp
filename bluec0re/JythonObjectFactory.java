package bluec0re;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.python.core.PyObject;
import org.python.core.PyString;
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

    /**
     * The createObject() method is responsible for the actual creation of the
     * Jython object into Java bytecode.
     */
    public static Object createObject(Object interfaceType, String moduleName){
        Object javaInt = null;
        // Create a PythonInterpreter object and import our Jython module
        // to obtain a reference.
        PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());
        PySystemState sys = interpreter.getSystemState();
        String home = System.getProperty("python.home");
        int idx = home != null ? home.indexOf(':') : -1;
        while(idx != -1) {
            sys.path.append(new PyString(home.substring(0, idx)));
            home = home.substring(idx);
        }
        if (home != null && !"".equals(home)) {
            sys.path.append(new PyString(home));
        }
        sys.path.append(new PyString(System.getProperty("user.dir")));
        interpreter.exec("from " + moduleName + " import " + moduleName);

        pyObject = interpreter.get(moduleName);

        try {
            // Create a new object reference of the Jython module and
            // store into PyObject.
            PyObject newObj = pyObject.__call__();
            // Call __tojava__ method on the new object along with the interface name
            // to create the java bytecode
            javaInt = newObj.__tojava__(Class.forName(interfaceType.toString().substring(
                        interfaceType.toString().indexOf(" ")+1,
                        interfaceType.toString().length())));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JythonObjectFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return javaInt;
    }

}
