/*
 * The MIT License
 *
 * Copyright 2017 pi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Comunicacion;

/**
 *
 * @author pi
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Read an Analog to Digital Converter
 */
public class ADCReader implements Runnable
{
  private final static boolean DISPLAY_DIGIT = false;
  private final static boolean DEBUG         = true;
  // Note: "Mismatch" 23-24. The wiring says DOUT->#23, DIN->#24
  // 23: DOUT on the ADC is IN on the GPIO. ADC:Slave, GPIO:Master
  // 24: DIN on the ADC, OUT on the GPIO. Same reason as above.
  // SPI: Serial Peripheral Interface
  private static Pin spiClk  = RaspiPin.GPIO_01; // Pin #18, clock
  private static Pin spiMiso = RaspiPin.GPIO_04; // Pin #23, data in.  MISO: Master In Slave Out
  private static Pin spiMosi = RaspiPin.GPIO_05; // Pin #24, data out. MOSI: Master Out Slave In
  private static Pin spiCs   = RaspiPin.GPIO_06; // Pin #25, Chip Select

    @Override
    public void run() {
        main();
    }
 
  private enum MCP3008_input_channels
  {
    CH0(0),
    CH1(1),
    CH2(2),
    CH3(3),
    CH4(4),
    CH5(5),
    CH6(6),
    CH7(7);
    
    private int ch;
    
    MCP3008_input_channels(int chNum)
    {
      this.ch = chNum;
    }
    
    public int ch() { return this.ch; }
  }
  
  private static int ADC_CHANNEL0 = MCP3008_input_channels.CH0.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL1 = MCP3008_input_channels.CH1.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL2 = MCP3008_input_channels.CH2.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL3 = MCP3008_input_channels.CH3.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL4 = MCP3008_input_channels.CH4.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL5 = MCP3008_input_channels.CH5.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL6 = MCP3008_input_channels.CH6.ch(); // Between 0 and 7, 8 channels on the MCP3008
  private static int ADC_CHANNEL7 = MCP3008_input_channels.CH7.ch(); // Between 0 and 7, 8 channels on the MCP3008
  
  private static GpioPinDigitalInput  misoInput        = null;
  private static GpioPinDigitalOutput mosiOutput       = null;
  private static GpioPinDigitalOutput clockOutput      = null;
  private static GpioPinDigitalOutput chipSelectOutput = null;
  
  private static boolean go = true;
  
  public static void main()
  {
        System.out.println("Inicio");
    GpioController gpio = GpioFactory.getInstance();
    mosiOutput       = gpio.provisionDigitalOutputPin(spiMosi, "MOSI", PinState.LOW);
    clockOutput      = gpio.provisionDigitalOutputPin(spiClk,  "CLK",  PinState.LOW);
    chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs,   "CS",   PinState.LOW);
    
    misoInput        = gpio.provisionDigitalInputPin(spiMiso, "MISO");
    
    Runtime.getRuntime().addShutdownHook(new Thread()
                                         {
                                           public void run()
                                           {
                                             System.out.println("Shutting down.");
                                             go = false;
                                            // go = true;
                                           }
                                         });
    int lastRead  = 0;
    int tolerance = 5;
    while (go)
    {
        //System.out.println("Leyendo Valor analogo");
      boolean trimPotChanged = false;
      Variables.adc = readAdc(ADC_CHANNEL0);
      Variables.adc1 = readAdc(ADC_CHANNEL1);
      Variables.adc2 = readAdc(ADC_CHANNEL2);
      Variables.adc3 = readAdc(ADC_CHANNEL3);
      Variables.adc4 = readAdc(ADC_CHANNEL4);
      Variables.adc5 = readAdc(ADC_CHANNEL5);
      Variables.adc6 = readAdc(ADC_CHANNEL6);
      Variables.adc7 = readAdc(ADC_CHANNEL7);
      
        if(Variables.adc>512)
            System.out.println("ADC=0: "+Variables.adc);
        if(Variables.adc1==1023)
            System.out.println("ADC=1: "+Variables.adc1);
        if(Variables.adc2==1023)
            System.out.println("ADC=2: "+Variables.adc2);
        if(Variables.adc3==1023)
            System.out.println("ADC=3: "+Variables.adc3);
        if(Variables.adc7==1023)
            System.out.println("ADC=7: "+Variables.adc7);
      int postAdjust = Math.abs(Variables.adc - lastRead);
      if (postAdjust > tolerance)
      {
        trimPotChanged = true;
        int volume = (int)(Variables.adc / 10.23); // [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
        if (DEBUG)
          System.out.println("readAdc:" + Integer.toString(Variables.adc) + 
                                          " (0x" + lpad(Integer.toString(Variables.adc, 16).toUpperCase(), "0", 2) + 
                                          ", 0&" + lpad(Integer.toString(Variables.adc, 2), "0", 8) + ")");        
        System.out.println("Volume:" + volume + "% (" + Variables.adc + ")");
        lastRead = Variables.adc;
      }
      try { Thread.sleep(100L); } catch (InterruptedException ie) { ie.printStackTrace(); }
    }
    System.out.println("Bye...");
    gpio.shutdown();
  }   
  
  private static int readAdc(int Channel)
  {
    chipSelectOutput.high();
    
    clockOutput.low();
    chipSelectOutput.low();
  
    int adccommand = Channel;
    adccommand |= 0x18; // 0x18: 00011000
    adccommand <<= 3;
    // Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
    for (int i=0; i<5; i++) //
    {
      if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
        mosiOutput.high();
      else
        mosiOutput.low();
      adccommand <<= 1;      
      clockOutput.high();
      clockOutput.low();      
    }

    int adcOut = 0;
    for (int i=0; i<12; i++) // Read in one empty bit, one null bit and 10 ADC bits
    {
      clockOutput.high();
      clockOutput.low();      
      adcOut <<= 1;

      if (misoInput.isHigh())
      {
//      System.out.println("    " + misoInput.getName() + " is high (i:" + i + ")");
        // Shift one bit on the adcOut
        adcOut |= 0x1;
      }
      if (DISPLAY_DIGIT)
        System.out.println("ADCOUT: 0x" + Integer.toString(adcOut, 16).toUpperCase() + 
                                 ", 0&" + Integer.toString(adcOut, 2).toUpperCase());
    }
    chipSelectOutput.high();

    adcOut >>= 1; // Drop first bit
    return adcOut;
  }
  
  private static String lpad(String str, String with, int len)
  {
    String s = str;
    while (s.length() < len)
      s = with + s;
    return s;
  }
}


