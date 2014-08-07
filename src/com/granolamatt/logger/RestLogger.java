/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.logger;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.htmlhelpers.GetFunctions;
import com.granolamatt.htmlhelpers.SSEPropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author root
 */
@Path("/logging")
public class RestLogger {

    private static final PropertyChangeSupport logChange = new PropertyChangeSupport(new Object());
    
    public static void fireLog(String log) {
        logChange.firePropertyChange("logger", "", log);
    }

    @GET
    @Path("/stdout")
    @Produces(MediaType.TEXT_HTML)
    public Response getStdOut(@QueryParam("constant") String refresh, @QueryParam("println") String line, @QueryParam("print") String in) {
        BasicDocument doc = new BasicDocument();
        if (refresh != null) {
            doc.setRefresh(0);
            doc.addContent(LoggerOut.getString());
        } else {
            try {
                if (line != null) {
                    String dline = URLDecoder.decode(line, GetFunctions.encString);
                    LoggerOut.println(dline);
                } else if (in != null) {
                    String din = URLDecoder.decode(in, GetFunctions.encString);
                    LoggerOut.print(din);
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RestLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
            doc.addContent(LoggerOut.getStringNoWait());
        }

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    @GET
    @Path("/stdout/sse")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents() {
        SSEPropertyChangeListener sse = new SSEPropertyChangeListener(logChange);
        return sse.register();
    }

    @GET
    @Path("/example")
    @Produces(MediaType.TEXT_HTML)
    public Response getExample() {
        try {
            StringBuilder doc = new StringBuilder();

            InputStream in = ClassLoader.getSystemResourceAsStream("com/granolamatt/html/tree.html");

            InputStreamReader sr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(sr);
            String rr;
            while ((rr = br.readLine()) != null) {
                doc.append(rr + "\n");
            }

            return Response.status(Response.Status.OK).entity(doc.toString()).build();
        } catch (IOException ex) {
            Logger.getLogger(RestLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExampleData() {
        final int num = (int)(Math.random()*100);
        StringBuilder ret = new StringBuilder();
        ret.append("[");
        for (int cnt = 0; cnt < num; cnt++) {
            if (cnt > 0) {
                ret.append(",");
            }
            ret.append("{\"x\":\"");
            ret.append(Integer.toString(cnt));
            ret.append("\",\"y\":\"");
            double val = 100 * Math.random();
            ret.append(Double.toString(val));
            ret.append("\"}\n");
        }
        ret.append("]");
        return Response.ok(ret.toString()).build();
    }
}
