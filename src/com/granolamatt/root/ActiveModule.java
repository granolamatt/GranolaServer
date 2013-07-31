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
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.util.List;

/**
 *
 * @author root
 */
public class ActiveModule {

    private final String context;
    private JaxRsDynamicLoader dynamicLoader = null;
    private HttpServer server = null;
    private final StringBuilder moduleInfo = new StringBuilder();

    public ActiveModule(HttpServer server) {
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

    public ActiveModule(HttpServer httpServer, File file) {
        this.server = httpServer;
        String jarName = file.getName();
        context = jarName.replaceAll(".jar", "").replaceAll(" ", "");
        dynamicLoader = new JaxRsDynamicLoader(file);
        HttpHandler dynamicHandler = ContainerFactory.createContainer(HttpHandler.class, dynamicLoader.getClasses());
        LoggerOut.println("Adding dynamic context " + context);
        server.createContext(getBaseURI().getPath() + context, dynamicHandler);

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
        
        moduleInfo.append("<a href=\"");
        moduleInfo.append(App.getBaseURI());
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
        ret.append("<a href=\"");
        ret.append(App.getBaseURI());
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
        moduleInfo.append("<a href=\"");
        moduleInfo.append(App.getBaseURI());
//        moduleInfo.append(App.getBaseURI()).deleteCharAt(moduleInfo.length() - 1);
        moduleInfo.append(context);
        moduleInfo.append(url);
        moduleInfo.append("\"target=\"_blank\">");
        moduleInfo.append(App.getBaseURI());
        moduleInfo.append(context);
        moduleInfo.append(url);
        moduleInfo.append("</a>\n");
    }

    public StringBuilder getInfo() {
        return moduleInfo;
    }

    private void addResource(String httpMethod, String path, List<Parameter> parms) {
//Each table starts with a table tag. 
//Each table row starts with a tr tag.
//Each table data starts with a td tag.
        moduleInfo.append("<tr>\n");
        moduleInfo.append("<td>").append(httpMethod).append("</td>\n");
        moduleInfo.append("<td>").append(getHyperLink(path, path)).append("</td>\n");
        moduleInfo.append("<td>");
        for (Parameter parm : parms) {
            moduleInfo.append(parm.getSourceName()).append("<br>");
        }
        moduleInfo.append("</td>\n");
        moduleInfo.append("<td>");
        for (Parameter parm : parms) {
            moduleInfo.append(parm.getParameterClass().getSimpleName()).append("<br>");
        }
        moduleInfo.append("</td>\n");
        moduleInfo.append("</tr>\n");
    }

    private void addTableHeader() {
        moduleInfo.append("<tr>\n");
        moduleInfo.append("<td>").append("Method").append("</td>\n");
        moduleInfo.append("<td>").append("URI").append("</td>\n");
        moduleInfo.append("<td>").append("Query Param").append("</td>\n");
        moduleInfo.append("<td>").append("Java Type").append("</td>\n");
        moduleInfo.append("</tr>\n");
    }

    private void loadJAX(Class<?> restClass) {

        moduleInfo.append("Class ").append(restClass.getSimpleName()).append("<br>\n");
        moduleInfo.append("<table border=\"1\">\n");
        addTableHeader();

        AbstractResource resource = IntrospectionModeller.createResource(restClass);
        if (resource.getPath() != null) {
            LoggerOut.println("Loading path " + resource.getPath().getValue());
            String uriPrefix = resource.getPath().getValue();
            List<AbstractResourceMethod> resourceMethod = resource.getResourceMethods();
            if (resourceMethod != null) {
                for (AbstractResourceMethod srm : resourceMethod) {
                    List<Parameter> parm = srm.getParameters();
                    for (Parameter p : parm) {
                        LoggerOut.println("Parameter is " + p.getParameterClass().getName() + " source name " + p.getSourceName());
                    }
                    addResource(srm.getHttpMethod(), resource.getPath().getValue(), parm);
                    LoggerOut.println(srm.getHttpMethod() + " at the path " + resource.getPath().getValue() + " return " + srm.getReturnType().getName());
                }
            }
            List<AbstractSubResourceMethod> resources = resource.getSubResourceMethods();
            if (resources != null) {
                for (AbstractSubResourceMethod srm : resources) {
                    String uri = uriPrefix + srm.getPath().getValue();
                    List<Parameter> parm = srm.getParameters();
                    for (Parameter p : parm) {
                        LoggerOut.println("Parameter is " + p.getParameterClass().getName() + " source name " + p.getSourceName());
                    }
                    addResource(srm.getHttpMethod(), uri, parm);
                    LoggerOut.println(srm.getHttpMethod() + " at the path " + uri + " return " + srm.getReturnType().getName());
                }
            }
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
//    private void loadRootPath() {
//        // We need to scan RestRoot and RestLogger
//
//        isJAX(RestRoot.class);
//        isJAX(RestLogger.class);
//
////        String classPath = System.getProperty("java.class.path");
////        String[] paths = classPath.split(File.pathSeparator);
////        for (String jname : paths) {
////            System.out.println("Jars are " + jname);
////        }
//    }
}
