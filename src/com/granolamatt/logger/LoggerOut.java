/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.logger;

import com.granolamatt.htmlhelpers.GetFunctions;
import com.granolamatt.root.App;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author root
 */
public class LoggerOut {

    private static final StringBuilder outString = new StringBuilder();
    private static int readLength = 0;
    private static final int totalLength = 100000;
    private static boolean printURISet = false;
    private static URI printURI = App.getBaseURI();
//    private static URI printURI = UriBuilder.fromUri("http://192.168.0.29/").port(8080).build();
    private static ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public static void println(final String line) {
        Runnable printTask = new Runnable() {
            @Override
            public void run() {
                if (!printURISet) {
                    synchronized (outString) {
                        outString.append(line).append("<br>");
                        outString.notify();
                    }
                } else {
                    // LoggerOut.println("Logging to remote server " + printURI);
                    try {
                        final String send = URLEncoder.encode(line, GetFunctions.encString);
                        String sending = UriBuilder.fromUri(printURI).path("/logging/stdout").queryParam("println", send).build(send).toString();
                        int result = GetFunctions.sendGet(sending);
                        if (result != 200) {
                            System.out.println("Not logging to url");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(LoggerOut.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        threadPool.submit(printTask);
    }

    public static void print(final String line) {
        Runnable printTask = new Runnable() {
            @Override
            public void run() {
                if (!printURISet) {
                    synchronized (outString) {
                        outString.append(line);
                        outString.notify();
                    }
                } else {
                    try {
                        final String send = URLEncoder.encode(line, GetFunctions.encString);
                        String sending = UriBuilder.fromUri(printURI).path("/logging/stdout").queryParam("print", send).build(send).toString();
                        int result = GetFunctions.sendGet(sending);
                        if (result != 200) {
                            System.out.println("Not logging to url");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(LoggerOut.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        threadPool.submit(printTask);
    }

    public static void setPrintURI(URI uri) {
        printURISet = true;
        printURI = uri;
    }

    public static String getStringNoWait() {
        synchronized (outString) {
            String ret = outString.toString();
            if (outString.length() > totalLength) {
                outString.delete(0, outString.length() - totalLength);
            }
            readLength = outString.length();
            return ret;
        }
    }

    public static String getString() {
        synchronized (outString) {
            while (outString.length() == readLength) {
                try {
                    outString.wait();
                } catch (InterruptedException ex) {
                }
            }

            String ret = outString.toString();
            if (outString.length() > totalLength) {
                outString.delete(0, outString.length() - totalLength);
            }
            readLength = outString.length();

            return ret;
        }
    }
}
