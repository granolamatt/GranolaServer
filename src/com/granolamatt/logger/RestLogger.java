/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.logger;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.htmlhelpers.GetFunctions;
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

}
