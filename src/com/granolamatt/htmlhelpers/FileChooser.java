/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

/**
 *
 * @author root
 */
public class FileChooser {
    

    public String getContent() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append("<form action=\"/\" enctype=\"multipart/form-data\" method=\"post\">\n");
        sbuilder.append("<p>files: <input type=\"file\" name=\"datafile\" size=\"40\"></p>\n");
        sbuilder.append("<div>\n");
        sbuilder.append("<input type=\"submit\" value=\"submit\">\n");
        sbuilder.append("</div>\n");
        sbuilder.append("</form>\n");

        return sbuilder.toString();
    }
}
