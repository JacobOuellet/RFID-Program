package csvreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

public class CsvReader 
{

    private String inputFile;
    private static final String path = "C:\\Users\\bruce\\Desktop\\inventory.xls";//path to file
    ArrayList<String> fileArr = new ArrayList<>(); //stores data that was already in the file to  this Array
    ArrayList<String> headArr = new ArrayList<>(); //stores file to "this" header Array
   
  

    public  void setInputFile(String inputFile) 
    {
        this.inputFile = inputFile;
    }

    public void read() throws IOException  
    {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        try 
        {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
             //Headers
             for (int j = 0; j < sheet.getColumns(); j++){
                for (int i = 0; i < 1; i++){
                    Cell cell = sheet.getCell(j, i);
                    headArr.add(cell.getContents());       
                }
             }
            //Rest of the data in file
            Cell cell2; 
            for (int p = 1; p < sheet.getRows(); p++){ //loops through rows ignoring header row.
                for (int k = 0; k < sheet.getColumns(); k++){
                    cell2 = sheet.getCell(k,p);
                    fileArr.add(cell2.getContents());                    
                }
            }

        } 
        catch (BiffException e) 
        {
        }
    }
    public ArrayList<String> readfrom(int j) throws IOException  
    {
        File inputWorkbook = new File(inputFile);
        Workbook w;
        try 
        {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            Cell cell2; 
                for (int k = 0; k < sheet.getColumns(); k++){
                        cell2 = sheet.getCell(k,j);
                        fileArr.add(cell2.getContents());
                           
                }
        } 
        catch (IOException | IndexOutOfBoundsException | BiffException e) 
        {
            
        }
        return fileArr;
    }

    
    public void write(ArrayList<String> newArr) throws WriteException 
    {
        int control = 0;
        int control2 = 0;
        int control3 = 1;
        WritableWorkbook workBook = null;
        try {

            workBook = Workbook.createWorkbook(new File(inputFile));
            // create an Excel sheet
            WritableSheet excelSheet = workBook.createSheet("Sheet 1", 0);
            //This loop sets up the Headers
            for (String object: headArr) {
                Label label = new Label(control, 0,object);
                control++;
                excelSheet.addCell(label);
            }
            //This loop sets up the rest of the data in the file
            fileArr.addAll(newArr); // Adds the new data to the end of the file while keeping the old data.
            for(String object2: fileArr){
               Label label = new Label(control2, control3,object2);
                excelSheet.addCell(label);
               control2++;
               if( control2 == headArr.size()){
                   control3++;
                   control2 = 0;
               }
               
            }

            workBook.write();
        } catch (IOException e) {
        } finally {
            if (workBook != null) {
                try {
                    workBook.close();
                } catch (IOException | WriteException e) {
                }
            }
        }
    }
    public static void main(String[] args) throws IOException, WriteException 
    {
        CsvReader xls = new CsvReader();
        ArrayList<String> newData = new ArrayList<>(); //array to use to pass in new data.

        newData.add("Campbell");
        newData.add("Bruce");
       
        xls.setInputFile(path);
        xls.read();
        xls.write(newData);

    }

        
    }

