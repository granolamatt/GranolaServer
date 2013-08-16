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
public class HardwareMemory {

    /**
     * This class was taken directly from Allan Barr at
     * https://github.com/alanbarr/RaspberryPi-GPIO Many many thanks!!!
     */
    private static IntBuffer gpioMem = null;
    private static IntBuffer i2cMem = null;
    private static int pcbVersion;
    private static int i2cByteTxTime_ms;

    public enum eFunction {

        input(GPFSEL_INPUT), /**
         * < Set pin to input
         */
        output(GPFSEL_OUTPUT), /**
         * < Set pin to output
         */
        alt0(GPFSEL_ALT0), /**
         * < Set pin to alternative function 0
         */
        alt1(GPFSEL_ALT1), /**
         * < Set pin to alternative function 1
         */
        alt2(GPFSEL_ALT2), /**
         * < Set pin to alternative function 2
         */
        alt3(GPFSEL_ALT3), /**
         * < Set pin to alternative function 3
         */
        alt4(GPFSEL_ALT4), /**
         * < Set pin to alternative function 4
         */
        alt5(GPFSEL_ALT5), /**
         * < Set pin to alternative function 5
         */
        eFunctionMin(GPFSEL_INPUT), /**
         * < Minimum valid value for enum
         */
        eFunctionMax(GPFSEL_ALT3);
        /**
         * < Maximum valid value for enum
         */
        private int numVal;

        eFunction(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public enum eResistor {

        pullDisable(GPPUD_DISABLE), /**
         * < No resistor
         */
        pulldown(GPPUD_PULLDOWN), /**
         * < Pulldown resistor
         */
        pullup(GPPUD_PULLUP);
        /**
         * < Pullup resistor
         */
        private int numVal;

        eResistor(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    public enum errors {

        OK,
        ERROR_DEFAULT,
        ERROR_INVALID_PIN_NUMBER,
        ERROR_RANGE,
        ERROR_NULL,
        ERROR_EXTERNAL,
        ERROR_NOT_INITIALISED,
        ERROR_ALREADY_INITIALISED,
        ERROR_I2C_NACK,
        ERROR_I2C,
        ERROR_I2C_CLK_TIMEOUT,
        ERROR_INVALID_BSC
    };

    public enum eState {

        low, high
    };
    /**
     * @ brief List of all BCM2835 pins available through the rev1 Raspberry Pi
     * header
     */
    public static final int[] REV1_PINS = {0, 1, 4, 7, 8, 9, 10, 11, 14, 15, 17, 18, 21, 22, 23, 24, 25};
    /**
     * @ brief List of all BCM2835 pins available through the rev2 Raspberry Pi
     * header
     */
    public static final int[] REV2_PINS = {2, 3, 4, 7, 8, 9, 10, 11, 14, 15, 17, 18, 22, 23, 24, 25, 27};
    /**
     * ***************************************************************************
     */
    /* The following are the physical GPIO addresses                              */
    /**
     * ***************************************************************************
     */
    public static final int GPFSEL0 = 0x20200000;
    /**
     * <GPIO Function Select 0 Register Address
     */
    public static final int GPFSEL1 = 0x20200004;
    /**
     * <GPIO Function Select 1 Register Address
     */
    public static final int GPFSEL2 = 0x20200008;
    /**
     * <GPIO Function Select 2 Register Address
     */
    public static final int GPFSEL3 = 0x2020000C;
    /**
     * <GPIO Function Select 3 Register Address
     */
    public static final int GPFSEL4 = 0x20200010;
    /**
     * <GPIO Function Select 4 Register Address
     */
    public static final int GPFSEL5 = 0x20200014;
    /**
     * <GPIO Function Select 5 Register Address
     */
    public static final int GPSET0 = 0x2020001C;
    /**
     * <GPIO Pin Output Set 0 Register Address
     */
    public static final int GPSET1 = 0x20200020;
    /**
     * <GPIO Pin Output Set 1 Register Address
     */
    public static final int GPCLR0 = 0x20200028;
    /**
     * <GPIO Pin Output Clear 0 Register Address
     */
    public static final int GPCLR1 = 0x2020002C;
    /**
     * <GPIO Pin Output Clear 1 Register Address
     */
    public static final int GPLEV0 = 0x20200034;
    /**
     * <GPIO Pin Level 0 Register Address
     */
    public static final int GPLEV1 = 0x20200038;
    /**
     * <GPIO Pin Level 1 Register Address
     */
    public static final int GPEDS0 = 0x20200040;
    /**
     * <GPIO Pin Event Detect Status 0 Register Address
     */
    public static final int GPEDS1 = 0x20200044;
    /**
     * <GPIO Pin Event Detect Status 1 Register Address
     */
    public static final int GPREN0 = 0x2020004C;
    /**
     * <GPIO Pin Rising Edge Detect Enable 0 Register Address
     */
    public static final int GPREN1 = 0x20200050;
    /**
     * <GPIO Pin Rising Edge Detect Enable 1 Register Address
     */
    public static final int GPHEN0 = 0x20200064;
    /**
     * <GPIO Pin High Detect Enable 0 Register Address
     */
    public static final int GPHEN1 = 0x20200068;
    /**
     * <GPIO Pin High Detect Enable 1 Register Address
     */
    public static final int GPAREN0 = 0x2020007C;
    /**
     * <GPIO Pin Async. Rising Edge Detect 0 Register Address
     */
    public static final int GPAREN1 = 0x20200080;
    /**
     * <GPIO Pin Async. Rising Edge Detect 1 Register Address
     */
    public static final int GPAFEN0 = 0x20200088;
    /**
     * <GPIO Pin Async. Falling Edge Detect 0 Register Address
     */
    public static final int GPAFEN1 = 0x2020008C;
    /**
     * <GPIO Pin Async. Falling Edge Detect 1 Register Address
     */
    public static final int GPPUD = 0x20200094;
    /**
     * <GPIO Pin Pull-up/down Enable Register Address
     */
    public static final int GPPUDCLK0 = 0x20200098;
    /**
     * <GPIO Pin Pull-up/down Enable Clock 0 Register Address
     */
    public static final int GPPUDCLK1 = 0x2020009C;
    /**
     * <GPIO Pin Pull-up/down Enable Clock 1 Register Address
     */
    /**
     * *******************************************************************************
     */
    /* The following are offset address which can be used with a pointer to GPIO_BASE */
    /**
     * *******************************************************************************
     */
    public static final int GPIO_BASE = GPFSEL0;
    /**
     * < First GPIO address of interest.
     */
    public static final int GPFSEL0_OFFSET = 0x000000;
    /**
     * < GPIO Function Select 0 Offset from GPIO_BASE
     */
    public static final int GPFSEL1_OFFSET = 0x000004;
    /**
     * < GPIO Function Select 1 Offset from GPIO_BASE
     */
    public static final int GPFSEL2_OFFSET = 0x000008;
    /**
     * < GPIO Function Select 2 Offset from GPIO_BASE
     */
    public static final int GPFSEL3_OFFSET = 0x00000C;
    /**
     * < GPIO Function Select 3 Offset from GPIO_BASE
     */
    public static final int GPFSEL4_OFFSET = 0x000010;
    /**
     * < GPIO Function Select 4 Offset from GPIO_BASE
     */
    public static final int GPFSEL5_OFFSET = 0x000014;
    /**
     * < GPIO Function Select 5 Offset from GPIO_BASE
     */
    public static final int GPSET0_OFFSET = 0x00001C;
    /**
     * < GPIO Pin Output Set 0 Offset from GPIO_BASE
     */
    public static final int GPSET1_OFFSET = 0x000020;
    /**
     * < GPIO Pin Output Set 1 Offset from GPIO_BASE
     */
    public static final int GPCLR0_OFFSET = 0x000028;
    /**
     * < GPIO Pin Output Clear 0 Offset from GPIO_BASE
     */
    public static final int GPCLR1_OFFSET = 0x00002C;
    /**
     * < GPIO Pin Output Clear 1 Offset from GPIO_BASE
     */
    public static final int GPLEV0_OFFSET = 0x000034;
    /**
     * < GPIO Pin Level 0 Offset from GPIO_BASE
     */
    public static final int GPLEV1_OFFSET = 0x000038;
    /**
     * < GPIO Pin Level 1 Offset from GPIO_BASE
     */
    public static final int GPEDS0_OFFSET = 0x000040;
    /**
     * < GPIO Pin Event Detect Status 0 Offset from GPIO_BASE
     */
    public static final int GPEDS1_OFFSET = 0x000044;
    /**
     * < GPIO Pin Event Detect Status 1 Offset from GPIO_BASE
     */
    public static final int GPREN0_OFFSET = 0x00004C;
    /**
     * < GPIO Pin Rising Edge Detect Enable 0 Offset from GPIO_BASE
     */
    public static final int GPREN1_OFFSET = 0x000050;
    /**
     * < GPIO Pin Rising Edge Detect Enable 1 Offset from GPIO_BASE
     */
    public static final int GPHEN0_OFFSET = 0x000064;
    /**
     * < GPIO Pin High Detect Enable 0 Offset from GPIO_BASE
     */
    public static final int GPHEN1_OFFSET = 0x000068;
    /**
     * < GPIO Pin High Detect Enable 1 Offset from GPIO_BASE
     */
    public static final int GPAREN0_OFFSET = 0x00007C;
    /**
     * < GPIO Pin Async. Rising Edge Detect 0 Offset from GPIO_BASE
     */
    public static final int GPAREN1_OFFSET = 0x000080;
    /**
     * < GPIO Pin Async. Rising Edge Detect 1 Offset from GPIO_BASE
     */
    public static final int GPAFEN0_OFFSET = 0x000088;
    /**
     * < GPIO Pin Async. Falling Edge Detect 0 Offset from GPIO_BASE
     */
    public static final int GPAFEN1_OFFSET = 0x00008C;
    /**
     * < GPIO Pin Async. Falling Edge Detect 1 Offset from GPIO_BASE
     */
    public static final int GPPUD_OFFSET = 0x000094;
    /**
     * < GPIO Pin Pull-up/down Enable Offset from GPIO_BASE
     */
    public static final int GPPUDCLK0_OFFSET = 0x000098;
    /**
     * < GPIO Pin Pull-up/down Enable Clock 0 Offset from GPIO_BASE
     */
    public static final int GPPUDCLK1_OFFSET = 0x00009C;
    /**
     * < GPIO Pin Pull-up/down Enable Clock 1 Offset from GPIO_BASE
     */
    /**
     * *******************************************************************************
     */
    /* Function select bits for GPFSELX. In GPFSELX registers each pin has three
     * bits associated with it. */
    /**
     * *******************************************************************************
     */
    public static final int GPFSEL_INPUT = 0x0;
    /**
     * < Sets a pin to input mode
     */
    public static final int GPFSEL_OUTPUT = 0x1;
    /**
     * < Sets a pin to output mode
     */
    public static final int GPFSEL_ALT0 = 0x4;
    /**
     * < Sets a pin to alternative function 0
     */
    public static final int GPFSEL_ALT1 = 0x5;
    /**
     * < Sets a pin to alternative function 1
     */
    public static final int GPFSEL_ALT2 = 0x6;
    /**
     * < Sets a pin to alternative function 2
     */
    public static final int GPFSEL_ALT3 = 0x7;
    /**
     * < Sets a pin to alternative function 3
     */
    public static final int GPFSEL_ALT4 = 0x3;
    /**
     * < Sets a pin to alternative function 4
     */
    public static final int GPFSEL_ALT5 = 0x2;
    /**
     * < Sets a pin to alternative function 5
     */
    public static final int GPFSEL_BITS = 0x7;
    /**
     * < Three bits per GPIO in the GPFSEL register
     */

    /* Function select bits for GPPUD - the pullup/pulldown resistor register */
    public static final int GPPUD_DISABLE = 0x0;
    /**
     * < Disables the resistor
     */
    public static final int GPPUD_PULLDOWN = 0x1;
    /**
     * < Enables a pulldown resistor
     */
    public static final int GPPUD_PULLUP = 0x2;
    /**
     * < Enables a pullup resistor
     */
    /**
     * ***************************************************************************
     */
    /* The following are the physical BSC / I2C addresses                         */
    /**
     * ***************************************************************************
     */
    public static final int BSC0_C = 0x20205000;
    /**
     * < BSC0 Control Register Address
     */
    public static final int BSC0_S = 0x20205004;
    /**
     * < BSC0 Status Register Address
     */
    public static final int BSC0_DLEN = 0x20205008;
    /**
     * < BSC0 Data Length Register Address
     */
    public static final int BSC0_A = 0x2020500C;
    /**
     * < BSC0 Slave Address Register Address
     */
    public static final int BSC0_FIFO = 0x20205010;
    /**
     * < BSC0 Data FIFO Register Address
     */
    public static final int BSC0_DIV = 0x20205014;
    /**
     * < BSC0 Clock Divider Register Address
     */
    public static final int BSC0_DEL = 0x20205018;
    /**
     * < BSC0 Data Delay Register Address
     */
    public static final int BSC1_C = 0x20804000;
    /**
     * < BSC1 Control Register Address
     */
    public static final int BSC1_S = 0x20804004;
    /**
     * < BSC1 Status Register Address
     */
    public static final int BSC1_DLEN = 0x20804008;
    /**
     * < BSC1 Data Length Register Address
     */
    public static final int BSC1_A = 0x2080400C;
    /**
     * < BSC1 Slave Address Register Address
     */
    public static final int BSC1_FIFO = 0x20804010;
    /**
     * < BSC1 Data FIFO Register Address
     */
    public static final int BSC1_DIV = 0x20804014;
    /**
     * < BSC1 Clock Divider Register Address
     */
    public static final int BSC1_DEL = 0x20804018;
    /**
     * < BSC1 Data Delay Register Address
     */
    public static final int BSC2_C = 0x20805000;
    /**
     * < BSC2 Control Register Address
     */
    public static final int BSC2_S = 0x20805004;
    /**
     * < BSC2 Status Register Address
     */
    public static final int BSC2_DLEN = 0x20805008;
    /**
     * < BSC2 Data Length Register Address
     */
    public static final int BSC2_A = 0x2080500C;
    /**
     * < BSC2 Slave Address Register Address
     */
    public static final int BSC2_FIFO = 0x20805010;
    /**
     * < BSC2 Data FIFO Register Address
     */
    public static final int BSC2_DIV = 0x20805014;
    /**
     * < BSC2 Clock Divider Register Address
     */
    public static final int BSC2_DEL = 0x20805018;
    /**
     * < BSC2 Data Delay Register Address
     */
    /**
     * *******************************************************************************
     */
    /* The following are the base addresses for each BSC module                       */
    /**
     * *******************************************************************************
     */
    public static final int BSC0_BASE = BSC0_C;
    /**
     * < BSC0 Base Address
     */
    public static final int BSC1_BASE = BSC1_C;
    /**
     * < BSC1 Base Address
     */
    public static final int BSC2_BASE = BSC2_C;
    /**
     * < BSC2 Base Address
     */
    /**
     * *******************************************************************************
     */
    /* The following are offset addresses which can be used with a pointer to the
     * appropriate BSC base */
    /**
     * *******************************************************************************
     */
    public static final int BSC_C_OFFSET = 0x00000000;
    /**
     * < BSC Control offset from BSCx_BASE
     */
    public static final int BSC_S_OFFSET = 0x00000004;
    /**
     * < BSC Status offset from BSCx_BASE
     */
    public static final int BSC_DLEN_OFFSET = 0x00000008;
    /**
     * < BSC Data Length offset from BSCx_BASE
     */
    public static final int BSC_A_OFFSET = 0x0000000C;
    /**
     * < BSC Slave Address offset from BSCx_BASE
     */
    public static final int BSC_FIFO_OFFSET = 0x00000010;
    /**
     * < BSC Data FIFO offset from BSCx_BASE
     */
    public static final int BSC_DIV_OFFSET = 0x00000014;
    /**
     * < BSC Clock Divider offset from BSCx_BASE
     */
    public static final int BSC_DEL_OFFSET = 0x00000018;
    /**
     * < BSC Data Delay offset from BSCx_BASE
     */
    /**
     * *******************************************************************************
     */
    /* The following are the BSC Control Register Bits                                */
    /**
     * *******************************************************************************
     */
    public static final int BSC_I2CEN = 0x8000;
    /**
     * < BSC Control: I2C Enable Bit
     */
    public static final int BSC_INTR = 0x0400;
    /**
     * < BSC Control: Interrupt on RX bit
     */
    public static final int BSC_INTT = 0x0200;
    /**
     * < BSC Control: Interrupt on TX bit
     */
    public static final int BSC_INTD = 0x0100;
    /**
     * < BSC Control: Interrupt on DONE bit
     */
    public static final int BSC_ST = 0x0080;
    /**
     * < BSC Control: Start transfer bit
     */
    public static final int BSC_CLEAR = 0x0010;
    /**
     * < BSC Control: Clear FIFO bit
     */
    public static final int BSC_READ = 0x0001;
    /**
     * < BSC Control: Read Packet Transfer bit
     */
    /**
     * *******************************************************************************
     */
    /* The following are the BSC Status Register Bits                                 */
    /**
     * *******************************************************************************
     */
    public static final int BSC_CLKT = 0x200;
    /**
     * < BSC Status: Clock Stretch Timeout bit
     */
    public static final int BSC_ERR = 0x100;
    /**
     * < BSC Status: Ack Error bit
     */
    public static final int BSC_RXF = 0x080;
    /**
     * < BSC Status: FIFO Full bit
     */
    public static final int BSC_TXE = 0x040;
    /**
     * < BSC Status: FIFO Empty bit
     */
    public static final int BSC_RXD = 0x020;
    /**
     * < BSC Status: FIFO Contains Data
     */
    public static final int BSC_TXD = 0x010;
    /**
     * < BSC Status: FIFO Can Accept Data bit
     */
    public static final int BSC_RXR = 0x008;
    /**
     * < BSC Status: FIFO Needs Reading bit
     */
    public static final int BSC_TXW = 0x004;
    /**
     * < BSC Status: FIFO Needs Writing bit
     */
    public static final int BSC_DONE = 0x002;
    /**
     * < BSC Status: Transfer Done
     */
    public static final int BSC_TA = 0x001;
    /**
     * < BSC Status: Transfer Active
     */
    public static final int BSC_FIFO_SIZE = 16;
    /**
     * @brief The size the I2C mapping is required to be.
     */
    public static final int I2C_MAP_SIZE = BSC_DEL_OFFSET;
    /**
     * @brief Default I2C clock frequency (Hertz)
     */
    public static final int I2C_DEFAULT_FREQ_HZ = 100000;
    /**
     * @brief nano seconds in a second
     */
    public static final int MSEC_IN_SEC = 1000;
    /**
     * @brief Clock pulses per I2C byte - 8 bits + ACK
     */
    public static final int CLOCKS_PER_BYTE = 9;
    /**
     * The size the GPIO mapping is required to be. GPPUDCLK1_OFFSET is the last
     * * register offset of interest.
     */
    public static final int GPIO_MAP_SIZE = (GPPUDCLK1_OFFSET);
    /**
     * Number of GPIO pins which are available on the Raspberry Pi.
     */
    public static final int NUMBER_GPIO = 17;
    /**
     * Delay for changing pullup/pulldown resistors. It should be at least 150 *
     * cycles which is 0.6 uS (1 / 250 MHz * 150). (250 Mhz is the core clock)
     */
    public static final int RESISTOR_SLEEP_US = 1;

    /**
     * @brief BSC_C register
     */
    private static int getI2C_C() {
        return i2cMem.get(BSC_C_OFFSET / 4);
    }

    /**
     * @brief BSC_C register
     */
    private static void putI2C_C(int val) {
        i2cMem.put(BSC_C_OFFSET / 4, val);
    }

    /**
     * @brief BSC_DIV register
     */
    private static int getI2C_DIV() {
        return i2cMem.get(BSC_DIV_OFFSET / 4);
    }

    /**
     * @brief BSC_DIV register
     */
    private static void putI2C_DIV(int val) {
        i2cMem.put(BSC_DIV_OFFSET / 4, val);
    }

    /**
     * @brief BSC_A register
     */
    private static int getI2C_A() {
        return i2cMem.get(BSC_A_OFFSET / 4);
    }

    /**
     * @brief BSC_A register
     */
    private static void putI2C_A(int val) {
        i2cMem.put(BSC_A_OFFSET / 4, val);
    }

    /**
     * @brief BSC_DLEN register
     */
    private static int getI2C_DLEN() {
        return i2cMem.get(BSC_DLEN_OFFSET / 4);
    }

    /**
     * @brief BSC_DLEN register
     */
    private static void putI2C_DLEN(int val) {
        i2cMem.put(BSC_DLEN_OFFSET / 4, val);
    }

    /**
     * @brief BSC_S register
     */
    private static int getI2C_S() {
        return i2cMem.get(BSC_S_OFFSET / 4);
    }

    /**
     * @brief BSC_S register
     */
    private static void putI2C_S(int val) {
        i2cMem.put(BSC_S_OFFSET / 4, val);
    }

    /**
     * @brief BSC_FIFO register
     */
    private static int getI2C_FIFO() {
        return i2cMem.get(BSC_FIFO_OFFSET / 4);
    }

    /**
     * @brief BSC_FIFO register
     */
    private static void putI2C_FIFO(int val) {
        i2cMem.put(BSC_FIFO_OFFSET / 4, val);
    }

    /**
     * @brief GPSET_0 register
     */
    public static void putGPIO_GPSET0(int val) {
        gpioMem.put(GPSET0_OFFSET / 4, val);
    }

    /**
     * @brief GPIO_GPCLR0 register
     */
    public static void putGPIO_GPCLR0(int val) {
        gpioMem.put(GPCLR0_OFFSET / 4, val);
    }

    /**
     * @brief GPIO_GPLEV0 register
     */
    public static int getGPIO_GPLEV0() {
        return gpioMem.get(GPLEV0_OFFSET / 4);
    }

    /**
     * @brief GPIO_GPPUD register
     */
    private static int getGPIO_GPPUD() {
        return gpioMem.get(GPPUD_OFFSET / 4);
    }

    /**
     * @brief GPIO_GPPUD register
     */
    private static void putGPIO_GPPUD(int val) {
        gpioMem.put(GPPUD_OFFSET / 4, val);
    }

    /**
     * @brief GPIO_GPPUDCLK0 register
     */
    private static int getGPIO_GPPUDCLK0() {
        return gpioMem.get(GPPUDCLK0_OFFSET / 4);
    }

    /**
     * @brief GPIO_GPPUDCLK0 register
     */
    private static void putGPIO_GPPUDCLK0(int val) {
        gpioMem.put(GPPUDCLK0_OFFSET / 4, val);
    }

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

            int version = getPCBRev();

            pcbVersion = version;

            if (version != 1 && version != 2) {
                System.out.println("Version is " + version);
                throw (new UnsatisfiedLinkError("no version number"));
            }

            System.out.println("!!!!!!!!!!!!!!!! Loaded Pi PCB Version " + version);

            ByteBuffer memgpio = setupIO();
            memgpio.order(ByteOrder.LITTLE_ENDIAN);
            gpioMem = memgpio.asIntBuffer();

            ByteBuffer memi2c = setupI2C(pcbVersion);
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

    public static void gpioSetFunction(int gpioNumber, eFunction function) {
        /* Clear what ever function bits currently exist - this puts the pin
         * into input mode.*/
        int val = gpioMem.get((gpioNumber / 10));
        val &= ~(GPFSEL_BITS << ((gpioNumber % 10) * 3));
        gpioMem.put((gpioNumber / 10), val);

        /* Set the three pins for the pin to the desired value */
        val = gpioMem.get((gpioNumber / 10));
        val |= (function.getNumVal() << ((gpioNumber % 10) * 3));
        gpioMem.put((gpioNumber / 10), val);
    }

    public static void gpioSetPin(int gpioNumber, eState state) {
        if (state == eState.high) {
            /* The offsets are all in bytes. Divide by sizeof uint32_t to allow
             * pointer addition. */
            putGPIO_GPSET0(0x1 << gpioNumber);
        } else if (state == eState.low) {
            /* The offsets are all in bytes. Divide by sizeof uint32_t to allow
             * pointer addition. */
            putGPIO_GPCLR0(0x1 << gpioNumber);
        }
    }

    public static eState gpioReadPin(int gpioNumber) {
        boolean low = ((getGPIO_GPLEV0() & (0x1 << gpioNumber)) == 0);
        if (low) {
            return eState.low;
        } else {
            return eState.high;
        }
    }

    public static void gpioSetPullResistor(int gpioNumber, eResistor resistorOption) {
        putGPIO_GPPUD(resistorOption.getNumVal());
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(HardwareMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Clock the control signal for desired resistor */
        putGPIO_GPPUDCLK0((0x1 << gpioNumber));
        /* Hold to set */
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(HardwareMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
        putGPIO_GPPUD(0);
        putGPIO_GPPUDCLK0(0);
    }

    public static void gpioI2cSetup() {
        int scl = 3;
        int sda = 2;
        if (pcbVersion == 1) {
            scl = 1;
            sda = 0;
        } else if (pcbVersion == 2) {
            scl = 3;
            sda = 2;
        }
        gpioSetPullResistor(sda, eResistor.pullDisable);
        gpioSetPullResistor(scl, eResistor.pullDisable);
        gpioSetFunction(sda, eFunction.alt0);
        gpioSetFunction(scl, eFunction.alt0);
        gpioI2cSetClock(I2C_DEFAULT_FREQ_HZ);
        /* Setup the Control Register.
         * Enable the BSC Controller.
         * Clear the FIFO. */
        putI2C_C(BSC_I2CEN | BSC_CLEAR);

        /* Setup the Status Register
         * Clear NACK ERR flag.
         * Clear Clock stretch flag.
         * Clear Done flag. */
        putI2C_S(BSC_ERR | BSC_CLKT | BSC_DONE);
    }

    public static void gpioI2cCleanup() {
        int scl = 3;
        int sda = 2;
        if (pcbVersion == 1) {
            scl = 1;
            sda = 0;
        } else if (pcbVersion == 2) {
            scl = 3;
            sda = 2;
        }
        gpioSetFunction(sda, eFunction.input);
        gpioSetFunction(scl, eFunction.input);
        /* Disable the BSC Controller */
        int val = getI2C_C();
        val &= ~BSC_I2CEN;
        putI2C_C(val);
    }

    public static void gpioI2cSet7BitSlave(int slaveAddress) {
        putI2C_A(slaveAddress);
    }

    public static errors gpioI2cWriteData(int[] data) {
        errors rtn;
        int dataLength = data.length;
        int dataIndex = 0;
        int dataRemaining = dataLength;

        /* Clear the FIFO */
        int val = getI2C_C();
        val |= BSC_CLEAR;
        putI2C_C(val);

        /* Configure Control for a write */
        val = getI2C_C();
        val &= ~BSC_READ;
        putI2C_C(val);

        /* Set the Data Length register to dataLength */
        putI2C_DLEN(dataLength);

        /* Configure Control Register for a Start */
        val = getI2C_C();
        val |= BSC_ST;
        putI2C_C(val);

        /* Main transmit Loop - While Not Done */
        while ((getI2C_S() & BSC_DONE) != 0) {
            while (((getI2C_S() & BSC_TXD) != 0) && (dataRemaining > 0)) {
                putI2C_FIFO(data[dataIndex]);
                dataIndex++;
                dataRemaining--;
            }

            int sleep = 0;
            /* FIFO should be full at this point. If data remaining to be added
             * sleep for time it should take to approximately half empty FIFO */
            if (dataRemaining > 0) {
                sleep = i2cByteTxTime_ms * BSC_FIFO_SIZE / 2;
            } /* Otherwise all data is currently in the FIFO, sleep for how many
             * bytes are in the FIFO to be transmitted */ /* TODO DOUBLE? */ else {
                sleep = getI2C_DLEN() * i2cByteTxTime_ms;
            }
            try {
                Thread.sleep(val);
            } catch (InterruptedException ex) {
                Logger.getLogger(HardwareMemory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /* Received a NACK */
        if ((getI2C_S() & BSC_ERR) != 0) {
            val = getI2C_S();
            val |= BSC_ERR;
            putI2C_S(val);
            System.out.println("Received a NACK.");
            rtn = errors.ERROR_I2C_NACK;
        } /* Received Clock Timeout error */ else if ((getI2C_S() & BSC_CLKT) != 0) {
            val = getI2C_S();
            val |= BSC_CLKT;
            putI2C_S(val);
            System.out.println("Received a Clock Stretch Timeout.");
            rtn = errors.ERROR_I2C_CLK_TIMEOUT;
        } else if (dataRemaining > 0) {
            System.out.println("BSC signaled done but %d data remained." + dataRemaining);
            rtn = errors.ERROR_I2C;
        } else {
            rtn = errors.OK;
        }

        /* Clear the DONE flag */
        val = getI2C_S();
        val |= BSC_DONE;
        putI2C_S(val);

        return rtn;
    }

    public static errors gpioI2cReadData(int[] buffer) {
        errors rtn = errors.ERROR_DEFAULT;
        int bytesToRead = buffer.length;
        int bufferIndex = 0;
        int dataRemaining = bytesToRead;


        /* Clear the FIFO */
        int val = getI2C_C();
        val |= BSC_CLEAR;
        putI2C_C(val);

        /* Configure Control for a write */
        val = getI2C_C();
        val |= BSC_READ;
        putI2C_C(val);

        /* Set the Data Length register to dataLength */
        putI2C_DLEN(bytesToRead);

        /* Configure Control Register for a Start */
        val = getI2C_C();
        val |= BSC_ST;
        putI2C_C(val);

        /* Main Receive Loop - While Transfer is not done */
        while ((getI2C_S() & BSC_DONE) == 0) {
            /* FIFO Contains Data. Read until empty */
            while ((getI2C_S() & BSC_RXD) != 0 && dataRemaining > 0) {
                buffer[bufferIndex] = getI2C_FIFO();
                bufferIndex++;
                dataRemaining--;
            }

            int sleep = 0;
            /* FIFO should be empty at this point. If more than one full FIFO
             * remains to be read sleep for time to approximately half fill
             * FIFO */
            if (dataRemaining > BSC_FIFO_SIZE) {
                sleep = i2cByteTxTime_ms * BSC_FIFO_SIZE / 2;
            } /* Otherwise, sleep for the number of bytes to be received */ /*TODO DOUBLE ?*/ else {
                sleep = getI2C_DLEN() * i2cByteTxTime_ms;
            }

            /* Sleep for approximate time to receive half the FIFO */
            sleep = sleep > BSC_FIFO_SIZE ? BSC_FIFO_SIZE / 2 : getI2C_DLEN() / 2;
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                Logger.getLogger(HardwareMemory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /* FIFO Contains Data. Read until empty */
        while ((getI2C_S() & BSC_RXD) != 0 && dataRemaining > 0) {
            buffer[bufferIndex] = getI2C_FIFO();
            bufferIndex++;
            dataRemaining--;
        }

        /* Received a NACK */
        if ((getI2C_S() & BSC_ERR) > 0) {
            val = getI2C_S();
            val |= BSC_ERR;
            putI2C_S(val);
            System.out.println("Received a NACK");
            rtn = errors.ERROR_I2C_NACK;
        } /* Received Clock Timeout error. */ else if ((getI2C_S() & BSC_CLKT) != 0) {
            val = getI2C_S();
            val |= BSC_CLKT;
            putI2C_S(val);
            System.out.println("Received a Clock Stretch Timeout");
            rtn = errors.ERROR_I2C_CLK_TIMEOUT;
        } else if (dataRemaining > 0) {
            System.out.println("BSC signaled done but data remained.");
            rtn = errors.ERROR_I2C;
        } else {
            rtn = errors.OK;
        }

        /* Clear the DONE flag */
        val = getI2C_S();
        val |= BSC_DONE;
        putI2C_S(val);

        return rtn;
    }

    private static void gpioI2cSetClock(int frequency) {
        /*Note CDIV is always rounded down to an even number */
        putI2C_DIV(250000000 / frequency);
        i2cByteTxTime_ms = (int) (1.0 / ((float) frequency / MSEC_IN_SEC)
                * CLOCKS_PER_BYTE);
    }

    public static void loadDriver() {
        LoggerOut.println("loaded dpio hardware memory");
    }

    private static native int getPCBRev();

    private static native ByteBuffer setupIO();

    private static native ByteBuffer setupI2C(int pcbRevNumber);

    private static native void downUser(String user);
}
