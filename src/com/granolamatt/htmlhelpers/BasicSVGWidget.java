/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

import com.granolamatt.html.HTMLBody;
import com.granolamatt.html.HTMLDiv;
import com.granolamatt.html.HTMLHead;
import com.granolamatt.html.HTMLScript;
import com.granolamatt.html.HTMLhtml;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author root
 */
public class BasicSVGWidget extends HTMLScript {

    private final HTMLScript script = new HTMLScript();
    private int height = 600;
    private int width = 600;

//    private final HTMLP para = new HTMLP();
    public BasicSVGWidget() {
    }

    public void setHeight(int h) {
        height = h;
    }

    public void setWidth(int w) {
        width = w;
    }

    private String loadSVG() {
        StringBuilder ret = new StringBuilder();
        ret.append("var rawSVG = '");
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("com/granolamatt/resources/drawing.svg");
            try (BufferedReader buff = new BufferedReader(new InputStreamReader(in))) {
                String line = buff.readLine();
                while (line != null) {
                    System.out.println("Line " + line);
                    ret.append(line);
                    line = buff.readLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ret.append("';\n");
        return ret.toString();
    }

    private String getSVG() {
        StringBuilder ret = new StringBuilder();
//        ret.append("var rawSVG = '<svg width=\"100%\" height=\"100%\" version=\"1.1\" xmlns:z=\"http://debeissat.nicolas.free.fr/svg3d/svg3d.rng\" xmlns=\"http://www.w3.org/2000/svg\" onload=\"init(this)\" z:rotationTime=\"100\" z:sortAlgo=\"none\"><circle cx=\"100\" cy=\"50\" r=\"40\" stroke=\"black\" stroke-width=\"2\" fill=\"red\"></circle></svg>';\n");
//        ret.append(loadSVG());
        ret.append(String.format("var draw = SVG('canvas').size(%d, %d);\n", height, width));
        ret.append("var image = draw.svg(rawSVG);\n");
        return ret.toString();
    }

    @Override
    public String toString() {
        StringBuilder cont = new StringBuilder();
        HTMLhtml document = new HTMLhtml();
        HTMLHead head = new HTMLHead();
        head.addHTMLContent(new HTMLScript().addAttribute("src", "/resources/com/granolamatt/resources/svg.js").addAttribute("type", "text/javascript"));
        head.addHTMLContent(new HTMLScript().addAttribute("src", "/resources/com/granolamatt/resources/svg.import.js").addAttribute("type", "text/javascript"));

        document.addHTMLContent(head);
        HTMLBody body = new HTMLBody();
        body.addHTMLContent(new HTMLDiv().addAttribute("id", "canvas"));
        script.addAttribute("type", "text/javascript");
        script.addText(getSVG());
        body.addHTMLContent(script);
        document.addHTMLContent(body);
        document.getHTML(cont);
        return cont.toString();
    }
}
