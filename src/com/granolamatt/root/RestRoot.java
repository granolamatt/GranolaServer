/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.htmlhelpers.FileChooser;
import com.granolamatt.logger.LoggerOut;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author root
 */
@Path("/")
public class RestRoot {

    private FileChooser chooser = new FileChooser();

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
        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("datafile") InputStream uploadedInputStream,
            @FormDataParam("datafile") FormDataContentDisposition fileDetail) { //            (byte[] summary) {

        String uploadedFileLocation = "/tmp/" + fileDetail.getFileName();
        LoggerOut.println("File is type " + fileDetail.getName());
        
        writeToFile(uploadedInputStream, uploadedFileLocation);
        String output = "File uploaded to : " + uploadedFileLocation;

        File jarName = new File(uploadedFileLocation);
        App.addModule(jarName);
        
        BasicDocument doc = new BasicDocument(5);
        doc.addContent(output);

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        try {
            OutputStream out;
            int read = 0;
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
