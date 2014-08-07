/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.html;

/**
 *
 * @author root
 */
public class HTMLInput extends HTMLType {
    private final StringBuilder paragraph = new StringBuilder();

    public HTMLInput() {
        super();
        setCloses(false);
    }

    public HTMLInput addLine(String line) {
        paragraph.append(line).append("<br>\n");
        return this;
    }

    public HTMLInput addText(String ntext) {
        paragraph.append(ntext);
        return this;
    }

    @Override
    public void getValue(StringBuilder output) {
        output.append(paragraph);
    }
}
