/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import static org.simpleframework.http.Protocol.CLOSE;
import static org.simpleframework.http.Protocol.CONNECTION;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

public class Task implements Runnable {

    private final Response response;
    private final Request request;
    private final List<ActiveModule> moduleList;

    public Task(Request request, Response response, List<ActiveModule> moduleList) {
        this.response = response;
        this.request = request;
        this.moduleList = moduleList;
    }

    private void closeConnection() throws IOException {
        response.setStatus(Status.NOT_FOUND);
        response.setValue(CONNECTION, CLOSE);
        response.close();
    }

    @Override
    public void run() {
        try {
            Path path = request.getPath();

            ActiveModule module = null;
            String check = path.getPath();
            if (check.startsWith("/")) {
                check = check.replaceFirst("/", "");
            }

            System.out.println("Checking " + check);
            for (ActiveModule am : moduleList) {
                if (am.getContext().equals("")) {
                    continue;
                }
                if (check.startsWith(am.getContext())) {
                    System.out.println("Found module " + am.getContext());
                    module = am;
                    break;
                }
            }
            if (module == null) {
                for (ActiveModule am : moduleList) {
                    if (am.getContext().equals("")) {
                        System.out.println("Using root module");
                        module = am;
                        break;
                    }
                }
            }
            if (module == null) { // Error, root is not even set up
                closeConnection();
            } else {
                module.parseModule(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
