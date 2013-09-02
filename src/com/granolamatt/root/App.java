package com.granolamatt.root;

import com.granolamatt.hardware.HardwareMemory;
import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.logger.LoggerOut;
import java.io.IOException;
import java.net.URI;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class App {

    private static String bindAddress = "localhost";
    private static Server server;
    private static final int port = 7023;
    private final static List<ActiveModule> moduleList = new LinkedList<>();

    public static class RestContainer implements Container {

        private final Executor executor;

        public RestContainer(int size) {
            this.executor = Executors.newFixedThreadPool(size);
        }

        @Override
        public void handle(Request request, Response response) {
            Task task = new Task(request, response, moduleList);
            executor.execute(task);
        }
    }

    static void startServer() throws IOException {

        Container container = new RestContainer(10);
        server = new ContainerServer(container);
        Connection connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(port);

        connection.connect(address);

        ActiveModule root = new ActiveModule();
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
        ActiveModule am = new ActiveModule(file);
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

//    private static void testGPIO() {
//        HardwareMemory.InpGPIO(4); // must use INP_GPIO before we can use OUT_GPIO
//        HardwareMemory.OutGPIO(4);
//        HardwareMemory.InpGPIO(17); // must use INP_GPIO before we can use OUT_GPIO
//        HardwareMemory.OutGPIO(17);
//        HardwareMemory.InpGPIO(27); // must use INP_GPIO before we can use OUT_GPIO
//        HardwareMemory.OutGPIO(27);
//
//
//        for (int rep = 0; rep < 10; rep++) {
//            try {
//                HardwareMemory.GPIOSetPinNumber(4);
//                Thread.sleep(1000);
//                HardwareMemory.GPIOSetPinNumber(17);
//                Thread.sleep(1000);
//                HardwareMemory.GPIOSetPinNumber(27);
//                Thread.sleep(1000);
//                HardwareMemory.GPIOClrPinNumber(4);
//                Thread.sleep(1000);
//                HardwareMemory.GPIOClrPinNumber(17);
//                Thread.sleep(1000);
//                HardwareMemory.GPIOClrPinNumber(27);
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//    private static void testI2C() {
//        { 
//    int rxBuffer[] = new int[8];
//    int txBuffer[] = new int[8];
//    
//
//    HardwareMemory.gpioI2cSetup();
//   
//    HardwareMemory.gpioI2cSet7BitSlave(0x68);
//
//    txBuffer[0] = 0;
//
//    HardwareMemory.gpioI2cWriteData(new int[]{0});
//
//    HardwareMemory.gpioI2cReadData(rxBuffer);
//
//            System.out.println("RTC values:");
//    for (int count = 0;count<8;count++) {
//        System.out.println("reg:" + count  + " : " + rxBuffer[count]);
//    }    
//
//    HardwareMemory.gpioI2cCleanup();
//} 
//
//    }
    public static void main(String[] args) throws IOException {
        HardwareMemory.loadDriver();
//        testI2C();


        if (args.length > 0) {
            bindAddress = args[0];
        }
        System.out.println("\"Hello World\" Jersey Example Application");

        startServer();

        LoggerOut.println("Application started.\n");
        System.out.println(
                "Try accessing " + getBaseURI() + "root in the browser.\n");
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
    }

    public static void getModulesInfo(BasicDocument doc) {
        synchronized (moduleList) {
            for (ActiveModule module : moduleList) {
                doc.addContent(module.getInfo());
            }
        }
    }

    public static URI getBaseURI() {
        URI uri = null;
        try {
            uri = new URI("http://" + bindAddress + "/:" + port);
        } catch (URISyntaxException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uri;
//        return UriBuilder.fromUri("http://" + bindAddress + "/").port(getPort(7023)).build();
    }
}
