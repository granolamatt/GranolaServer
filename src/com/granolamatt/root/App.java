package com.granolamatt.root;

import com.granolamatt.dynamicloader.DefaultApplication;
import com.granolamatt.hardware.HardwareMemory;
import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.logger.LoggerOut;
import com.granolamatt.widgets.SVGFixer;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class App {

    private static String bindAddress = "127.0.0.1";
    private static HttpServer server;
    private final static List<ActiveModule> moduleList = new LinkedList<>();

    static void startServerGrizzly() throws IOException {

        final ResourceConfig resourceConfig = new DefaultApplication();

        server = GrizzlyHttpServerFactory.createHttpServer(getBaseURI());

        // Map the path to the processor.
        final ServerConfiguration config = server.getServerConfiguration();

        CLStaticHttpHandler classHandler = new CLStaticHttpHandler(App.class.getClassLoader());
        config.addHttpHandler(classHandler, "/resources");

        // create a handler wrapping the JAX-RS application        
        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(resourceConfig, HttpHandler.class);
        config.addHttpHandler(handler, getBaseURI().getPath());

        server.start();

        ActiveModule root = new ActiveModule(server);
        synchronized (moduleList) {
            moduleList.add(root);
        }

        loadFromDir();

    }

    public static void loadFromDir() {
        File uploadDir = new File("/opt/granola");
        if (uploadDir.exists() && uploadDir.isDirectory()) {
            File[] files = uploadDir.listFiles();
            for (File testJar : files) {
                if (testJar.getName().endsWith(".jar")) {
                    addModule(testJar);
                }
            }
        }

    }

    public static void addModule(File file) {
        ActiveModule am = new ActiveModule(server, file);
        synchronized (moduleList) {
            moduleList.add(am);
        }
    }

    public static void removeModule(String moduleContext) {
        synchronized (moduleList) {
            LinkedList<ActiveModule> removeList = new LinkedList<>();
            for (ActiveModule module : moduleList) {
                if (module.getContext().equals(moduleContext)) {
                    removeList.add(module);
                    module.stopModule();
                }
            }
            for (ActiveModule module : removeList) {
                moduleList.remove(module);
            }
        }
//        server.removeContext("/" + moduleContext);
        File jarFile = new File("/opt/granola/" + moduleContext + ".jar");
        if (jarFile.exists()) {
            jarFile.delete();
        }
    }

    public static void main(String[] args) throws IOException {
        HardwareMemory.loadDriver();
//        testI2C();

        if (args.length > 0) {
            bindAddress = args[0];
        }
        System.out.println("\"Hello World\" Jersey Example Application");

        startServerGrizzly();

        LoggerOut.println("Application started.\n");
        System.out.println(
                "Try accessing " + getBaseURI() + "resources/com/granolamatt/resources/tuto3.svg " + "root in the browser.\n");
        final Thread thisThread = Thread.currentThread();
        Thread stop = new Thread() {
            @Override
            public void run() {
                synchronized (thisThread) {
                    thisThread.notify();
                }
                try {
                    thisThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(stop);
        synchronized (thisThread) {
            try {
                thisThread.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //      server.stop(0);
    }

    public static void getModulesInfo(BasicDocument doc) {
        synchronized (moduleList) {
            for (ActiveModule module : moduleList) {
                doc.addContent(module.getInfo());
            }
        }
    }

    private static int getPort(int defaultPort) {
        final String port = System.getProperty("jersey.config.test.container.port");
        if (null != port) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
                System.out.println("Value of jersey.config.test.container.port property"
                        + " is not a valid positive integer [" + port + "]."
                        + " Reverting to default [" + defaultPort + "].");
            }
        }
        return defaultPort;
    }

    public static URI getBaseURI() {
        return UriBuilder.fromUri("http://" + bindAddress + "/").port(getPort(7023)).build();
    }
}
