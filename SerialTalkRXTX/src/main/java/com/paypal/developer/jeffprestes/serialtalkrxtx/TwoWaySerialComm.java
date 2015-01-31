/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paypal.developer.jeffprestes.serialtalkrxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import static javax.management.Query.gt;
 
public class TwoWaySerialComm {
    
    private static final String PORT_NAMES[] = {
        "/dev/tty.usbmodem1411", //MacOS
        "/dev/ttyUSB0", // Linux
        "/dev/ttyACM0", //RaspberryPi
        "COM3", // Windows
    };
 
    public void connect( ) throws Exception {
    
        System.out.println("Initialing...");
        System.out.println("Scanning ports...");
        
        CommPortIdentifier portIdentifier = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        // iterate through, looking for the port
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portNameTest : PORT_NAMES) {
                if (currPortId.getName().equals(portNameTest)) {
                    portIdentifier = currPortId;
                    System.out.println("Port found: " + portIdentifier.getName());
                    break;
                }
            }
        }
        
        if( portIdentifier.isCurrentlyOwned() ) {
            System.out.println( "Error: Port is currently in use" );
        
        } else {
            int timeout = 2000;
            CommPort commPort = portIdentifier.open( this.getClass().getName(), timeout );
 
            if( commPort instanceof SerialPort ) {
                SerialPort serialPort = ( SerialPort )commPort;
                serialPort.setSerialPortParams( 9600,
                                        SerialPort.DATABITS_8,
                                        SerialPort.STOPBITS_1,
                                        SerialPort.PARITY_NONE );
 
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
 
                ( new Thread( new SerialReader( in ) ) ).start();
                ( new Thread( new SerialWriter( out ) ) ).start();
 
            } else {
                System.out.println( "Error: Only serial ports are handled by this example." );
            }
        }
    }
 
    public static class SerialReader implements Runnable {
 
        InputStream in;
 
        public SerialReader( InputStream in ) {
            this.in = in;
        }
 
        public void run() {
            byte[] buffer = new byte[ 1024 ];
            int len = -1;
            try {
                while( ( len = this.in.read( buffer ) ) > -1 ) {
                    System.out.print( new String( buffer, 0, len ) );
                }
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
 
  public static class SerialWriter implements Runnable {
 
    OutputStream out;
 
    public SerialWriter( OutputStream out ) {
      this.out = out;
    }
 
    public void run() {
      try {
        int c = 0;
        while( ( c = System.in.read() ) > -1 ) {
          this.out.write( c );
        }
      } catch( IOException e ) {
        e.printStackTrace();
      }
    }
  }
 
  public static void main( String[] args ) {
    try {
      ( new TwoWaySerialComm() ).connect( );
    } catch( Exception e ) {
      e.printStackTrace();
    }
  }
}