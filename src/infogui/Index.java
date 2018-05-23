package infogui;

/**
 * Zaccheus Everett
 * Index class
 * Holds the variables used to create the data index.
 * 3/20/18
 * last edited 4/10/18
 * default row length is set from max possible computers in a room(30)
 * multidimensional array that has a the ability to update cell by cell
 */
public class Index { 
    int currentCol = 0;//column that data is going to be written to
    int currentRow = 0;//row that data is going to be written to
    String[][] info; //Array where data is held.
    
    
    public Index(){//default settings used to hold data from the computers
        info=new String[30][22];
    }
    
    public Index(int row, int col){//if size of array needeed to change
        info=new String[row][col];
    }
    
    public String[][] getArray(){return info;}//gives the 
    
    protected void updateIndex(String[] data){//updates the values in the index one row each time it's called
      try{
          for (int i=0; i<info[0].length; i++){
              info[currentRow][i] = data[i];
          }
          currentRow++;
      }  
      catch(Exception outOfRange){
          //error warning out of range of index length
      }
    }
    
     
    protected void updateIndex(String data){//updates the values in the index one column each time it's called
      try{
          info[currentRow][currentCol] = data;
          currentCol++;
          if(currentCol==info[0].length){//at the end of a row move up to the next one
              currentCol = 0;
              currentRow++;
          }
      }  
      catch(Exception outOfRange){
          //error warning out of range of index length
      }
    }
    
    protected void dropRow(){// sets the currnet position of the index down a row
        if (currentRow>info.length){currentRow=info.length;}
        else{currentRow--;}
        currentCol = 0;
    }
    
    
    void fill(){//test to see how array is updated
        for (int i = 0; i < info.length; i++) {
            for (int j = 0; j <info[0].length; j++) {           
                updateIndex(String.valueOf(j));
                System.out.println(info[i][j]);   
            }
        }
    }

}
