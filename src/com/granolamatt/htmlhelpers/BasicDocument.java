/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

import com.granolamatt.html.HTMLBody;
import com.granolamatt.html.HTMLHead;
import com.granolamatt.html.HTMLMETA;
import com.granolamatt.html.HTMLP;
import com.granolamatt.html.HTMLhtml;

/**
 *
 * @author root
 */
public class BasicDocument {

    private HTMLMETA meta = null;
    
    private final HTMLP para = new HTMLP();

    public BasicDocument() {
    }

    public BasicDocument(int refresh) {
        this();
        meta = new HTMLMETA().setRefresh(refresh);
    }

    public void setRefresh(int seconds) {
        meta = new HTMLMETA().setRefresh(seconds);
    }

    public void addLine(String line) {
        para.addLine(line);
    }

    public void addContent(String content) {
        para.addText(content);
    }

    public void addContent(StringBuilder content) {
        addContent(content.toString());
    }

    @Override
    public String toString() {
        StringBuilder cont = new StringBuilder();
        HTMLhtml document = new HTMLhtml();
        if (meta != null) {
            document.addHTMLContent(meta);
        }
        document.addHTMLContent(new HTMLHead());
        document.addHTMLContent(new HTMLBody()).addHTMLContent(para);
        document.getHTML(cont);
        return cont.toString();
    }
}
