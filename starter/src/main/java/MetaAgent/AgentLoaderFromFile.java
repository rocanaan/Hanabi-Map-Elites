package MetaAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

import Evolution.Rulebase;

public class AgentLoaderFromFile {
	
	public static HashMap<String,Agent> makeAgentMapFromFile(String fileName, boolean rulebaseStandard, boolean ignoreEmpty){
		fileName = System.getProperty("user.dir")+File.separator+fileName;
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        HashMap<String,Agent> agents = new HashMap<String,Agent>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            
            int agentIndex = 0;
            while ((thisLine = br.readLine()) != null) {
                System.out.println(agentIndex);
                //thisLine = thisLine.substring(1, thisLine.length() - 2);
        		thisLine = thisLine.replaceAll(" ", "");
        		thisLine = thisLine.replaceAll("\t", "");

        		String[] c = thisLine.split(",");

        		boolean valid = false;
        		int[] chromosome = new int[c.length];
            	System.out.println(c.length);

                for (int i = 0; i < c.length; i++) {
                    chromosome[i] = Integer.parseInt(c[i]);
                    if (chromosome[i] !=0 && !valid) {
                    	valid = true;
                    	System.out.println("Valid");

                    }
                }
            	System.out.println(valid);

                if (valid || !ignoreEmpty) {
                	System.out.println("Trying to add  agent to map");

                	agents.put(String.valueOf(agentIndex), rb.makeAgent(chromosome));
                	System.out.println("Added agent to map");
                }
                agentIndex++;
            }
        }
        catch (Exception e) {
    		System.err.println(e);;
        }
        return agents;
	}

	//TODO: Extract into a class that gets the integers from file, then one that builds the agents. See chromosome similarity analyzer
	public static ArrayList<Agent> makeAgentsFromFile(String fileName, int sizeDim1, int sizeDim2, boolean rulebaseStandard) {
		fileName = System.getProperty("user.dir")+File.separator+fileName;
        Rulebase rb = new Rulebase(rulebaseStandard);
        String thisLine;
        ArrayList<Agent> agents = new ArrayList<Agent>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((thisLine = br.readLine()) != null) {
                //thisLine = thisLine.substring(1, thisLine.length() - 2);
            		thisLine = thisLine.replaceAll(" ", "");
            		thisLine = thisLine.replaceAll("\t", "");
            		String[] c = thisLine.split(",");
            		int[] chromossome = new int[c.length];
                for (int i = 0; i < c.length; i++) {
                    chromossome[i] = Integer.parseInt(c[i]);
                }
               
//                Rule[] rules1 = new Rule[chromossome.length];
//                for (int i = 0; i < chromossome.length; i++) {
//
//                    if (rulebaseStandard) {
//                        rules1[i] = rb.ruleMapping(chromossome[i]);
//                    } else {
//                        rules1[i] = rb.ruleMapping(chromossome[i]);
//
//                    }
//
//                }
//                agents.add(new HistogramAgent(rb.makeAgent(rules1)));
                agents.add(rb.makeAgent(chromossome));

            }
            br.close();
        } catch (Exception e) {
        		System.err.println(e);;
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
