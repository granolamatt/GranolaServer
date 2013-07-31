/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.granolamatt.hardware;

import com.granolamatt.logger.LoggerOut;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 *
 * @author root
 */
public class HardwareMemory {

    private static IntBuffer hardwareMem = null;

    /**
     * load the native libraries
     */
    static {
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("com/granolamatt/hardware/libGranolaserver.so");
            File f = File.createTempFile("libGranolaserver", "so");
            OutputStream out = new FileOutputStream(f);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            System.load(f.getAbsolutePath());

            ByteBuffer mem = setupIO();
            mem.order(ByteOrder.LITTLE_ENDIAN);
            hardwareMem = mem.asIntBuffer();
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Loading arm library did not work, are you on the pi");
            hardwareMem = IntBuffer.allocate(1000);
        } catch (Exception ex) {
            System.out.println("Error loading so");
        }
    }

    public static void loadDriver() {
        LoggerOut.println("loaded dpio hardware memory");
    }

//    #define INP_GPIO(g) *(gpio+((g)/10)) &= ~(7<<(((g)%10)*3))
    public static void InpGPIO(final int g) {
        int val = hardwareMem.get(g / 10);
        hardwareMem.put(g / 10, val &= ~(7 << (((g) % 10) * 3)));
    }

//    #define OUT_GPIO(g) *(gpio+((g)/10)) |=  (1<<(((g)%10)*3))
    public static void OutGPIO(final int g) {
        int val = hardwareMem.get(g / 10);
        hardwareMem.put(g / 10, val |= (1 << (((g) % 10) * 3)));
    }
//    #define SET_GPIO_ALT(g,a) *(gpio+(((g)/10))) |= (((a)<=3?(a)+4:(a)==4?3:2)<<(((g)%10)*3))

    public static void AltGPIO(final int g, final int a) {
        int val = hardwareMem.get(g / 10);
        hardwareMem.put(g / 10, val |= (((a) <= 3 ? (a) + 4 : (a) == 4 ? 3 : 2) << (((g) % 10) * 3)));
    }

// #define GPIO_SET *(gpio+7)  // sets   bits which are 1 ignores bits which are 0
    public static void GPIOSetPinNumber(final int pinNumber) {
        hardwareMem.put(7, 1 << pinNumber);
    }

// #define GPIO_CLR *(gpio+10) // clears bits which are 1 ignores bits which are 0
    public static void GPIOClrPinNumber(final int pinNumber) {
        hardwareMem.put(10, 1 << pinNumber);
    }
    
// #define GPIO_SET *(gpio+7)  // sets   bits which are 1 ignores bits which are 0
    public static void GPIOSet(final int pinData) {
        hardwareMem.put(7, pinData);
    }

// #define GPIO_CLR *(gpio+10) // clears bits which are 1 ignores bits which are 0
    public static void GPIOClr(final int pinData) {
        hardwareMem.put(10, pinData);
    }

//    public static ByteBuffer getHardwareMemory() {
//        return hardwareMem;
//    }
    private static native ByteBuffer setupIO();
}
