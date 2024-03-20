/*
UFR Driver
Author @ Jacob Ouellet
Last edited: 4/26/2018
Version 0.0.1
Purpose: The purpose of this program is to connect the gui built by Zac Everett called infogui and the ufr caller built by Kevin Lukitsch. 
This program is a driver program as it calls methods from both and drives the program forward. It also handles exceptions for the program.
*/
package ufrdriver;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import csvreader.CsvReader;
import java.util.Arrays;
import java.io.IOException;
import jxl.write.WriteException;
import ufrcaller.uFrCaller;
import infogui.TagScreen;
import java.awt.HeadlessException;

public class UFRDriver {

    int succession;
    ufrcaller.uFrCaller toTag = new uFrCaller();

    public static void main(String[] args) {
        // calls the tag screen in info gui as thats the main screen
         java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TagScreen().setVisible(true);
            }
        });
        
    }
   /* public void WriteToTag(String[] txtBoxes){

        
        // loop checks to make sure each txtbox has less then or = to 20 characters and 21 the "notes" block has less then or = to 350 characters and if so it sends it to ufr
        // caller to be writen to the tag and uses countForWrite as the block number too
        try{
            
            for(int countForWrite = 0; countForWrite < (txtBoxes.length - 1); countForWrite++){
            if(txtBoxes[countForWrite].length() <= 20){
               succession = toTag.Write(countForWrite, txtBoxes[countForWrite]);
            }
            else{
                JOptionPane.showMessageDialog(null, "Character input is limited to 20 characters per box.", "InfoBox: " + "Character limit reached!", JOptionPane.INFORMATION_MESSAGE);
                succession = 1;
                break;
            }
        }
            // final check for the "notes" block
            if (txtBoxes[21].length() <=350 & succession == 0){
                succession = toTag.Write(21, txtBoxes[21]);
                if(succession == 0){
                 JOptionPane.showMessageDialog(null, "Success, tag has been writen to.", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);   
                }
                 if ( succession == 1 )
                 JOptionPane.showMessageDialog(null, "Could not write to tag!.", "InfoBox: " + "Check Reader!", JOptionPane.INFORMATION_MESSAGE);
                if ( succession == 2 )
                 JOptionPane.showMessageDialog(null, "Authentication Error: Could not write to tag becuase the tag and reader passwords do not match!", "InfoBox: " + "Authentication Error!", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null, "Character input is limited to 400 characters for the notes box.", "InfoBox: " + "Character limit reached!", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch(HeadlessException e){
                 JOptionPane.showMessageDialog(null, "Could not write to tag!.", "InfoBox: " + "Uknown error!", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }*/
     public void WriteToTag(String[] txtBoxes){

        
        // loop checks to make sure each txtbox has less then or = to 20 characters and 21 the "notes" block has less then or = to 350 characters and if so it sends it to ufr
        // caller to be writen to the tag and uses countForWrite as the block number too
        try{
            for(int test = 0; test < (txtBoxes.length -1); test++){
                if(txtBoxes[test].length() <= 20){
                    
                }
                else {
                    JOptionPane.showMessageDialog(null,"Box Number: " + (test+1) + " Character input is limited to 20 characters per box.", "InfoBox: " + "Character limit reached!", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
                if(txtBoxes[21].length() <= 350){
                    
                }
                else {
                    JOptionPane.showMessageDialog(null, "Character input is limited to 350 characters for the notes box.", "InfoBox: " + "Character limit reached!", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
                if(test == 20){
                    for(int countForWrite = 0; countForWrite < (txtBoxes.length); countForWrite++){
                        succession = toTag.Write(countForWrite, txtBoxes[countForWrite]);
                    }
                    switch(succession){
                        case 0:
                            JOptionPane.showMessageDialog(null, "Success, tag has been writen to.", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 1:
                            JOptionPane.showMessageDialog(null, "Could not write to tag!.", "InfoBox: " + "Check Reader!", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 2:  
                            JOptionPane.showMessageDialog(null, "Authentication Error: Could not write to tag becuase the tag and reader passwords do not match!", "InfoBox: " + "Authentication Error!", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        default: 
                            JOptionPane.showMessageDialog(null, "Could not write to tag!.", "InfoBox: " + "Uknown error!", JOptionPane.INFORMATION_MESSAGE);
                             break;
                    }
                }
            }
        }
        catch(HeadlessException e){
                 JOptionPane.showMessageDialog(null, "Could not write to tag!.", "InfoBox: " + "Uknown error!", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    public void WriteToExcel(String[] txtboxes, String filepath)throws IOException, WriteException{
        try{
            ArrayList<String> dataList = new ArrayList<>(Arrays.asList(txtboxes)); // converts arrayto listarray to pass to write method in csvreader
            csvreader.CsvReader toExcel = new CsvReader();
            // sends file path to csv reader. Note! xls file must be read before it can be written to.
            toExcel.setInputFile(filepath);
         toExcel.read();
         toExcel.write(dataList);
         JOptionPane.showMessageDialog(null, "Success, data has been writen to the excel sheet.", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(HeadlessException | IOException | WriteException e){
             JOptionPane.showMessageDialog(null, "Could not write to excel sheet!.", "InfoBox: " + "Check file path!", JOptionPane.INFORMATION_MESSAGE);
        }
         
    }
    public ArrayList<String> readFromExcel(int row, String filepath)throws IOException, WriteException{
        
        ArrayList<String> dataList = new ArrayList<>();
        csvreader.CsvReader toExcel = new CsvReader();
        
        try{
           toExcel.setInputFile(filepath);
           dataList = toExcel.readfrom(row);
        }
        catch(Exception e){
             JOptionPane.showMessageDialog(null, "Could not read from excel sheet. Check to make sure a xsl file is selected!.", "InfoBox: " + "Check file path!", JOptionPane.INFORMATION_MESSAGE);
        }
        return dataList;
    }
    public void clearTag(){
        try{
            // builds a array with "" and passes it to the writer function in ufr caller
             String blank = "";
            for(int count = 0; count < 22; count++){
            succession = toTag.Write(count, blank);
            }
             if(succession == 0){
              JOptionPane.showMessageDialog(null, "Success, tag has been cleared, ", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
            }
             if ( succession == 1 )
                JOptionPane.showMessageDialog(null, "Could not clear tag!.", "InfoBox: " + "Check Reader!", JOptionPane.INFORMATION_MESSAGE);
             if ( succession == 2 )
                JOptionPane.showMessageDialog(null, "Authentication Error: Could not clear tag becuase the tag and reader passwords do not match!", "InfoBox: " + "Authentication Error!", JOptionPane.INFORMATION_MESSAGE);
            
            
        }
        catch(HeadlessException e){
                JOptionPane.showMessageDialog(null, "Could not clear tag!.", "InfoBox: " + "Unknown error!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public String[] ReadTag(){
        // fills the readData with the data that is returned from each pass of the read method in ufrcaller. The loop occurs 21 times which represents the 22 blocks of data stored on the tag
        String[] readData = new String[22];   
        try{
;
            for(int count=0; count < 22; count++){
                readData[count] = toTag.Read(count).trim();
           }  
            return readData;
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Could not read tag!.", "InfoBox: " + "Unknown error!", JOptionPane.INFORMATION_MESSAGE);
    }
        return null;
    }
    public String getCardInfo(){
        try{
            // gets card uid in the ufr caller by calling tagid method 
            String cardInfo;

            cardInfo = toTag.tagID();

            return cardInfo;
        }
        catch(Exception e){
            
        }
        return null;
    }
    
    public void SetReaderKeys(int[] keys){
        try{
                JOptionPane.showMessageDialog(null, "Tag password meets the criteria.", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
                succession = toTag.changeReaderKey(keys); 
                if(succession == 0){
                    JOptionPane.showMessageDialog(null, "Success, tag password has been set, ", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
                }
                if ( succession == 1 )
                JOptionPane.showMessageDialog(null, "Could not change tag password!.", "InfoBox: " + "Check Reader!", JOptionPane.INFORMATION_MESSAGE);
                if ( succession == 2 )
                JOptionPane.showMessageDialog(null, "Authentication Error: Could not change tag password because current reader password does not match current tag password!.", "InfoBox: " + "Authentication Error!", JOptionPane.INFORMATION_MESSAGE);      
        }
        catch(HeadlessException e){

                 JOptionPane.showMessageDialog(null, "Could not change tag password!.", "InfoBox: " + "Unknown error!", JOptionPane.INFORMATION_MESSAGE);

        }
    }
    public void SetTagKeys(int[] keys){
        try{
           
                JOptionPane.showMessageDialog(null, "Tag password meets the criteria.", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
                succession = toTag.changeTagKey(keys); 
                if(succession == 0){
                    JOptionPane.showMessageDialog(null, "Success, tag password has been set, ", "InfoBox: " + "Success!", JOptionPane.INFORMATION_MESSAGE);
                }
                if ( succession == 1 )
                JOptionPane.showMessageDialog(null, "Could not change tag password!.", "InfoBox: " + "Check Reader!", JOptionPane.INFORMATION_MESSAGE);
                if ( succession == 2 )
                JOptionPane.showMessageDialog(null, "Authentication Error: Could not change tag password because current reader password does not match current tag password!.", "InfoBox: " + "Authentication Error!", JOptionPane.INFORMATION_MESSAGE);      
        }
        catch(HeadlessException e){

                 JOptionPane.showMessageDialog(null, "Could not change tag password!.", "InfoBox: " + "Unknown error!", JOptionPane.INFORMATION_MESSAGE);

        }
    }
}
