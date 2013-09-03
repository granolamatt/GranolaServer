/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.root;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.htmlhelpers.FileChooser;
import com.granolamatt.logger.LoggerOut;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
    public String getRoot() {
        BasicDocument doc = new BasicDocument();
        doc.addLine("Welcome to Rest Server");
        doc.addLine("Device time is " + new Date());
        doc.addLine("");
        doc.addContent(chooser.getContent());
        App.getModulesInfo(doc);

        return doc.toString();
    }
    
    @GET
    @Path("/remove/{context}")
    @Produces(MediaType.TEXT_HTML)
    public String removeModule(@PathParam("context") String context) {
        LoggerOut.println("Removing module " + context);
        BasicDocument doc = new BasicDocument();
        App.removeModule(context);
        doc.addLine("Removed Module " + context);
        doc.addLine("");
        return doc.toString();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(
            @FormParam("datafile") InputStream uploadedInputStream,
            @FormParam("datafile") String fileDetail) { //            (byte[] summary) {

        String uploadedFileLocation = "/opt/granola/" + fileDetail;
        LoggerOut.println("File is type " + fileDetail);
        
        File uploadDir = new File("/opt/granola");
        uploadDir.mkdirs();
        
        writeToFile(uploadedInputStream, uploadedFileLocation);
        String output = "File uploaded to : " + uploadedFileLocation;

        File jarName = new File(uploadedFileLocation);
        App.addModule(jarName);
        
        BasicDocument doc = new BasicDocument(5);
        doc.addContent(output);

        return doc.toString();
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
