/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.widget;

import com.granolamatt.htmlhelpers.SSEPropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.adrianwalker.multilinestring.Multiline;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

/**
 *
 * @author matt
 */
public class LEDResource {

    private int xpos = 100;
    private int ypos = 200;
    private int height = 100;
    private int width = 400;
    private int zpos = 2;
    private static final PropertyChangeSupport logChange = new PropertyChangeSupport(new Object());

    public String getPath() {
        return "/led";
    }

    public Set<Resource> getResources() {
        final Set<Resource> ret = new HashSet<>();
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(getPath());

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.produces(MediaType.TEXT_HTML)
                .handledBy(new Inflector<ContainerRequestContext, Response>() {

                    @Override
                    public Response apply(ContainerRequestContext containerRequestContext) {
                        return Response.ok().entity(getHTMLDocument()).build();
                    }
                });

        ret.add(resourceBuilder.build());

        final Resource.Builder resourceBuilder3 = Resource.builder();
        resourceBuilder3.path(getPath());

        final ResourceMethod.Builder methodBuilder3 = resourceBuilder3.addMethod("GET");
        methodBuilder3.produces(MediaType.APPLICATION_JSON)
                .handledBy(new Inflector<ContainerRequestContext, Response>() {

                    @Override
                    public Response apply(ContainerRequestContext containerRequestContext) {
                        return Response.ok().entity(getJSON()).build();
                    }
                });

        ret.add(resourceBuilder3.build());

        final Resource.Builder resourceBuilder4 = Resource.builder();
        resourceBuilder4.path("/resources/loader.js");

        final ResourceMethod.Builder methodBuilder4 = resourceBuilder4.addMethod("GET");
        methodBuilder4.produces(MediaType.APPLICATION_JSON)
                .handledBy(new Inflector<ContainerRequestContext, Response>() {

                    @Override
                    public Response apply(ContainerRequestContext containerRequestContext) {
                        InputStream in = ClassLoader.getSystemResourceAsStream("com/granolamatt/resources/loader.js");
                        return Response.ok().entity(in).build();
                    }
                });

        ret.add(resourceBuilder4.build());

        final Resource.Builder resourceBuilder5 = Resource.builder();
        resourceBuilder5.path("/resources/jquery.min.js");

        final ResourceMethod.Builder methodBuilder5 = resourceBuilder5.addMethod("GET");
        methodBuilder5.produces(MediaType.APPLICATION_JSON)
                .handledBy(new Inflector<ContainerRequestContext, Response>() {

                    @Override
                    public Response apply(ContainerRequestContext containerRequestContext) {
                        InputStream in = ClassLoader.getSystemResourceAsStream("com/granolamatt/resources/jquery.min.js");
                        return Response.ok().entity(in).build();
                    }
                });

        ret.add(resourceBuilder5.build());

        final Resource.Builder resourceBuilder6 = Resource.builder();
        resourceBuilder6.path("/led");

        final ResourceMethod.Builder methodBuilder6 = resourceBuilder6.addMethod("GET");
        methodBuilder6.produces(MediaType.TEXT_XML)
                .handledBy(new Inflector<ContainerRequestContext, Response>() {

                    @Override
                    public Response apply(ContainerRequestContext containerRequestContext) {
                        return Response.ok().entity(getXML()).build();
                    }
                });

        ret.add(resourceBuilder6.build());

        final Resource.Builder resourceBuilder2 = Resource.builder();
        resourceBuilder2.path(getPath());

        final ResourceMethod.Builder methodBuilder2 = resourceBuilder2.addMethod("GET");
        methodBuilder2.produces(SseFeature.SERVER_SENT_EVENTS)
                .handledBy(new Inflector<ContainerRequestContext, EventOutput>() {

                    @Override
                    public EventOutput apply(ContainerRequestContext containerRequestContext) {
                        SSEPropertyChangeListener sse = new SSEPropertyChangeListener(logChange);
                        return sse.register();
                    }
                });
        ret.add(resourceBuilder2.build());
        return ret;
    }

    /**
     * <!DOCTYPE html>
     * <html>
     * <head>
     * <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
     * <script src="/resources/jquery.min.js" charset="utf-8"></script>
     * <script src="/resources/loader.js" charset="utf-8"></script>
     * <meta charset="UTF-8">
     * <title>{@link #getTitle()}</title>
     * </head>
     * <body>
     * <script type="text/javascript">
     * function rme() { checkJS(); }
     *
     * $(document).ready(WidgetStarter("{@link #getPath()}"));
     * </script> 
     * </body>
     * </html>
     *
     */
    @Multiline
    private static String html;

    /**
     * <div style="position: absolute; left: {@link #getXpos()}px; top: {@link
     * #getYpos()}px; height: {@link #getHeight()}px; width: {@link
     * #getWidth()}px; z-index: {@link #getZpos()}; padding: 1em;
     * background-color: red;">
     * <p>
     * This is a paragraph. </p>
     * <div style="position: absolute; left: 5px; top:5px; height: 20px; width:
     * 25px; z-index: 1; padding: 1em; background-color: blue;">
     * <p>
     * This is Nested. </p>
     * </div>
     * </div>
     */
    @Multiline
    private static String myhtml;

    /**
     *
     * function loadJS() {
     * 
     * // var old = d3.select("body").selectAll("div");
     * 
     * d3.select("body").append("div")
     * .style("position", "absolute")
     * .style("left", "{@link #getXpos()}px")
     * .style("top", "{@link #getYpos()}px")
     * .style("height", "{@link #getHeight()}px")
     * .style("width", "{@link #getWidth()}px")
     * .style("z-index", "{@link #getZpos()}")
     * .style("padding", "1em")
     * .style("background-color", "red");
     * 
     * // old.exit().remove();
     * 
     *   function cb(xml) {
     *      var ret = $(xml).find("output").text();
     *      if (ret === "true") {
     *      alert('turn it on ' + ret);
     *      } else {
     *      alert('turn it off ' + ret);
     *      }
     *   }
     *   
     *   XMLLoader("/led", cb);
     * };
     *
     */
    @Multiline
    private static String myjson;

    public String getTitle() {
        return "My Doc Test";
    }
    
    public String getXML() {
        return "<output>true</output>";
    }

    public String getJSON() {
        try {
            return WidgetUtils.substituteVariablesWithMethod(myjson, this);
        } catch (Exception ex) {
            Logger.getLogger(LEDResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String addHTML() {
        try {
            return WidgetUtils.substituteVariablesWithMethod(myhtml, this);
        } catch (Exception ex) {
            Logger.getLogger(LEDResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getHTMLDocument() {
        try {
            return WidgetUtils.substituteVariablesWithMethod(html, this);
        } catch (Exception ex) {
            Logger.getLogger(LEDResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * @return the xpos
     */
    public int getXpos() {
        return xpos;
    }

    /**
     * @param xpos the xpos to set
     */
    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    /**
     * @return the ypos
     */
    public int getYpos() {
        return ypos;
    }

    /**
     * @param ypos the ypos to set
     */
    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the zpos
     */
    public int getZpos() {
        return zpos;
    }

    /**
     * @param zpos the zpos to set
     */
    public void setZpos(int zpos) {
        this.zpos = zpos;
    }

}
