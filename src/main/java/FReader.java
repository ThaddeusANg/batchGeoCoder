import java.io.*;
import java.util.*;
import javax.swing.*;

class WriteFile{
	String path="";
	boolean appendToFile=false;
	public WriteFile(String name){
		path = name;
	}
	
	public WriteFile(String name, boolean append){
		path = name;
		appendToFile=append;
	}
	
	public void writeToFile(String content) throws IOException{
		FileWriter write = new FileWriter(path, appendToFile);
		PrintWriter print_line = new PrintWriter(write);
		print_line.printf("%s"+"%n", content);
		print_line.close();
	}
}

public class FReader {
  ArrayList<String> content = new ArrayList<String>();;
  
  //filereader takes file, creates arrayList of strings
  public FReader(File path) throws IOException{
    BufferedReader bReader = new BufferedReader(new FileReader(path));
    String line;
    while ((line = bReader.readLine()) != null)
      content.add(line);
    bReader.close();
  }
  
  //returns arraylist of file-row strings
  public ArrayList<String> getContent(){
    return content;
  }
  
  //compares one arraylist against another return arraylist of missing rows
  public ArrayList<String> missing(ArrayList<String> compare){
    ArrayList<String> output = new ArrayList<String>();
    for(String temp: content){
        if(!compare.contains(temp))
          output.add(temp);
      }
    return output;
  }
  
  //compares one arraylist against another, splitting each row on delim, and comparing the two cells given in index
  public ArrayList<String> missing(ArrayList<String> compare, String delim, int index[]){
    //get max index value to create arrays for comparison
    int largest=0;
    for(int big : index){
      if(big>largest)
        largest=big;
    }
    ArrayList<String> output = new ArrayList<String>();
    
    //traverse content ArrayList
    for(String temp: content){
      //split each row on delim value into array, create array up to max size of searched values
      String[] origArray = temp.split(delim);
      String original[] = new String[index.length];
      
      //populate original[] with items we're searching for up to the max cell we're looking for
      if(origArray.length>largest){
        for(int x=0;x<index.length;x++){
          original[x]=origArray[index[x]];
        }
      
      String[] altArray;
      int add=0;      
      
      //traverse alternate array 
      for(String alt : compare){
        add=0;
        //split each row in alternate[] on delim
        altArray=alt.split(delim);
        if(altArray.length>largest)
        {
          for(int x=0;x<original.length;x++){
            //check if cells in each row contain the same values, if so increment a counter
            if(altArray[index[x]].equalsIgnoreCase(original[x]))
              add++;
          }
          // if all checked values are found to match, break out of the loop
          if(add==index.length)
            break;
        }
      }
      //if not all values match, add to ArrayList and return value
      if(add<index.length)
        output.add(temp);
      }	
    }
    return output;
  }
  
    public static void main(String args[]) throws Exception {
    	//----------primary
//      ArrayList<FReader> files = new ArrayList<FReader>();
//       JFileChooser fc = new JFileChooser();
//       fc.setCurrentDirectory(new File("."));
//       
//       for(int x=0;x<2;x++){
//         int r = fc.showOpenDialog(new JFrame());
//         if (r == JFileChooser.APPROVE_OPTION) {
//           File name = fc.getSelectedFile();
//           files.add(new FReader(name));
//         }
//       }
//
//       ArrayList<String> output = files.get(0).missing(files.get(1).getContent());
//      for(String temp: output){
//        System.out.println("row: "+temp);
//      }
       //-----------primary
    	//CustomRoles myRoles = new CustomRoles();
    }
}