/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

/**
 *
 * @author root
 */
public class URLLister {

    private final StringBuilder buffer = new StringBuilder();
    
    public URLLister() {
        
    }

    public void addLine(String line) {
        buffer.append(line).append("<br>\n");
    }

    public void addContent(String content) {
        buffer.append(content);
    }

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append("<body>\n");
        sbuilder.append("<p>");
        sbuilder.append(buffer);
        sbuilder.append("</p>\n");

        return sbuilder.toString();
    }
}
