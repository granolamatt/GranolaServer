/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.widget;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.adrianwalker.multilinestring.Multiline;

/**
 *
 * @author matt
 */
public class MultiLineTest {

    /**
     * @return the html
     */
    public static String getHtml() {
        return html;
    }

    private String time = "Now";
    private int name = 100;

    /**
     * <html>
     * <head/>
     * <body>
     * <p>
     * Hello
     * {@link #getName()} is {@link #getTime()}
     * World
     * </p>
     * </body>
     * </html>
     */
    @Multiline
    private static String html;

//    public String substituteVariables(String template) throws Exception {
//
////        Method method = this.getClass().getDeclaredMethod("getName");
////        System.out.println("Methods are " + method.invoke(this));
//
//        Pattern pattern = Pattern.compile("\\@\\{(.+?)\\}");
//        Matcher matcher = pattern.matcher(template);
//        StringBuffer buffer = new StringBuffer();
//        while (matcher.find()) {
//            Field match = this.getClass().getDeclaredField(matcher.group(1));
//            if (match != null) {
//                String replacement = match.get(this).toString();
//                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
//            } else {
//                throw new Exception("Match for variable not found");
//            }
//        }
//        matcher.appendTail(buffer);
//        return buffer.toString();
//    }

    public void setName(int n) {
        name = n;
    }

    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        String test = "";
        MultiLineTest tester = new MultiLineTest();
//        tester.setName(55);
        try {
            test = WidgetUtils.substituteVariablesWithMethod(getHtml(), tester);
        } catch (Exception ex) {
            Logger.getLogger(MultiLineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Test is now " + test);
        
        LEDResource led = new LEDResource();
        System.out.println("HTML: " + led.getHTMLDocument());
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @return the name
     */
    public int getName() {
        return name;
    }

}
