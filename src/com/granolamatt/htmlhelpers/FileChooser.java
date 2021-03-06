/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.htmlhelpers;

import com.granolamatt.html.HTMLDiv;
import com.granolamatt.html.HTMLForm;
import com.granolamatt.html.HTMLInput;
import com.granolamatt.html.HTMLP;

/**
 *
 * @author root
 */
public class FileChooser {
    
    private String path = "/";
    
    
    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {

        HTMLForm form = new HTMLForm();
        form.addAttribute("action", path);
        form.addAttribute("enctype", "multipart/form-data");
        form.addAttribute("method", "post");
        HTMLP para = new HTMLP();
        form.addHTMLContent(para);
        para.addText("files: ");
        HTMLInput finput = new HTMLInput();
        para.addHTMLContent(finput);
        finput.addAttribute("type", "file");
        finput.addAttribute("name", "datafile");
        finput.addAttribute("size", "40");


        HTMLInput input = new HTMLInput();
        input.addAttribute("type", "submit");
        input.addAttribute("value", "submit");
        form.addHTMLContent(new HTMLDiv()).addHTMLContent(input);

        StringBuilder s = new StringBuilder();
        form.getHTML(s);


        return s.toString();
    }
}
