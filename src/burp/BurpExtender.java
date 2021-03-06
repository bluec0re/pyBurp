package burp;

import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import bluec0re.*;

public class BurpExtender implements IBurpExtender {

    private IBurpExtenderCallbacks callbacks;
    private String[] args;
    private ArrayList<ICallback> pyCallbacks;
    private int port = 55666;
    private JythonObjectFactory factory = JythonObjectFactory.getInstance();

    private Thread server = new Thread(new Runnable() {
        public void run() {
            ServerSocket serverSocket = null;
            boolean listen = true;
            while(listen) {
                try {
                    byte[] localhost = {
                        127,0,0,1
                    };
                    serverSocket = new ServerSocket(port, 0, InetAddress.getByAddress(localhost));
                    listen = false;
                    System.out.println("Listen on 127.0.0.1:" + port);
                } catch (IOException e) {
                    if (port > 55700) {
                        System.out.println("Could not listen on port: " + port);
                        return;
                    } else
                        port++;
                }
            }
            try 
            {
                while(true) {
                    Socket clientSocket = null;
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (IOException e) {
                        System.out.println("Accept failed: " + port);
                        break;
                    }

                    PrintWriter out = new PrintWriter(
                                        clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                                            new InputStreamReader(
                                                clientSocket.getInputStream()));
                    String inputLine, outputLine;

                    while ((inputLine = in.readLine()) != null) {   
                        if (inputLine.equals("quit")) {
                            out.println("Bye");
                            break;
                        }
                        if (inputLine.equals(""))
                            continue;
                        
                        if(inputLine.equals("help")) {
                            out.println("Commands:");
                            out.println("help list add rm cd pwd quit");
                        }
                        else if(inputLine.equals("list")) {
                            out.println("Callback list:");
                            int i = 1;
                            for(ICallback cb : pyCallbacks) {
                                out.print("" + i + ": ");
                                out.println(cb.toString());
                                i++;
                            }
                        }
                        else if (inputLine.startsWith("add ")) {
                            String modulename = inputLine.substring(4);
                            out.println("adding " + modulename);
                            try {
                                ArrayList<Object> list =  factory.createObject(
                                        ICallback.class, modulename);
                                for(Object o : list) {
                                    System.out.println("Loaded " + o);
                                    BurpExtender.this.addCallback((ICallback)o);
                                }
                                out.println("done");
                            } catch(Exception e) {
                                out.println(e.toString());
                            }
                        } else if (inputLine.startsWith("rm ")) {
                            int idx = Integer.parseInt(inputLine.substring(3));
                            try
                            {
                                ICallback obj = pyCallbacks.get(idx - 1);
                                out.println("removing " + obj);
                                pyCallbacks.remove(obj);
                                out.println("done");
                            } catch(Exception e) {}
                        } else if (inputLine.startsWith("cd ")) {
                            String cd = inputLine.substring(3);
                            if (cd.charAt(0) == '/' || (cd.length() > 1 && cd.charAt(1) == ':'))
                                System.setProperty("user.dir", cd);
                            else
                                System.setProperty("user.dir", System.getProperty("user.dir") + cd);
                        } else if (inputLine.equals("pwd")) {
                            out.println(System.getProperty("user.dir"));
                        } else {
                            out.println("Unknown: " + inputLine);
                        }
                    }
                    out.close();
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("Error on communication");
                return;
            }
        }
    }, "burp-server");
    
    public BurpExtender() {
        pyCallbacks = new ArrayList<ICallback>();
        this.server.start();
        System.out.println("Burp Python loaded. CWD: " + System.getProperty("user.dir"));
    }

    public void addCallback(ICallback callback) {
        callback.setCommandLineArgs(this.args);
        callback.registerExtenderCallbacks(this.callbacks);
        this.pyCallbacks.add(callback);
    }

    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        this.callbacks = callbacks;
        callbacks.issueAlert("Listening for plugin commands on port " + this.port);
//        callbacks.registerMenuItem("my menu item", new CustomMenuItem());
        for(ICallback cb : pyCallbacks) {
            cb.registerExtenderCallbacks(callbacks);
        }


        System.out.println("Load autoload.py");
        try {
            ArrayList<Object> list =  factory.createObject(
                    ICallback.class, "autoload");
            for(Object o : list) {
                System.out.println("Loaded " + o);
                this.addCallback((ICallback)o);
            }
            System.out.println("done");
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public byte[] processProxyMessage(
            int messageReference,
            boolean messageIsRequest,
            String remoteHost,
            int remotePort,
            boolean serviceIsHttps,
            String httpMethod,
            String url,
            String resourceType,
            String statusCode,
            String responseContentType,
            byte[] message,
            int[] action) {
        for(ICallback cb : pyCallbacks) {
            message = cb.processProxyMessage(
                    messageReference,
                    messageIsRequest,
                    remoteHost,
                    remotePort,
                    serviceIsHttps,
                    httpMethod,
                    url,
                    resourceType,
                    statusCode,
                    responseContentType,
                    message,
                    action);
        }
        return message;
    }

    public void applicationClosing() {
        for(ICallback cb : pyCallbacks) {
            cb.applicationClosing();
        }
    }

    public void processHttpMessage(String toolName, boolean messageIsRequest, 
            IHttpRequestResponse messageInfo) {
        for(ICallback cb : pyCallbacks) {
            cb.processHttpMessage(toolName, messageIsRequest, messageInfo);
        }
    }

    public void newScanIssue(IScanIssue issue) {
        for(ICallback cb : pyCallbacks) {
            cb.newScanIssue(issue);
        }
    } 

    public void setCommandLineArgs(String[] args) {
        this.args = args;
        for(ICallback cb : pyCallbacks) {
            cb.setCommandLineArgs(args);
        }
        for(String arg : args) {
            if(arg.startsWith("--python-home=")) {
                System.setProperty("python.home", arg.substring("--python-home=".length()));
            }
        }
    }

    public static void main(String[] args) {
/*
    // Obtain an instance of the object factory
    onObjectFactory factory = JythonObjectFactory.getInstance();

    // Call the createObject() method on the object factory by
    // passing the Java interface and the name of the Jython module
    // in String format. The returning object is casted to the the same
    // type as the Java interface and stored into a variable.
    IBurpExtender ext = (IBurpExtender) factory.createObject(
        IBurpExtender.class, "Extender");
    // Populate the object with values using the setter methods
    System.out.println(ext);
*/
        BurpExtender b = new BurpExtender();
    }

}
