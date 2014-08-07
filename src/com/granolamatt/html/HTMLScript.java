/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.html;


/**
 *
 * @author root
 */
public class HTMLScript extends HTMLType {

    private final StringBuilder paragraph = new StringBuilder();
    
    public HTMLScript addLine(String line) {
        paragraph.append(line).append("\n");
        return this;
    }
    
    
    public HTMLScript addText(String ntext) {
        paragraph.append(ntext);
        return this;
    }

    @Override
    public void getValue(StringBuilder output) {
        output.append(paragraph);
    }
}
