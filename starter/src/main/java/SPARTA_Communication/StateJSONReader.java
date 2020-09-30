package SPARTA_Communication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class StateJSONReader {
	
	private String in_path;
	private String infix;
//	private boolean active; /TODO: Implement threading
	
	public StateJSONReader(String in_path, String infix) {
		super();
		this.in_path = in_path;
		this.infix = infix;
	}
	
	public Map<String, Object> read() {
		String[] pathnames;
		File f = new File(in_path);
		pathnames = f.list();
		
		boolean found = false;
		for (String pathname:pathnames) {
			if (pathname.toLowerCase().contains(infix)){
				
				
		        
				
				/* With ObjectMapper*/

//		        Map<String,Object> result = null;
//		        try {
//					result =
//					        new ObjectMapper().readValue(in_path + File.separator + pathname, HashMap.class);
//				} catch (JsonParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		        
//		        return result;
		         
				
				
				/* With standard JSONParser*/
				
				//JSON parser object to parse read file
		        JSONParser jsonParser = new JSONParser();

				
		        try (FileReader reader = new FileReader(in_path + File.separator + pathname))
		        {
		            //Read JSON file
		            Object obj = jsonParser.parse(reader);
		            System.out.println(obj);
		            Map<String, Object> response = new ObjectMapper().readValue(obj.toString(), HashMap.class);
		            return response;
		            
//		            System.out.println(obj);
//		 
//		            JSONArray hleStateJSON = (JSONArray) obj;
//		            
//		            
//		             
//		            //Iterate over employee array
//		            return hleStateJSON;
		 
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
//				
				
			
				
				
			}
		}
		return null;
			
	}

	
	public static void main( String[] args ) {
		String path = "/Users/rodrigocanaan/Dev/HLE_AIIDE/hanabi-ad-hoc-learning/Experiments/SPARTA_Integration/states";
		String infix = "state";
		
		StateJSONReader reader = new StateJSONReader(path,infix);
		boolean found = false;
		Map<String, Object> hleStateJSON = null;
		while (!found){
			hleStateJSON = reader.read();
			if (hleStateJSON == null){
				System.out.println("Waiting for state");
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				found = true;
			}
		}
		System.out.println(hleStateJSON);
		System.out.println(hleStateJSON.get("discard_pile"));
		System.out.println(hleStateJSON.get("complete_card_knowledge"));
		System.out.println(hleStateJSON.keySet());
	}
	
	
}
