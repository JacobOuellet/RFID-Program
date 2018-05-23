/*
Kevin Lukitsch
testing.java
4/13/2018
A class created for basic reading and writing testing
 */
package ufrcaller;
import com.sun.jna.Native;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import static ufrcaller.UfrCoder.GetLibFullPath;
import ufrcaller.UfrCoder.uFrFunctions;
import java.io.*;

public class testing {
    //variable for calling uFrFunctions
    uFrFunctions ufr;
    
    //this is a library of all byte information for keys and tags - "stolen"
    final static byte DL_MIFARE_ULTRALIGHT = 0x01,
            DL_MIFARE_ULTRALIGHT_EV1_11 = 0x02,
            DL_MIFARE_ULTRALIGHT_EV1_21 = 0x03,
            DL_MIFARE_ULTRALIGHT_C = 0x04,
            DL_NTAG_203 = 0x05,
            DL_NTAG_210 = 0x06,
            DL_NTAG_212 = 0x07,
            DL_NTAG_213 = 0x08,
            DL_NTAG_215 = 0x09,
            DL_NTAG_216 = 0x0A,
            DL_MIFARE_MINI = 0x20,
            DL_MIFARE_CLASSIC_1K = 0x21,
            DL_MIFARE_CLASSIC_4K = 0x22,
            DL_MIFARE_PLUS_S_2K = 0x23,
            DL_MIFARE_PLUS_S_4K = 0x24,
            DL_MIFARE_PLUS_X_2K = 0x25,
            DL_MIFARE_PLUS_X_4K = 0x26,
            DL_MIFARE_DESFIRE = 0x27,
            DL_MIFARE_DESFIRE_EV1_2K = 0x28,
            DL_MIFARE_DESFIRE_EV1_4K = 0x29,
            DL_MIFARE_DESFIRE_EV1_8K = 0x2A;
    
    final static byte MIFARE_AUTHENT1A = 0x60,
            MIFARE_AUTHENT1B = 0x61,
            KEY_INDEX = 0,
            MAX_BLOCK = 16;
    
    final static byte MAX_SECTORS_1k = 16,
            MAX_SECTORS_4k = 40,
            MAX_BYTES_ULTRALIGHT = 48;
    
    final static short MAX_BYTES_ULTRALIGHT_C = 144,
            MAX_BYTES_NTAG_203 = 144,
            MAX_BYTES_CLASSIC_1K = 752,
            MAX_BYTES_CLASSIC_4k = 3440,
            MAX_BYTES_TOTAL_NTAG_203 = 168,
            MAX_BYTES_TOTAL_ULTRAL_C = 168, //Mifare Ultralight C
            MAX_BYTES_TOTAL_ULTRALIGHT = 64; // 72?
    
    final static int FUNCT_LIGHT_OK = 4,
            FUNCT_SOUND_OK = 0,//4 Tripple sound
            FUNCT_LIGHT_ERROR = 2,
            FUNCT_SOUND_ERROR = 0,//2 Long sound
            DL_OK = 0,
            SLEEP_VALUE = 500;
    
    public void run(){
        System.out.println("TESTING----------------------------");
      
        try{//this is a basic setup of the reader and libraries
            ufr = (uFrFunctions) Native.loadLibrary(GetLibFullPath(false), uFrFunctions.class);
        }catch( Exception e ){
            System.out.println( "Library missing" );
        }
        
        int RConnection = 0;
        boolean connected = false;
        int datacounter = 0;
        
        while(true){//create a loop to constantly check for cards and connection of reader
            try{
                RConnection = ufr.ReaderOpen();//this will check if the reader is connected
                if ( RConnection == 0 ){//readerOpen returning 0 means reader is present
                    if ( !connected )//print when reader connected
                        System.out.println("Reader connected--------------------");
                    connected = true;
                    
                    ByteByReference card = new ByteByReference();
                    ufr.GetDlogicCardType(card);
                    byte[] data = new byte[MaxBytes(card.getValue())];//card data will be read to this variable, i think???
                    ShortByReference success = new ShortByReference();//this returns whether reading was successfull, i think???
                    
                    if ( ufr.LinearRead(data, 0, data.length, success, MIFARE_AUTHENT1A, KEY_INDEX) == DL_OK ){
                        //when linearRead returns 0 then reading has been successful
                        System.out.println( new String(data) );//print data on card
                        ufr.ReaderUISignal(FUNCT_LIGHT_OK, FUNCT_SOUND_OK);//light and sound - needs testing
                        
                        data = ("This card has been tested, test count: " + datacounter).getBytes();//convert message to bytes
                        if ( ufr.LinearWrite(data, data.length, data.length, success, MIFARE_AUTHENT1A, KEY_INDEX) == DL_OK ){
                            //when linearWrite returns 0 then writing has been successful
                            datacounter++;
                            System.out.println( "new data written" );//notify that new data has been written
                            ufr.ReaderUISignal(FUNCT_LIGHT_OK, FUNCT_SOUND_OK);
                        }
                    }
                }
                else{//readerOpen returning anything but 0 means reader is missing
                    if ( connected )//print when reader disconnected
                        System.out.println("No reader connected-----------------");
                    connected = false;
                }
            }catch(Exception e){
                System.out.println("yall fucked up");
                break;
            }
        }
        ufr.ReaderClose();
    }
    
    private int MaxBytes(byte bCardType){//finds max byte lengths for different card types
        short usMaxBytes = 0;
        switch (bCardType) {
            case DL_NTAG_203:
                usMaxBytes = MAX_BYTES_NTAG_203;
                break;
            case DL_MIFARE_ULTRALIGHT:
                usMaxBytes = MAX_BYTES_ULTRALIGHT;
                break;
            case DL_MIFARE_ULTRALIGHT_C:
                usMaxBytes = MAX_BYTES_ULTRALIGHT_C;
                break;
            case DL_MIFARE_CLASSIC_1K:
                usMaxBytes = MAX_BYTES_CLASSIC_1K;
                break;
            case DL_MIFARE_CLASSIC_4K:
            case DL_MIFARE_PLUS_S_4K:
                usMaxBytes = MAX_BYTES_CLASSIC_4k;
                break;
        }
        return usMaxBytes;
    }
}
