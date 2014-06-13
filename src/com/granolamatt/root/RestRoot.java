/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.htmlhelpers.FileChooser;
import com.granolamatt.htmlhelpers.SSEPropertyChangeListener;
import com.granolamatt.logger.LoggerOut;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author root
 */
@Path("/")
public class RestRoot {

    private final FileChooser chooser = new FileChooser();
    private static final PropertyChangeSupport rootChange = new PropertyChangeSupport(new Object());

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Response getRoot() {
        BasicDocument doc = new BasicDocument();
        doc.addLine("Welcome to Rest Server");
        doc.addLine("Device time is " + new Date());
        doc.addLine("");
        doc.addContent(chooser.getContent());
        App.getModulesInfo(doc);

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    @GET
    @Path("remove/{context}")
    @Produces(MediaType.TEXT_HTML)
    public Response removeModule(@PathParam("context") String context) {
        LoggerOut.println("Removing module " + context);
        BasicDocument doc = new BasicDocument();
        App.removeModule(context);
        doc.addLine("Removed Module " + context);
        doc.addLine("");
        rootChange.firePropertyChange("module-removed", "", context);
        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    @GET
    @Path("/sse")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents() {
        SSEPropertyChangeListener sse = new SSEPropertyChangeListener(rootChange);
        return sse.register();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("datafile") InputStream uploadedInputStream,
            @FormDataParam("datafile") FormDataContentDisposition fileDetail) { //            (byte[] summary) {

        String uploadedFileLocation = "/opt/granola/" + fileDetail.getFileName();
        LoggerOut.println("File is type " + fileDetail.getName());

        File uploadDir = new File("/opt/granola");
        uploadDir.mkdirs();
        
        if (!uploadDir.exists()) {
            try {
                File tmp = File.createTempFile(fileDetail.getFileName(), ".tmp");
                uploadedFileLocation = tmp.getAbsolutePath();
            } catch (IOException ex) {
                Logger.getLogger(RestRoot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        writeToFile(uploadedInputStream, uploadedFileLocation);
        String output = "File uploaded to : " + uploadedFileLocation;

        File jarName = new File(uploadedFileLocation);
        App.addModule(jarName);

        BasicDocument doc = new BasicDocument(5);
        doc.addContent(output);

        rootChange.firePropertyChange("module-added", "", jarName.getName());
        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        try {
            OutputStream out;
            int read;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
