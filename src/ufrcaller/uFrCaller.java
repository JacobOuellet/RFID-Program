/*
Kevin Lukitsch
uFrCaller.java
4/18/2018
A class created for reading and writing from a ufr RFID reader
*/
package ufrcaller;
import com.sun.jna.Native;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import javax.swing.JOptionPane;
import static ufrcaller.UfrCoder.GetLibFullPath;
import ufrcaller.UfrCoder.uFrFunctions;

public class uFrCaller {
    UfrCoder.uFrFunctions ufr;
    
    int totalblocks;
    
    //this is a list of all byte information for keys and tags
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
    
    public uFrCaller(){
        //upon creation the uFrCaller class checks for the ufrCoder libraries
        try{//this is a basic setup of the reader and libraries
            ufr = (uFrFunctions) Native.loadLibrary(GetLibFullPath(false), uFrFunctions.class);
        }catch( Exception e ){
            //System.out.println( "Library missing." );
        }
        totalblocks = 22;
    }
    
    public String Read( int block ){
        //A block of data is read from a tag
        if ( Connected() ){
            ByteByReference card = new ByteByReference();
            ufr.GetDlogicCardType(card);
            byte[] data = new byte[MaxBytes(card.getValue())];//card data will be read to this variable
            ShortByReference success = new ShortByReference();//this returns whether reading was successfull
            
            //reading is seperated depending on whether or not the comments section is being read (block 20)
            if ( block < totalblocks-1 ){
                int auth = ufr.LinearRead(data, block*20, 20, success, MIFARE_AUTHENT1A, KEY_INDEX);
                if ( auth == DL_OK ){
                    //when linearRead returns 0 then reading has been successful
                    ufr.ReaderUISignal(FUNCT_LIGHT_OK, FUNCT_SOUND_OK);//light to confirm reading
                    return new String(data);
                }
                else if ( auth == 14 ){//if linear read returns 14 then there is an authentication error
                    ufr.ReaderUISignal(FUNCT_LIGHT_ERROR, FUNCT_SOUND_ERROR);//light to present error
                    return "";
                }
            }
            else{
                int auth = ufr.LinearRead(data, block*20, MaxBytes(card.getValue())-(20*(totalblocks-1)), success, MIFARE_AUTHENT1A, KEY_INDEX);
                if ( auth == DL_OK ){
                    //when linearRead returns 0 then reading has been successful
                    ufr.ReaderUISignal(FUNCT_LIGHT_OK, FUNCT_SOUND_OK);//light to confirm reading
                    return new String(data);
                }
                else if ( auth == 14 ){//if linear read returns 14 then there is an authentication error
                    ufr.ReaderUISignal(FUNCT_LIGHT_ERROR, FUNCT_SOUND_ERROR);//light to present error
                     JOptionPane.showMessageDialog(null, "Authentication error: Could not read tag because the reader and tag passwords do not match!", "InfoBox: " + "Authentication error!", JOptionPane.INFORMATION_MESSAGE);
                    return "";
                   
                }
            }
        }
        else
            ufr.ReaderUISignal(FUNCT_LIGHT_ERROR, FUNCT_SOUND_ERROR);//lightto present error
        
        return "";
    }
    
    public int Write( int block, String message ){
        //A message is written to a given block of data
        //Write returns either 0, 1, or 2
        //0 means writing was successful
        //1 means there was an issue connecting to the reader
        //2 means there was an issue with card authentication
        byte[] data = message.getBytes();
        
        if ( Connected() ){
            ByteByReference card = new ByteByReference();
            ufr.GetDlogicCardType(card);
            ShortByReference success = new ShortByReference();//this returns whether reading was successfull
            
            //These lines will clear out the space that is about to be written to
            if ( block < totalblocks-1 ){
                ufr.LinearWrite(fillSpace(20).getBytes(), block*20, 20, success, MIFARE_AUTHENT1A, KEY_INDEX);
            }
            else{
                ufr.LinearWrite(fillSpace(MaxBytes(card.getValue())-(20*(totalblocks-1))).getBytes(), block*20, MaxBytes(card.getValue())-(20*(totalblocks-1)), success, MIFARE_AUTHENT1A, KEY_INDEX);
            }
                
            //This if statement will write to the block
            int auth = ufr.LinearWrite(data, block*20, data.length, success, MIFARE_AUTHENT1A, KEY_INDEX);
            if ( auth == DL_OK ){
                //when linearWrite returns 0 then writing has been successful
                ufr.ReaderUISignal(FUNCT_LIGHT_OK, FUNCT_SOUND_OK);//light to confirm writing
            }
            if ( auth == 14 ){//if linear read returns 14 then there is an authentication error
                ufr.ReaderUISignal(FUNCT_LIGHT_ERROR, FUNCT_SOUND_ERROR);//light to present error
                return 2;
            }
        }
        else{
            ufr.ReaderUISignal(FUNCT_LIGHT_ERROR, FUNCT_SOUND_ERROR);//light to present error
            return 1;
        }
        
        return 0;
    }
    
    public boolean readerConnected(){
        //readerConnected will return the readers status
        return Connected();
    }
    
    public boolean tagSignal(){
        //returns true if tag signal is found
        //WARNING the signal is finnicky and changes frequently even when a tag is present
        return ( ufr.GetDlogicCardType( new ByteByReference() ) == 0 );
    }
    
    public String tagID(){
        //retrieves the tags ID
        ByteByReference cardType = new ByteByReference();
        byte[] cardId = new byte[9];
        ByteByReference cardIdSize = new ByteByReference();
        String id = "";
        
        //ID is originally given in byte form and must be converted to string
        if (ufr.GetCardIdEx(cardType, cardId, cardIdSize) == DL_OK) {
                int size = cardIdSize.getValue();
                for (byte i = 0; i < size; ++i) {//using cardIdSize all bytes are converted to integers
                    id += Integer.toHexString((((char) cardId[i] & 0xFF)));//integers are compiled into string
            }
                return "0x"+id.toUpperCase().toUpperCase();//return final string
        }
        return "No tag found";
    }
    
    public void blockNumber( int newtotal ){
        //changes the total number of block spaces on the tag
        totalblocks = newtotal;
    }
    
    public int changeTagKey( int[] keys ){
        //takes in a 6 integer password for card formatting
        //returns 0, 1, or 2
        //0 means there were no errors
        //1 means change was unsuccessful - may be due to reader disconnected
        //2 means there was an authentication error
        
        //closing and opeing the reader is largely a cautionary measure
        ufr.ReaderClose();
        ufr.ReaderOpen();
        
        ByteByReference card = new ByteByReference();
        byte[] keyA = new byte[6];
        byte[] keyB = new byte[6];
        //keys are assigned to byte[] arrays
        for ( int i = 0; i < 6; i++ ){
            keyA[i] = (byte)keys[i];
            keyB[i] = (byte)keys[i];
        }
        byte blockAccess = 0;
        byte trailerAccess = 1;
        byte trailerByte9 = 45;
        byte keyIndex = 0;
        
        //linearformatcard will return an integer based on possible errors
        int auth = ufr.LinearFormatCard( keyA, blockAccess, trailerAccess, trailerByte9, keyB, card, MIFARE_AUTHENT1A, keyIndex );
        if ( auth == 0 )//0 means everything is ok
            return 0;
        else if ( auth == 14 )
            return 2;
        else
            return 1;
    }
    
    public int changeReaderKey( int[] keys ){
        //takes in a 6 integer password for reader formatting
        //return 0 or 1
        //0 means there were no errors
        //1 means change was unsuccessful - may be due to reader disconnected
        
        //closing and opeing the reader is largely a cautionary measure
        ufr.ReaderClose();
        ufr.ReaderOpen();
        
        byte[] Rkey = new byte[6];
        //keys are assigned to a byte[] array
        for ( int i = 0; i < 6; i++ )
            Rkey[i] = (byte)keys[i];
        
        byte keyIndex = 0;
        
        //readerkeywrite returns and integer based on possible errors
        int auth = ufr.ReaderKeyWrite( Rkey, keyIndex );
        if ( auth == 0 )//0 means everything is ok
            return 0;
        else
            return 1;
    }
    
    private boolean Connected(){
        //checks connected status of the reader
        return ( ufr.ReaderOpen() == 0 );
    }
    
    private int MaxBytes(byte bCardType){
        //finds max byte lengths for different card types
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
    
    private String fillSpace( int maxbytes ){
        //returns a string of blank spaces for overwriting data blocks
        String s = "";
        for ( int i = 0; i < maxbytes; i++)
            s += " ";
        return s;
    }
}
