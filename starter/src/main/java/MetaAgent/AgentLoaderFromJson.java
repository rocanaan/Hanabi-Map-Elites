package MetaAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

import Evolution.Rulebase;

public class AgentLoaderFromJson {
	


	//TODO: Extract into a class that gets the integers from file, then one that builds the agents. See chromosome similarity analyzer
	public static ArrayList<Agent> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
		//fileName = System.getProperty("user.dir")+File.separator+fileName;
	    String fileNameN;
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        ArrayList<Agent> agents = new ArrayList<Agent>();
        
        JSONParser parser = new JSONParser();
        int c = sizeDim1*sizeDim2;
        for (int i = 0; i < c; i++) {
            try {
                fileNameN = fileName +"_"+(i+1)+".json";
                FileReader f = new FileReader(fileNameN);
                Object obj = parser.parse(f);
     
                // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
                JSONObject jsonObject = (JSONObject) obj;

                JSONObject jsonPopulation = (JSONObject)jsonObject.get("population");
                String arr = (String)jsonPopulation.get("Chromosome");
                //System.out.println("1:"+jsonObject);
                //System.out.println("2:"+jsonPopulation);
                //System.out.println("3:"+arr);
                //String[] items = arr.split(", ");
                String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                
                int[] intArr = new int[items.length];

                for (int l = 0; l < items.length; l++) {
                    intArr[l] = Integer.parseInt(items[l]);
                }
            
                agents.add(rb.makeAgent(intArr));
                f.close();
                
            }catch (Exception e) {
                    System.out.println("?");
                    System.err.println(e);;
            }finally {
                
            }
        }
        
        
      
//        return agents;
//        ArrayList<HistogramAgent> agents = makeAgentsFromFile(fileName, rulebaseStandard);
		if(agents !=null) {
			//System.out.println("not null" + agents.size());
		}
		else {
			//System.out.println("null");
		}
		
//		Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
//		for (int i = 0; i<sizeDim1; i++) {
//			for (int j = 0; j<sizeDim2; j++) {
//				
//				HistogramAgent ha = agents.get(j + sizeDim1*i);
//				agentPlayers.add(new AgentPlayer("report agent [ " + i + " , " + j + "]", ha));
//			}
//		}
		return agents;
    }
}
