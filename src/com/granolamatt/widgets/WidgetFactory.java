/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.widgets;

import com.granolamatt.html.HTMLScript;
import com.granolamatt.htmlhelpers.BasicSVGWidget;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author root
 */
@Path("/widgets")
public class WidgetFactory {

    @GET
    @Path("/drawing")
    @Produces(MediaType.TEXT_HTML)
    public Response getDrawing() {
        BasicSVGWidget doc = new BasicSVGWidget();
//        doc.addContent("Hello World");

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    @GET
    @Path("/script")
    @Produces("text/script")
    public Response getScript() {
        HTMLScript doc = new HTMLScript();
        doc.addAttribute("type", "text/javascript");
        doc.addLine("Hello World");
        StringBuilder cont = new StringBuilder();
        doc.getHTML(cont);
        return Response.status(Response.Status.OK).entity(cont.toString()).build();
    }

    /*
     * Reads the xml configuration file for this application and updates attributes accordingly.
     * If there are exceptions, just load the default configuration.
     */
    private void readConfigFile() {
        try {
            SAXBuilder parser = new SAXBuilder();
            InputStream in = ClassLoader.getSystemResourceAsStream("com/granolamatt/resources/drawing.svg");
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));
            Document doc = parser.build(buff);
            Element rootNode = doc.getRootElement();
            List<Element> chld = rootNode.getChildren();
            for (Element c : chld) {
                System.out.println("Element is " + c.getName());
            }
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(WidgetFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
