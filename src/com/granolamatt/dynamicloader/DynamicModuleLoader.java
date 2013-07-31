/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.dynamicloader;

import com.granolamatt.device.DeviceModule;
import com.granolamatt.logger.LoggerOut;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author root
 */
public class DynamicModuleLoader {

    public void loadJar(File file) {
        try {

            ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
            URI url = file.toURI();
            URL[] urls = new URL[]{url.toURL()};
            ClassLoader cl = new URLClassLoader(urls, currentThreadClassLoader);
//            Class cls = cl.loadClass("com.mypackage.myclass");
            Thread.currentThread().setContextClassLoader(cl);

        } catch (Exception ex) {
            Logger.getLogger(DynamicModuleLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static List getClasseNamesInPackage(File jarName) {
        ArrayList classes = new ArrayList();

        try {
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
                    LoggerOut.println("Fould class " + className.toString());
                    classes.add(className.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public Set<Class<? extends DeviceModule>> startModule(File file) {
        final Set<Class<? extends DeviceModule>> classes = new HashSet<>();
        try {
            URI url = file.toURI();
            URL[] urls = new URL[]{url.toURL()};

            ClassLoader cl = new URLClassLoader(urls);
            getClasseNamesInPackage(file);

            List<String> classNames = new ArrayList<>();
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    // This ZipEntry represents a class. Now, what class does it represent?
                    StringBuilder className = new StringBuilder();
                    for (String part : entry.getName().split("/")) {
                        if (className.length() != 0) {
                            className.append(".");
                        }
                        className.append(part);
                        if (part.endsWith(".class")) {
                            className.setLength(className.length() - ".class".length());
                        }
                    }
                    LoggerOut.println("Fould class " + className.toString());
                    classNames.add(className.toString());
                }
            }
            LoggerOut.println("Classis size is " + classNames.size());


        } catch (Exception ex) {
            Logger.getLogger(DynamicModuleLoader.class.getName()).log(Level.SEVERE, null, ex);
        }


        return classes;
    }

    public Class<?> loadClass(File file, String classname) {
        Class<?> ret = null;
        try {
            URI url = file.toURI();
            URL[] urls = new URL[]{url.toURL()};
            ClassLoader cl = new URLClassLoader(urls);

            ret = cl.loadClass(classname);
        } catch (Exception ex) {
            Logger.getLogger(DynamicModuleLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;

    }
}
