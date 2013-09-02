/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root;

import com.granolamatt.dynamicloader.JaxRsDynamicLoader;
import com.granolamatt.hardware.RestHardware;
import com.granolamatt.logger.LoggerOut;
import com.granolamatt.logger.RestLogger;
import com.granolamatt.root.datatype.SearchEvent;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 *
 * @author root
 */
public class ActiveModule {

    private final String context;
    private JaxRsDynamicLoader dynamicLoader = null;
    private final StringBuilder moduleInfo = new StringBuilder();
    private final boolean debug = true;
    private final SearchEvent search = new SearchEvent();

    public ActiveModule() {
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

    public ActiveModule(File file) {
        String jarName = file.getName();
        context = jarName.replaceAll(".jar", "").replaceAll(" ", "");
//        try {
//            mcontext = URLEncoder.encode(mcontext, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            mcontext = Double.toString(Math.random());
//        }
//        context = mcontext;
        System.out.println("context is " + context);
        dynamicLoader = new JaxRsDynamicLoader(file);
//        HttpHandler dynamicHandler = ContainerFactory.createContainer(HttpHandler.class, dynamicLoader);
        LoggerOut.println("Adding dynamic context " + context);
//        server.createContext(getBaseURI().getPath() + context, dynamicHandler);

        synchronized (moduleInfo) {
            moduleInfo.append("<p>");
            moduleInfo.append("<hr><br>\n");
            moduleInfo.append("Context /");
            moduleInfo.append(context);
            moduleInfo.append("<br>\n");
            moduleInfo.append("Base ");
            addHyperLink("");
            moduleInfo.append("<br>\n");

            addRemove();

            for (Class<?> myClass : dynamicLoader.getClasses()) {
                loadJAX(myClass);
            }

            moduleInfo.append("<br>");
            moduleInfo.append("</p>\n");
        }
    }

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
            search.addMethod(restClass, meth, context);
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

            String[] sub = path.split("/");
            ArrayList<String> plist = new ArrayList<>();
            for (int cnt = 0; cnt < sub.length; cnt++) {
                if (!sub[cnt].equals("")) {
                    plist.add(sub[cnt]);
                }
            }
            sub = new String[plist.size()];
            for (int cnt = 0; cnt < plist.size(); cnt++) {
                sub[cnt] = plist.get(cnt);
            }
            
            System.out.println("Method " + method + " meth " + meth.getName() + " path " + path + " produces " + produces);
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

    public void parseModule(Request request, Response response) throws IOException {

//        Path path = request.getPath();
//        String check = path.getPath();
//        if (check.startsWith("/")) {
//            check = check.replaceFirst("/", "");
//        }
//        if (!context.equals("")) {
//            check = check.replaceFirst(context, "");
//        } else {
//            check = "/" + check;
//        }
//
//        System.out.println("Check is " + check);
        search.callMethod(request, response);
        
//        
//        String directory = path.getDirectory();
//        String name = path.getName();
//        Query q = request.getQuery();
//
//        Set<Map.Entry<String, String>> entry = q.entrySet();
//        for (Map.Entry<String, String> en : entry) {
//            System.out.println("For entry " + en.getKey() + " the value " + en.getValue());
//        }
//        System.out.println("Request was for path " + path + " dir " + directory
//                + " name " + name);
//        String[] segments = path.getSegments();
//        
//        for (String seg : segments) {
//            System.out.println("Segment " + seg);
//        }
//        PrintStream body = response.getPrintStream();
//        long time = System.currentTimeMillis();
//
//        response.setValue(Protocol.CONTENT_TYPE, "text/plain");
//        response.setValue("Server", "GranolaServer 0.9");
//        response.setDate("Date", time);
//        response.setDate("Last-Modified", time);
//
//
//        body.println("Hello World " + check);
//        body.close();
    }
}
