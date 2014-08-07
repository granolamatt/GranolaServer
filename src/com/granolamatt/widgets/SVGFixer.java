/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.widgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author root
 */
public class SVGFixer {

    /*
     * Reads the xml configuration file for this application and updates attributes accordingly.
     * If there are exceptions, just load the default configuration.
     */
    public void readSVGFile(String filepath) {
        try {
            SAXBuilder parser = new SAXBuilder();
            InputStream in = ClassLoader.getSystemResourceAsStream(filepath);
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));
            Document doc = parser.build(buff);
            Element rootNode = doc.getRootElement();

            recursePath(rootNode);
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(WidgetFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void recursePath(Element myElement) {
        if (myElement.getName().equals("desc")) {
            String text = myElement.getTextTrim();
            switch (text) {
                case "pushButton":
                    System.out.println("Found a pushButton");
                    System.out.println("Parent is " + myElement.getParentElement().getName());
                    break;
                case "textField":
                    System.out.println("Found a textField");
                    System.out.println("Parent is " + myElement.getParentElement().getName());
                    break;
            }
        }
        for (Element oneLevelDeep : myElement.getChildren()) {
            recursePath(oneLevelDeep);
        }
    }

}
