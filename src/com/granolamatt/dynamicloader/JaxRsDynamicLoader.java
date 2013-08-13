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
