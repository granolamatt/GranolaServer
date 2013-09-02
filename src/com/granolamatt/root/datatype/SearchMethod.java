/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root.datatype;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 *
 * @author root
 */
public class SearchMethod {

    private int[] pathHash;
    private final Class<?> myClass;
    private final Method myMethod;
    private final String context;
    private String httpMethod;
    private String produces;
    private String path;
    private final HashMap<String, Integer> pathParams = new HashMap<>();

    public SearchMethod(Class<?> myClass, Method myMethod, String context) {
        this.myClass = myClass;
        this.myMethod = myMethod;
        this.context = context;
        parseMethod();
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

    private void parseMethod() {
        Annotation modpath = myClass.getAnnotation(javax.ws.rs.Path.class);
        String methodpath = getValue(modpath);
        Annotation[] ann = myMethod.getAnnotations();
        Annotation methpath = myMethod.getAnnotation(javax.ws.rs.Path.class);
        if (methpath != null) {
            methodpath += getValue(methpath);
        }
        Annotation prod = myMethod.getAnnotation(javax.ws.rs.Produces.class);
        if (prod != null) {
            produces = getValue(prod);
            if (produces.length() > 2) {
                produces = produces.substring(1, produces.length() - 1);
            }
        }
        for (Annotation cl : ann) {
            String parse = cl.toString();
            if (parse.startsWith("@javax.ws.rs.GET()")) {
                httpMethod = "GET";
            } else if (parse.startsWith("@javax.ws.rs.POST()")) {
                httpMethod = "POST";
            } else if (parse.startsWith("@javax.ws.rs.DELETE()")) {
                httpMethod = "DELETE";
            } else if (parse.startsWith("@javax.ws.rs.PUT()")) {
                httpMethod = "PUT";
            }
        }
        Annotation[][] mymethods = myMethod.getParameterAnnotations();
        for (Annotation[] annn : mymethods) {
            for (Annotation an : annn) {
                String parse = an.toString();
                if (parse.startsWith("@javax.ws.rs.QueryParam")) {
                    String[] sub = parse.split("value=");
                    if (sub != null && sub.length > 1) {
                        String q = sub[1].replace(")", "");
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

        path = (methodpath.startsWith("//")) ? methodpath.substring(1) : methodpath;

        String[] sub = path.split("/");
        ArrayList<String> plist = new ArrayList<>();
        if (!context.equals("")) {
            plist.add(context);
        }
        for (int cnt = 0; cnt < sub.length; cnt++) {
            if (!sub[cnt].equals("")) {
                plist.add(sub[cnt]);
            }
        }
        sub = new String[plist.size()];
        for (int cnt = 0; cnt < plist.size(); cnt++) {
            sub[cnt] = plist.get(cnt);
        }

        addMethod(sub);
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    private void addMethod(String[] segments) {
        pathHash = new int[segments.length];
        for (int cnt = 0; cnt < segments.length; cnt++) {
            if (!segments[cnt].startsWith("{")) {
                pathHash[cnt] = segments[cnt].hashCode();
            } else {
                String parm = segments[cnt];
                System.out.println("Adding " + parm.substring(1, parm.length() - 1) + " to params");
                pathParams.put(parm.substring(1, parm.length() - 1), cnt);
            }
        }
    }

    public int getPathLength() {
        return pathHash.length;
    }

    public int getPathDistance(String[] test) {
        int ret = 0;
        for (int cnt = 0; cnt < test.length; cnt++) {
            if (pathHash[cnt] != 0) {
                if (pathHash[cnt] != test[cnt].hashCode()) {
                    ret++;
                }
            }
        }
        return ret;
    }

    public boolean callMethod(Request request, Response response) {
        boolean success = false;
        try {
            String[] segments = request.getPath().getSegments();
            Annotation[][] mymethods = myMethod.getParameterAnnotations();
            Class<?>[] paramTypes = myMethod.getParameterTypes();
            System.out.println("for Path " + getPath());
            int idx = 0;

            Object[] args = new Object[paramTypes.length];

            for (Annotation[] annn : mymethods) {
                for (Annotation an : annn) {
                    String parse = an.toString();
                    if (parse.startsWith("@javax.ws.rs.QueryParam")) {
                        String[] sub = parse.split("value=");
                        if (sub != null && sub.length > 1) {
                            String q = sub[1].replace(")", "");
                            //queryParams.put(q, idx);
                            System.out.println("Query param " + q + " type " + paramTypes[idx]);
                            // XXX Finish querys
                            Query query = request.getQuery();
                            String pathValue = query.get(q);
                            System.out.println("Arg is " + pathValue);
                            if (paramTypes[idx].isPrimitive()) {
                                switch (paramTypes[idx].getSimpleName()) {
                                    case "double":
                                        args[idx] = Double.parseDouble(pathValue);
                                        break;
                                    case "int":
                                        args[idx] = Integer.parseInt(pathValue);
                                        break;
                                }
                            } else {
                                args[idx] = paramTypes[idx].cast(pathValue);
                            }
                        }
                    } else if (parse.startsWith("@javax.ws.rs.PathParam")) {
                        String[] sub = parse.split("value=");
                        if (sub != null && sub.length > 1) {
                            String pp = sub[1].replace(")", "");
                            System.out.println("Path param " + pp + " type " + paramTypes[idx]);
                            int pathIndex = pathParams.get(pp);
                            String pathValue = segments[pathIndex];
                            System.out.println("Arg is " + pathValue);
                            if (paramTypes[idx].isPrimitive()) {
                                switch (paramTypes[idx].getSimpleName()) {
                                    case "double":
                                        args[idx] = Double.parseDouble(pathValue);
                                        break;
                                    case "int":
                                        args[idx] = Integer.parseInt(pathValue);
                                        break;
                                }
                            } else {
                                args[idx] = paramTypes[idx].cast(pathValue);
                            }
                        }
                    }
                    idx++;
                }
            }

            Object out = myMethod.invoke(myClass.newInstance(), args);

            PrintStream body = response.getPrintStream();
            long time = System.currentTimeMillis();
            response.setValue(Protocol.CONTENT_TYPE, produces);
            response.setValue("Server", "GranolaServer 0.9");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            body.println(out);
            body.close();
        } catch (Exception ex) {
            success = false;
            Logger.getLogger(SearchMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }
}
