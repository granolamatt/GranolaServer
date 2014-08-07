/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.dynamicloader;

import com.granolamatt.hardware.RestHardware;
import com.granolamatt.logger.RestLogger;
import com.granolamatt.root.RestRoot;
import com.granolamatt.widgets.WidgetFactory;
import java.util.logging.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author root
 */
public class DefaultApplication extends ResourceConfig {
//private final Set<Class<?>> classes;

    private static final Logger LOGGER = Logger.getLogger(DefaultApplication.class.getName());

    public DefaultApplication() {
        register(RestRoot.class);
        register(RestLogger.class);
        register(RestHardware.class);
        register(WidgetFactory.class);
        register(MultiPartFeature.class);
        register(SseFeature.class);
//        registerInstances(new LoggingFilter(LOGGER, true));

//          HashSet<Class<?>> c = new HashSet<>();
//        c.add(RestRoot.class);
//        c.add(RestLogger.class);
//        c.add(RestHardware.class);
//        classes = Collections.unmodifiableSet(c);
    }

//    @Override
//    public Set<Class<?>> getClasses() {
//
//        return classes;
//    }
}
