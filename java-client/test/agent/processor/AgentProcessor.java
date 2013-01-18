package agent.processor;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.Properties;
import org.json.JSONException;
import org.json.JSONObject;
import agent.DarkstarAuthenticationException;
import agent.Player;
import agent.PlayersController;
import agent.TCGAuthenticationException;
import agent.XMLParser;
import agent.util.Util;

import com.sun.sgs.client.simple.SimpleClient;

/** Singleton Design Pattern was applied to this class.
 * */
public class AgentProcessor {

	public static AgentProcessor getInstance(){
		if (instance == null){
			instance = new AgentProcessor();
		}
		return instance;
	}
	
	private AgentProcessor(){}
	
	private static AgentProcessor instance;
		
	





	


}
