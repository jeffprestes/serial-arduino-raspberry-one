/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paypal.developer.jeffprestes.serialtalkrxtx;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * Application test to read and write data to an Arduino Device
 * @author jprestes
 */
public class SerialTest implements SerialPortEventListener {

    SerialPort serialPort;
    
    private static final String PORT_NAMES[] = {
        "/dev/tty.usbmodem1411", //MacOS
        "/dev/ttyUSB0", // Linux
        "COM3", // Windows
    };
    
    /** Buffered input stream from the port */
    private InputStream input;
    
    /** The output stream to the port */
    private OutputStream output;
    
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 2000;
    
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;
    
    
    public void initialize()    {
        
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        // iterate through, looking for the port
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }

        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    
    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    
    
    
    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent spe) {

        if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE)   {
            try     {
                int available = input.available();
                byte chunk[] = new byte[available];
                input.read(chunk, 0, available);
                
                System.out.println(new String(chunk));
                
            }   catch (Exception ex)     {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    
    public static void main (String[] args)     {
        SerialTest sTest = new SerialTest();
        sTest.initialize();
        System.out.println("Started...");
    }
    
}
