/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.dynamicloader;
import com.granolamatt.logger.LoggerOut;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

public class JarFileLoader extends URLClassLoader
{
    public JarFileLoader (URL[] urls)
    {
        super (urls);
    }

    public void addFile (String path) throws MalformedURLException
    {
        String urlPath = "jar:file://" + path + "!/";
        addURL (new URL (urlPath));
    }

    public static void main (String args [])
    {
        try
        {
            LoggerOut.println ("First attempt...");
            Class.forName ("org.gjt.mm.mysql.Driver");
        }
        catch (Exception ex)
        {
            LoggerOut.println ("Failed.");
        }

        try
        {
            URL urls [] = {};

            JarFileLoader cl = new JarFileLoader (urls);
            cl.addFile ("/opt/mysql-connector-java-5.0.4/mysql-connector-java-5.0.4-bin.jar");
            LoggerOut.println ("Second attempt...");
            cl.loadClass ("org.gjt.mm.mysql.Driver");
            LoggerOut.println ("Success!");
        }
        catch (Exception ex)
        {
            LoggerOut.println ("Failed.");
            ex.printStackTrace ();
        }
    }
}
