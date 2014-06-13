/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.dynamicloader;

import com.granolamatt.hardware.RestHardware;
import com.granolamatt.logger.RestLogger;
import com.granolamatt.root.RestRoot;
import java.util.logging.Logger;
import org.glassfish.jersey.filter.LoggingFilter;
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
        register(MultiPartFeature.class);
        register(SseFeature.class);
//        register(MyObjectMapperProvider.class);  // No need to register this provider if no special configuration is required.
//        register(JacksonFeature.class);
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
