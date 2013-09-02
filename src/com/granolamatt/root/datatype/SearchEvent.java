/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root.datatype;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import static org.simpleframework.http.Protocol.CLOSE;
import static org.simpleframework.http.Protocol.CONNECTION;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

/**
 *
 * @author root
 */
public class SearchEvent {

    private HashMap<Integer, ArrayList<SearchMethod>> getMethodList = new HashMap<>();
    private HashMap<Integer, ArrayList<SearchMethod>> putMethodList = new HashMap<>();
    private HashMap<Integer, ArrayList<SearchMethod>> postMethodList = new HashMap<>();
    private HashMap<Integer, ArrayList<SearchMethod>> deleteMethodList = new HashMap<>();

    public void addMethod(Class<?> myClass, Method method, String context) {
        SearchMethod sm = new SearchMethod(myClass, method, context);
        int pathLength = sm.getPathLength();
        System.out.println("Adding method pathlength " + pathLength);
        if (sm.getHttpMethod() != null) {
            HashMap<Integer, ArrayList<SearchMethod>> methodList = getMethodList(sm.getHttpMethod());
            if (methodList.containsKey(pathLength)) {
                ArrayList<SearchMethod> array = methodList.get(pathLength);
                array.add(sm);
            } else {
                ArrayList<SearchMethod> array = new ArrayList<>();
                array.add(sm);
                methodList.put(pathLength, array);
            }
        }
    }

    private HashMap<Integer, ArrayList<SearchMethod>> getMethodList(String method) {
        HashMap<Integer, ArrayList<SearchMethod>> methodList;
        switch (method) {
            case "GET":
                methodList = getMethodList;
                break;
            case "POST":
                methodList = postMethodList;
                break;
            case "PUT":
                methodList = putMethodList;
                break;
            case "DELETE":
                methodList = deleteMethodList;
                break;
            default:
                methodList = getMethodList;
                break;
        }
        return methodList;
    }

    private void closeConnection(Response response) throws IOException {
        response.setStatus(Status.NOT_FOUND);
        response.setValue(CONNECTION, CLOSE);
        response.close();
    }

    public void callMethod(Request request, Response response) throws IOException {
        Path path = request.getPath();
        String[] segments = path.getSegments();
        int pathLength = segments.length;

        System.out.println("This is a " + request.getMethod());

        if (request.getMethod() == null) {
            return;
        }
        HashMap<Integer, ArrayList<SearchMethod>> methodList = getMethodList(request.getMethod());

//        CharSequence c = request.getHeader();
//        System.out.println("Sequence is " + c);

        String accept = request.getValue("Accept");
        String encoding = request.getValue("Accept-Encoding");
        System.out.println("Accepts " + accept + " Encoding " + encoding);
        System.out.println("Checking for length " + pathLength);

        if (methodList.containsKey(pathLength)) {
            ArrayList<SearchMethod> array = methodList.get(pathLength);
            boolean success = false;
            for (SearchMethod sm : array) {
                int distance = sm.getPathDistance(segments);
                if (distance == 0) {
                    //XXX Need to check for best info
                    // in other words, make sure method can produce accept coding
                    // and it can do proper encoding
                    success = sm.callMethod(request, response);
                    if (success) {
                        break;
                    }
                }
            }
            if (!success) {
                closeConnection(response);
            }
        } else {
            closeConnection(response);
        }
    }
}
