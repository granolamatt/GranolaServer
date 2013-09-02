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
    public String turnOnPin(@QueryParam("pin") int pin) {
        BasicDocument doc = new BasicDocument();
        if (pin != 0 && pin < 32) {
            if (!pinDirection.containsKey(pin)) {
                HardwareMemory.gpioSetFunction(pin, HardwareMemory.eFunction.output);
                pinDirection.put(pin, PinDirection.OUTPUT);
                pinStatus.put(pin, PinStatus.OFF);
            }
            if (pinDirection.get(pin).equals(PinDirection.OUTPUT)) {
                HardwareMemory.gpioSetPin(pin, HardwareMemory.eState.high);
                pinStatus.put(pin, PinStatus.ON);
                doc.addContent("Turned on pin " + pin + "<br>");
                LoggerOut.println("Turned on pin " + pin);
            }
        }
        for (Integer in : pinDirection.keySet()) {
            doc.addContent("Pin " + in + " is " + pinDirection.get(in) + " and is " + pinStatus.get(in) + "<br>");
        }

        return doc.toString();
    }

    @GET
    @Path("/turnoff")
    @Produces(MediaType.TEXT_HTML)
    public String turnOffPin(@QueryParam("pin") int pin) {
        BasicDocument doc = new BasicDocument();
        if (pin != 0 && pin < 32) {
            if (!pinDirection.containsKey(pin)) {
//                HardwareMemory.gpioSetFunction(pin, HardwareMemory.eFunction.input);
                HardwareMemory.gpioSetFunction(pin, HardwareMemory.eFunction.output);
//                pinDirection.put(pin, PinDirection.OUTPUT);
                HardwareMemory.gpioSetPin(pin, HardwareMemory.eState.low);
                pinStatus.put(pin, PinStatus.OFF);
            }
            if (pinDirection.get(pin).equals(PinDirection.OUTPUT)) {
                HardwareMemory.gpioSetPin(pin, HardwareMemory.eState.low);
                doc.addContent("Turned off pin " + pin + "<br>");
                pinStatus.put(pin, PinStatus.OFF);
                LoggerOut.println("Turned off pin " + pin);
            }
        }
        for (Integer in : pinDirection.keySet()) {
            doc.addContent("Pin " + in + " is " + pinDirection.get(in) + " and is " + pinStatus.get(in) + "<br>");
        }

        return doc.toString();
    }
}
