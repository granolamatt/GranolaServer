/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.dynamicloader;

import com.granolamatt.hardware.RestHardware;
import com.granolamatt.logger.RestLogger;
import com.granolamatt.root.RestRoot;
import com.granolamatt.widget.LEDResource;
import java.util.logging.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;

/**
 *
 * @author root
 */
public class DefaultApplication extends ResourceConfig {
//private final Set<Class<?>> classes;

    private static final Logger LOGGER = Logger.getLogger(DefaultApplication.class.getName());

    public DefaultApplication() {
        LEDResource led = new LEDResource();

        register(RestRoot.class);
        register(RestLogger.class);
        register(RestHardware.class);
        register(MultiPartFeature.class);
        register(SseFeature.class);
        registerResources(led.getResources());

    }

}
