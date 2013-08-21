/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.logger;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.htmlhelpers.GetFunctions;
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

/**
 *
 * @author root
 */
@Path("/logging")
public class RestLogger {

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
        BattersExample example = new BattersExample();
        example.items.add(new BatterExampleData());
        example.items.add(new BatterExampleData());
        return Response.ok(example).build();
    }
}
