/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

/**
 *
 * @author root
 */
public class BasicDocument {

    private final StringBuilder buffer = new StringBuilder();
    private int refresh = -1;

    public BasicDocument() {
    }

    public BasicDocument(int refresh) {
        this.refresh = refresh;
    }

    public void setRefresh(int seconds) {
        refresh = seconds;
    }

    public void addLine(String line) {
        buffer.append(line).append("<br>\n");
    }

    public void addContent(String content) {
        buffer.append(content);
    }

    public void addContent(StringBuilder content) {
        buffer.append(content);
    }

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append("<!DOCTYPE html>\n");
        sbuilder.append("<html>\n");
        if (refresh >= 0) {
            sbuilder.append("<META HTTP-EQUIV=\"refresh\" CONTENT=\"");
            sbuilder.append(refresh);
            sbuilder.append("\">\n");
        }
        sbuilder.append("<body>\n");
        sbuilder.append("<p>");
        sbuilder.append(buffer);
        sbuilder.append("</p>\n");

        sbuilder.append("</body>\n");
        sbuilder.append("</html>\n");
        return sbuilder.toString();
    }
}
