/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.widget;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author matt
 */
public class WidgetUtils {

    public static String substituteVariablesWithMethod(String template, Object object) throws Exception {

        Pattern pattern = Pattern.compile("\\{\\s*\\@link\\s+\\#(.+?)\\(\\)\\s*\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            Method match = object.getClass().getDeclaredMethod(matcher.group(1));
            if (match != null) {
                String replacement = match.invoke(object).toString();
                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
            } else {
                throw new Exception("Match for variable not found");
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

}
