/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.dynamicloader;

import com.granolamatt.hardware.RestHardware;
import com.granolamatt.logger.RestLogger;
import com.granolamatt.root.RestRoot;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author root
 */
public class DefaultApplication extends Application {

    private final Set<Class<?>> classes;

    public DefaultApplication() {

        HashSet<Class<?>> c = new HashSet<>();
        c.add(RestRoot.class);
        c.add(RestLogger.class);
        c.add(RestHardware.class);
        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Map<String, Object> getProperties() {
        return super.getProperties(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
