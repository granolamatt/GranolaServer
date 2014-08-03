/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root;

import com.granolamatt.dynamicloader.JaxRsDynamicLoader;
import com.granolamatt.hardware.RestHardware;
import com.granolamatt.logger.LoggerOut;
import com.granolamatt.logger.RestLogger;
import static com.granolamatt.root.App.getBaseURI;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.simple.SimpleServer;

/**
 *
 * @author root
 */
public class ActiveModule {

    private final String context;
    private JaxRsDynamicLoader dynamicLoader = null;
    private SimpleServer server = null;
    private final StringBuilder moduleInfo = new StringBuilder();
    private final boolean debug = true;

    public ActiveModule(SimpleServer server) {
        this.server = server;
        this.context = "";

        synchronized (moduleInfo) {
            moduleInfo.append("<p>");
            moduleInfo.append("<br>\n");
            moduleInfo.append("Context /");
            moduleInfo.append("<br>\n");
            moduleInfo.append("Base ");
            addHyperLink("");
            moduleInfo.append("<br>\n");

            loadJAX(RestRoot.class);
            loadJAX(RestLogger.class);
            loadJAX(RestHardware.class);

            moduleInfo.append("<br>");
            moduleInfo.append("</p>\n");
        }

    }

//    public ActiveModule(SimpleServer httpServer, File file) {
//        this.server = httpServer;
//        String jarName = file.getName();
//        context = jarName.replaceAll(".jar", "").replaceAll(" ", "");
//        dynamicLoader = new JaxRsDynamicLoader(file);
//        HttpHandler dynamicHandler = ContainerFactory.createContainer(HttpHandler.class, dynamicLoader);
//        LoggerOut.println("Adding dynamic context " + context);
//        // Map the path to the processor.
//        final ServerConfiguration config = server.getServerConfiguration();
//        Map<HttpHandler, String[]> handlers = config.getHttpHandlers();
//        HttpHandler old = null;
//        for (HttpHandler hand : handlers.keySet()) {
//            String[] oldPath = handlers.get(hand);
//            if (oldPath[0].equals(getBaseURI().getPath() + context)) {
//                old = hand;
//                break;
//            }
//        }
//        config.addHttpHandler(dynamicHandler, getBaseURI().getPath() + context);
//        if (old != null) {
//            old.destroy();
//            config.removeHttpHandler(old);
//            config.addHttpHandler(dynamicHandler, getBaseURI().getPath() + context);
//        }
//
//        synchronized (moduleInfo) {
//            moduleInfo.append("<p>");
//            moduleInfo.append("<hr><br>\n");
//            moduleInfo.append("Context /");
//            moduleInfo.append(context);
//            moduleInfo.append("<br>\n");
//            moduleInfo.append("Base ");
//            addHyperLink("");
//            moduleInfo.append("<br>\n");
//
//            addRemove();
//
//            for (Class<?> myClass : dynamicLoader.getClasses()) {
//                loadJAX(myClass);
//            }
//
//            moduleInfo.append("<br>");
//            moduleInfo.append("</p>\n");
//        }
//    }

    private void addRemove() {
        moduleInfo.append("<p>");

        moduleInfo.append("<a href=\"/");
//        moduleInfo.append(App.getBaseURI()).deleteCharAt(moduleInfo.length() - 1);
        moduleInfo.append("remove/");
        moduleInfo.append(context);
        moduleInfo.append("\"target=\"_blank\">");
        moduleInfo.append("Remove Module ").append(context);
        moduleInfo.append("</a>\n");

        moduleInfo.append("</p>");
    }

    private StringBuilder getHyperLink(String url, String text) {
        StringBuilder ret = new StringBuilder();
        ret.append("<a href=\"/");
        if (context.equals("")) {
            ret.deleteCharAt(ret.length() - 1);
        } else {
            ret.append(context);
        }
        ret.append(url);
        ret.append("\"target=\"_blank\">");
        ret.append(text);
        ret.append("</a>");
        return ret;
    }

    private void addHyperLink(String url) {
        moduleInfo.append("<a href=\"/");
//        moduleInfo.append(App.getBaseURI()).deleteCharAt(moduleInfo.length() - 1);
        moduleInfo.append(context);
        moduleInfo.append(url);
        moduleInfo.append("\"target=\"_blank\">/");
        moduleInfo.append(context);
        moduleInfo.append(url);
        moduleInfo.append("</a>\n");
    }

    public StringBuilder getInfo() {
        return moduleInfo;
    }

    private void addResource(String httpMethod, String path, List<String> parms, String produces) {
//Each table starts with a table tag. 
//Each table row starts with a tr tag.
//Each table data starts with a td tag.
        moduleInfo.append("<tr>\n");
        moduleInfo.append("<td>").append(httpMethod).append("</td>\n");
        moduleInfo.append("<td>").append(getHyperLink(path, path)).append("</td>\n");
        moduleInfo.append("<td>");
        if (parms != null) {
            for (String parm : parms) {
                moduleInfo.append(parm).append("<br>");
            }
        }
        moduleInfo.append("</td>\n");
        moduleInfo.append("<td>");
        moduleInfo.append(produces).append("<br>");
        moduleInfo.append("</td>\n");
        moduleInfo.append("</tr>\n");
    }

    private void addTableHeader() {
        moduleInfo.append("<tr>\n");
        moduleInfo.append("<td>").append("Method").append("</td>\n");
        moduleInfo.append("<td>").append("URI").append("</td>\n");
        moduleInfo.append("<td>").append("Query Param").append("</td>\n");
        moduleInfo.append("<td>").append("MediaType").append("</td>\n");
        moduleInfo.append("</tr>\n");
    }

    private String getValue(Annotation an) {
        String parse = an.toString();
        String ret = "";
        String[] sub = parse.split("value=");
        if (sub != null && sub.length > 1) {
            ret = sub[1].replace(")", "");
        }
        return ret;
    }

    private void loadJAX(Class<?> restClass) {

        if (!restClass.isAnnotationPresent(javax.ws.rs.Path.class)) {
            return;
        }
        
        moduleInfo.append("Class ").append(restClass.getSimpleName()).append("<br>\n");
        moduleInfo.append("<table border=\"1\">\n");
        addTableHeader();

//        System.out.println("restClass is " + restClass + " jax " + restClass.isAnnotationPresent(javax.ws.rs.Path.class));
//        if (restClass.isAnnotationPresent(javax.ws.rs.Path.class)) {
//            Path path = restClass.getAnnotation(javax.ws.rs.Path.class);
//            LoggerOut.println("Loading path " + path.value());
//        }
        Method[] m = restClass.getDeclaredMethods();
        Annotation modpath = restClass.getAnnotation(javax.ws.rs.Path.class);
        String basepath = getValue(modpath);
        if (basepath.endsWith("/")) {
            basepath = basepath.substring(0, basepath.length() - 1);
        }
        for (Method meth : m) {
            String method = null;
            String path = basepath;
            String produces = "";
            Annotation[] ann = meth.getAnnotations();
            Annotation methpath = meth.getAnnotation(javax.ws.rs.Path.class);
            if (methpath != null) {
                path += getValue(methpath);
            }
            Annotation prod = meth.getAnnotation(javax.ws.rs.Produces.class);
            if (prod != null) {
                produces = getValue(prod);
            }
            for (Annotation cl : ann) {
                String parse = cl.toString();
                if (parse.startsWith("@javax.ws.rs.GET()")) {
                    method = "GET";
                } else if (parse.startsWith("@javax.ws.rs.POST()")) {
                    method = "POST";
                } else if (parse.startsWith("@javax.ws.rs.DELETE()")) {
                    method = "DELETE";
                } else if (parse.startsWith("@javax.ws.rs.PUT()")) {
                    method = "PUT";
                }
            }
            if (method == null) {
                continue;
            }
//            for (Annotation cl : ann) {
//                System.out.println("Name is " + cl.toString());
//            }

//            System.out.println("For method " + meth + " d ");
            Annotation[][] mymethods = meth.getParameterAnnotations();
            ArrayList<String> list = new ArrayList<>();
            for (Annotation[] annn : mymethods) {
                for (Annotation an : annn) {
                    String parse = an.toString();
                    if (parse.startsWith("@javax.ws.rs.QueryParam")) {
                        String[] sub = parse.split("value=");
                        if (sub != null && sub.length > 1) {
                            String q = sub[1].replace(")", "");
                            list.add(q);
                        }

                    } else if (parse.startsWith("@javax.ws.rs.PathParam")) {
                        String[] sub = parse.split("value=");
                        if (sub != null && sub.length > 1) {
                            String pp = sub[1].replace(")", "");
                            System.out.println("Path Param " + pp);
                        }

                    }
                }
            }

//            for (Type tt : meth.getGenericParameterTypes()) {
//                System.out.println("Type is " + tt);
//            }
            addResource(method, path, list, produces);

        }

        moduleInfo.append("</table>").append("<br>\n");
    }

    public void stopModule() {
        if (dynamicLoader != null) {
            dynamicLoader.stopModule();
        }
    }

    public String getContext() {
        return context;
    }
}
