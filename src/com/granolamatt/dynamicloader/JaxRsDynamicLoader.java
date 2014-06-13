package com.granolamatt.dynamicloader;

import com.granolamatt.device.DeviceModule;
import com.granolamatt.logger.LoggerOut;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JaxRsDynamicLoader extends ResourceConfig {

    private final List<DeviceModule> deviceModules = new LinkedList<>();
    private static final Logger LOGGER = Logger.getLogger(JaxRsDynamicLoader.class.getName());

    public boolean isJAX(Class<?> restClass) {
        return restClass.isAnnotationPresent(javax.ws.rs.Path.class);
    }

//    private void analyzeClass(Class<?> myClass) {
//
//        Method[] methods = myClass.getDeclaredMethods();
//
//        for (int i = 0; i < methods.length; i++) {
//            Type[] params = methods[i].getGenericParameterTypes();
//            for (int j = 0; j < params.length; j++) {
//                System.out.println("Parameter: " + params[j].toString());
//            }
//        }
//    }

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
        for (Class<?> cl : c) {
            register(cl);
        }
        register(MultiPartFeature.class);
        register(SseFeature.class);
        registerInstances(new LoggingFilter(LOGGER, true));
    }

    public void stopModule() {
        synchronized (deviceModules) {
            for (DeviceModule module : deviceModules) {
                module.haltModule();
            }
            deviceModules.clear();
        }
    }

}
