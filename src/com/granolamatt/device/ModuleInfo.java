/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.device;

import java.util.Random;

/**
 *
 * @author root
 */
public abstract class ModuleInfo {

    private static final Random random = new Random();
    private static String context = "Unknown" + Integer.toHexString(random.nextInt());
    
    public static String getContext() {
        return context;
    }
}
