package SPARTA_Communication;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator; 
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;


public class temp
{ 
	public static void loadBeliefFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        
        try (FileReader reader = new FileReader(filename))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject belief = (JSONObject) obj;
            System.out.println(belief);
            Set keys = 	belief.keySet();
            ;
            //Iterate over employee arra
            Iterator<String> it = keys.iterator();
            while(it.hasNext()){
            	String key = it.next();
               System.out.println(belief.get(key));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
    public static void main(String[] args) throws Exception  
    { 
    	loadBeliefFromFile("Bayestest");
    		
    } 
} 