/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.granolamatt.dynamicloader;

import com.granolamatt.device.DeviceModule;
import com.granolamatt.logger.LoggerOut;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application class for this example.
 *
 * @author Martin Matula (martin.matula at oracle.com)
 */
public class JaxRsDynamicLoader extends Application {
    
    private final Set<Class<?>> classes;
    private final List<DeviceModule> deviceModules = new LinkedList<>();
    
    public boolean isJAX(Class<?> restClass) {
        AbstractResource resource = IntrospectionModeller.createResource(restClass);
        if (resource.getPath() != null) {
            LoggerOut.println("Loading path " + resource.getPath().getValue());
            String uriPrefix = resource.getPath().getValue();
            List<AbstractSubResourceMethod> resources = resource.getSubResourceMethods();
            if (resources != null) {
                for (AbstractSubResourceMethod srm : resources) {
                    String uri = uriPrefix + "/" + srm.getPath().getValue();
                    LoggerOut.println(srm.getHttpMethod() + " at the path " + uri + " return " + srm.getReturnType().getName());
                }
            }
            return true;
        }
        return false;
    }
    
    private void analyzeClass(Class<?> myClass) {
        
        Method[] methods = myClass.getDeclaredMethods();
        
        for (int i = 0; i < methods.length; i++) {
//            System.out.println("Method Name:: " + methods[i].getName());
//            System.out.println("Return Type:: " + methods[i].getReturnType());
            Type[] params = methods[i].getGenericParameterTypes();
            for (int j = 0; j < params.length; j++) {
                System.out.println("Parameter: " + params[j].toString());
            }
        }
    }
    
    private HashSet<Class<?>> getClasseNamesInPackage(File jarName) {
        HashSet<Class<?>> foundClasses = new HashSet<>();
        
        try {
            URI uri = jarName.toURI();
            URL[] urls = new URL[]{uri.toURL()};
            ClassLoader cl = new URLClassLoader(urls);
            
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            
            JarEntry jarEntry;
            
            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (jarEntry.getName().endsWith(".class")) {
                    StringBuilder className = new StringBuilder();
                    for (String part : jarEntry.getName().split("/")) {
                        if (className.length() != 0) {
                            className.append(".");
                        }
                        className.append(part);
                        if (part.endsWith(".class")) {
                            className.setLength(className.length() - ".class".length());
                        }
                    }
                    LoggerOut.println("Found class " + className.toString());
                    Class<?> nClass = cl.loadClass(className.toString());
                    
                    LoggerOut.println("NClass is " + nClass);
                    Class<?>[] inter = nClass.getInterfaces();
                    for (Class<?> ci : inter) {
                        if (ci.isAssignableFrom(DeviceModule.class)) {
                            final DeviceModule nModule = (DeviceModule) nClass.newInstance();
                            Thread moduleThread = new Thread() {
                                @Override
                                public void run() {
                                    nModule.setupModule();
                                }
                            };
                            moduleThread.setDaemon(true);
                            moduleThread.start();
                            synchronized (deviceModules) {
                                deviceModules.add(nModule);
                            }
                        }
                    }
                    
                    if (isJAX(nClass)) {
                        foundClasses.add(nClass);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foundClasses;
    }
    
    public JaxRsDynamicLoader(File jarName) {
        HashSet<Class<?>> c = getClasseNamesInPackage(jarName);
        classes = Collections.unmodifiableSet(c);
    }
    
    public void stopModule() {
        synchronized(deviceModules) {
            for (DeviceModule module : deviceModules) {
                module.haltModule();
            }
            deviceModules.clear();
        }
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
