/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.hardware;

import com.granolamatt.htmlhelpers.BasicDocument;
import com.granolamatt.logger.LoggerOut;
import java.util.HashMap;
import java.util.Map;
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
@Path("/hardware")
public class RestHardware {

    public enum PinDirection {

        INPUT, OUTPUT
    }

    public enum PinStatus {

        ON, OFF, BLINKING
    }
    private static final Map<Integer, PinDirection> pinDirection = new HashMap<>();
    private static final Map<Integer, PinStatus> pinStatus = new HashMap<>();

    @GET
    @Path("/turnon")
    @Produces(MediaType.TEXT_HTML)
    public Response turnOnPin(@QueryParam("pin") int pin) {
        BasicDocument doc = new BasicDocument();
        if (pin != 0 && pin < 32) {
            if (!pinDirection.containsKey(pin)) {
                HardwareMemory.InpGPIO(pin);
                HardwareMemory.OutGPIO(pin);
                pinDirection.put(pin, PinDirection.OUTPUT);
                pinStatus.put(pin, PinStatus.OFF);
            }
            if (pinDirection.get(pin).equals(PinDirection.OUTPUT)) {
                HardwareMemory.GPIOSetPinNumber(pin);
                pinStatus.put(pin, PinStatus.ON);
                doc.addContent("Turned on pin " + pin + "<br>");
                LoggerOut.println("Turned on pin " + pin);
            }
        }
        for (Integer in : pinDirection.keySet()) {
            doc.addContent("Pin " + in + " is " + pinDirection.get(in) + " and is " + pinStatus.get(in) + "<br>");
        }

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

    @GET
    @Path("/turnoff")
    @Produces(MediaType.TEXT_HTML)
    public Response turnOffPin(@QueryParam("pin") int pin) {
        BasicDocument doc = new BasicDocument();
        if (pin != 0 && pin < 32) {
            if (!pinDirection.containsKey(pin)) {
                HardwareMemory.InpGPIO(pin);
                HardwareMemory.OutGPIO(pin);
                pinDirection.put(pin, PinDirection.OUTPUT);
                pinStatus.put(pin, PinStatus.OFF);
            }
            if (pinDirection.get(pin).equals(PinDirection.OUTPUT)) {
                HardwareMemory.GPIOClrPinNumber(pin);
                doc.addContent("Turned off pin " + pin + "<br>");
                pinStatus.put(pin, PinStatus.OFF);
                LoggerOut.println("Turned off pin " + pin);
            }
        }
        for (Integer in : pinDirection.keySet()) {
            doc.addContent("Pin " + in + " is " + pinDirection.get(in) + " and is " + pinStatus.get(in) + "<br>");
        }

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }
}
