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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class HardwareMemoryBak {

    /**
     * This class was taken directly from Pieter-Jan at
     * http://www.pieter-jan.com/node/15 Many many thanks!!!
     */
    private static IntBuffer gpioMem = null;
    private static IntBuffer i2cMem = null;
    public static final int BSC_C_I2CEN = (1 << 15);
    public static final int BSC_C_INTR = (1 << 10);
    public static final int BSC_C_INTT = (1 << 9);
    public static final int BSC_C_INTD = (1 << 8);
    public static final int BSC_C_ST = (1 << 7);
    public static final int BSC_C_CLEAR = (1 << 4);
    public static final int BSC_C_READ = 1;
    public static final int START_READ = BSC_C_I2CEN | BSC_C_ST | BSC_C_CLEAR | BSC_C_READ;
    public static final int START_WRITE = BSC_C_I2CEN | BSC_C_ST;
    public static final int BSC_S_CLKT = (1 << 9);
    public static final int BSC_S_ERR = (1 << 8);
    public static final int BSC_S_RXF = (1 << 7);
    public static final int BSC_S_TXE = (1 << 6);
    public static final int BSC_S_RXD = (1 << 5);
    public static final int BSC_S_TXD = (1 << 4);
    public static final int BSC_S_RXR = (1 << 3);
    public static final int BSC_S_TXW = (1 << 2);
    public static final int BSC_S_DONE = (1 << 1);
    public static final int BSC_S_TA = 1;
    public static final int CLEAR_STATUS = BSC_S_CLKT | BSC_S_ERR | BSC_S_DONE;

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

            ByteBuffer memgpio = setupIO();
            memgpio.order(ByteOrder.LITTLE_ENDIAN);
            gpioMem = memgpio.asIntBuffer();

            ByteBuffer memi2c = setupI2C();
            memi2c.order(ByteOrder.LITTLE_ENDIAN);
            i2cMem = memi2c.asIntBuffer();

            downUser("pi");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Loading arm library did not work, are you on the pi");
            gpioMem = IntBuffer.allocate(1000);
            i2cMem = IntBuffer.allocate(1000);
        } catch (Exception ex) {
            System.out.println("Error loading so");
        }
    }

    public static void loadDriver() {
        LoggerOut.println("loaded dpio hardware memory");
    }

//    #define INP_GPIO(g) *(gpio+((g)/10)) &= ~(7<<(((g)%10)*3))
    public static void InpGPIO(final int g) {
        int val = gpioMem.get(g / 10);
        gpioMem.put(g / 10, val &= ~(7 << (((g) % 10) * 3)));
    }

//    #define OUT_GPIO(g) *(gpio+((g)/10)) |=  (1<<(((g)%10)*3))
    public static void OutGPIO(final int g) {
        int val = gpioMem.get(g / 10);
        gpioMem.put(g / 10, val |= (1 << (((g) % 10) * 3)));
    }
//    #define SET_GPIO_ALT(g,a) *(gpio+(((g)/10))) |= (((a)<=3?(a)+4:(a)==4?3:2)<<(((g)%10)*3))

    public static void AltGPIO(final int g, final int a) {
        int val = gpioMem.get(g / 10);
        gpioMem.put(g / 10, val |= (((a) <= 3 ? (a) + 4 : (a) == 4 ? 3 : 2) << (((g) % 10) * 3)));
    }

// #define GPIO_SET *(gpio+7)  // sets   bits which are 1 ignores bits which are 0
    public static void GPIOSetPinNumber(final int pinNumber) {
        gpioMem.put(7, 1 << pinNumber);
    }

// #define GPIO_CLR *(gpio+10) // clears bits which are 1 ignores bits which are 0
    public static void GPIOClrPinNumber(final int pinNumber) {
        gpioMem.put(10, 1 << pinNumber);
    }

// #define GPIO_SET *(gpio+7)  // sets   bits which are 1 ignores bits which are 0
    public static void GPIOSet(final int pinData) {
        gpioMem.put(7, pinData);
    }

// #define GPIO_CLR *(gpio+10) // clears bits which are 1 ignores bits which are 0
    public static void GPIOClr(final int pinData) {
        gpioMem.put(10, pinData);
    }
    
//    #define GPIO_READ(g)  *(gpio.addr + 13) &= (1<<(g))
    public static int GPIORead(final int g) {
        int ret = gpioMem.get(13);
        ret &= (1 << g);
        return ret; 
    }

//// I2C macros
//#define BSC0_C          *(bsc0.addr + 0x00)
    public static void putBSC0_C(final int data) {
        i2cMem.put(0, data);
    }

    public static int getBSC0_C() {
        return i2cMem.get(0);
    }

//#define BSC0_S          *(bsc0.addr + 0x01)
    public static void putBSC0_S(final int data) {
        i2cMem.put(1, data);
    }

    public static int getBSC0_S() {
        return i2cMem.get(1);
    }

//#define BSC0_DLEN     *(bsc0.addr + 0x02)
    public static void putBSC0_DLEN(final int data) {
        i2cMem.put(2, data);
    }

    public static int getBSC0_DLEN() {
        return i2cMem.get(2);
    }

//#define BSC0_A          *(bsc0.addr + 0x03)
    public static void putBSC0_A(final int data) {
        i2cMem.put(3, data);
    }

    public static int getBSC0_A() {
        return i2cMem.get(3);
    }

//#define BSC0_FIFO     *(bsc0.addr + 0x04)
    public static void putBSC0_FIFO(final int data) {
        i2cMem.put(4, data);
    }

    public static int getBSC0_FIFO() {
        return i2cMem.get(4);
    }

// 
//// I2C Function Prototypes
//void i2c_init();
    public void i2c_init() {
        InpGPIO(0);
        AltGPIO(0, 0);
        InpGPIO(1);
        AltGPIO(1, 0);
    }

//void wait_i2c_done();
    public void wait_i2c_done() {
        int timeout = 50;
        while (((getBSC0_S() & BSC_S_DONE) != 0) && (--timeout >= 0)) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(HardwareMemoryBak.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (timeout == 0) {
            System.out.println("Error: wait_i2c_done() timeout.");
        }
    }

    private static native ByteBuffer setupIO();

    private static native ByteBuffer setupI2C();

    private static native void downUser(String user);
}
