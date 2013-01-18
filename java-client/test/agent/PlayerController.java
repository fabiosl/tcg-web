package agent;

import gameserver.GameHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import agent.processor.AgentProcessor;
import agent.util.Util;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.simple.SimpleClient;
import com.yongboy.socketio.server.transport.IOClient;

/**
 * This class should be the controller that will handle the SimpleClients that must be connected to SGS-Server (DarkStar Server). 
 * @author fabiosl
 *
 */
public class PlayerController {
	
	private static Map<Player, SimpleClient> simpleClientMap = new HashMap<Player, SimpleClient>(); //Darkstar Mapping
	private static Map<Player, List<IOClient>> IOClientMap = new HashMap<Player, List<IOClient>>(); //Socket.IO Mapping
	private static Map<String, ClientChannel> channelMap = new HashMap<String, ClientChannel>();
	
	
	public static void debugSimpleClientMap(){
		String result = "simpleClientMap: {";
		for (Player p : simpleClientMap.keySet()) {
			SimpleClient simpleClient= simpleClientMap.get(p);
			String clientString = "flash";
			
			if (simpleClient != null){
				clientString = ""+simpleClient.isConnected();
			}
			
			result += p.getUserId() + ":" + clientString  + "; "; 
		}
		result += "}";
		System.err.println(result);
	}
	
	public static void debugIOClientMap(){
		String result = "ioClientMap: {";
		for (Player p : IOClientMap.keySet()) {
			result += p.getUserId() + ":" + IOClientMap.get(p).size() + " clients; "; 
		}
		result += "}";
		System.err.println(result);
	}
	
	
	
	
	private List<Room> availableRooms = new ArrayList<Room>();
	private AgentProcessor agentProcessor = AgentProcessor.getInstance();
	
	public static void broadcastToPlayerIOClients(Player player, String message){
		List<IOClient> socketIOClients = IOClientMap.get(player);
		for (IOClient ioClient : socketIOClients) {
			GameHandler.getInstance().broadcast(ioClient, message);
			System.out.println("Sent to client: " + message);
		}
	}
	
	public static Map<String, ClientChannel> getChannelMap(){
		return channelMap;
	}
	public static void main(String[] args) throws Exception {
//		PlayerController handler = new PlayerController();
//		handler.login("fabio1", "sousaleal.fabio");
//		Thread.sleep(5000);
//		System.out.println("Number of Logged players: "	+ handler.playersMap.size());
//		handler.login("fabio2", "sousaleal.fabio");
//		Thread.sleep(5000);
//		System.out.println("Number of Logged players: "
//				+ handler.playersMap.size());
	}

	public static Set<Player> getLoggedPlayers(){
		return simpleClientMap.keySet();
	}
	
	public static void addIOClient(Player p, IOClient c){
		IOClientMap.get(p).add(c);
	}
	
	
	public List<Room> getAvailableRooms() {
		return availableRooms;
	}

	public void setAvailableRooms(List<Room> availableRooms) {
		this.availableRooms = availableRooms;
	}

	public static Map<Player, SimpleClient> getPlayersMap() {
		return simpleClientMap;
	}
	
	public static Set<String> getLoggedPlayersNames(){
		Set<String> names = new TreeSet<String>();
		for (Player player : getPlayersMap().keySet()) {
			names.add(player.getUserName());
		}
		return names;
	}
	
	public static synchronized void createPlayerClient(String userId, String password, boolean isFlashPlayer){
		if(!simpleClientMap.containsKey(getPlayer(userId))){
			SimpleClient client = null;
			if (!isFlashPlayer){
				client = new SimpleClient(new ClientListener(userId, password));
			}
			simpleClientMap.put(new Player(userId, password,isFlashPlayer), client);
			
			Player player = getPlayer(userId);
			if(! IOClientMap.keySet().contains(player)){
				IOClientMap.put(player, new ArrayList<IOClient>()); // iOClients will be added later	
			}
		}else{
			System.err.println("Are you trying to create a player that already exists?");
		}
	}
	

	public static Player getPlayer(String userId){
		for (Player player : simpleClientMap.keySet()) {
			if (player.getUserId().equals(userId)){
				return player;
			}
		}
		return null;
	}
	
	public static SimpleClient getPlayerClient(String userId){
		for (Player player : simpleClientMap.keySet()) {
			if (player.getUserId().equals(userId)){
				return simpleClientMap.get(player);
			}
		}
		return null;
	}
	
	
	public static boolean loginTCG(String userId, String password) {
		boolean loginSuccessful = false;
		CharSequence charSequence = "loginOK=true";
		String tempString = "";
		BufferedReader in;
		try {
			URLConnection urlConnection = new URL(
					"http://tcg.dyndns.info:8081/mcslcard/JSP/Login_flash.jsp?acc="
							+ userId + "&pwd=" + password).openConnection();
			in = new BufferedReader(new InputStreamReader(
					(InputStream) urlConnection.getContent()));
			while ((tempString = in.readLine()) != null) {
				if (tempString.contains(charSequence)) {
					loginSuccessful = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return loginSuccessful;
	}
	
	
	private static boolean loginDarkstar(String userId, String password) {
		String host = "tcg.dyndns.info";
		String port = "842";
		SimpleClient playerClient = null;
		try {
			Properties connectProps = new Properties();
			connectProps.put("host", host);
			connectProps.put("port", port);
			createPlayerClient(userId,password,false);
			playerClient = getPlayerClient(userId);
			playerClient.login(connectProps);
			Thread.sleep(5000);
			
			if(!playerClient.isConnected()){
				PlayerController.removePlayer(userId);
			}
			
			
		} catch (Exception exception) {
			exception.printStackTrace();
			System.err.println(exception.getMessage());
		}
		return playerClient != null && playerClient.isConnected();
	}
	
	private void processLogin(JSONObject inputJson)
			throws TCGAuthenticationException, DarkstarAuthenticationException {
		try {
			String userId = Util.getStringFromJson(inputJson, "username");
			String password = Util.getStringFromJson(inputJson, "password");
			JSONObject jsonObject = new JSONObject();
			if (loginTCG(userId, password)) {
				if (loginDarkstar(userId, password)) {
					System.out.println("User " + userId + " logged Successfully");
					getPlayerInitData(userId);
					loadLoginSequence(userId);
				} else {
					jsonObject.put("cmd", "LOGIN_FAILURE_DS");
					jsonObject.put("username", userId);
					System.err.println("Login to Darkstar failed");
					throw new DarkstarAuthenticationException();
				}
			} else {
				jsonObject.put("cmd", "LOGIN_FAILURE_TCG");
				jsonObject.put("username", userId);
				// darkstarBridge.sendToAgent(jsonObject.toString());// TODO
				System.err.println("Login to TCG failed");
				throw new TCGAuthenticationException();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	

	private static void loadLoginSequence(String userId) {
		URLConnection urlConnection; 
		CharSequence charSequence = "loginSeq="; 
		String tempString = ""; 
		BufferedReader in; 
		try { 
			if (PlayerController.getPlayerClient(userId).isConnected()) {
				URL url = new URL("http://tcg.dyndns.info:8081/mcslcard/data/saveLogin.jsp?userId="+ userId);
				urlConnection = url.openConnection(); 
				in = new BufferedReader( new InputStreamReader((InputStream) urlConnection.getContent()));
				while ((tempString = in.readLine()) != null) { 
					if (tempString.contains(charSequence)) { 
						tempString = tempString.substring(charSequence.length(), tempString.length());
					}
					JSONObject jsonObjNest = new JSONObject(); 
					jsonObjNest.put("loginSeq", tempString); 
					JSONObject jsonObj = new JSONObject(); 
					jsonObj.put("cmd", "SET_LOGIN_SEQ"); 
					jsonObj.put("parameters", jsonObjNest);
					jsonObj.put("scene", "ALL_SCENE"); 
					sendSessionMsgToDarkstar(PlayerController.getPlayerClient(userId),jsonObj.toString()); 
				}
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}

	public static void sendSessionMsgToDarkstar(SimpleClient playerClient, String text) {
		try { 
			System.out.println("Sent sess msg to DS server: " + text);
			ByteBuffer message = Util.encodeString(text);
			playerClient.send(message); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	
	
	public static void getPlayerInitData(String userId) {
		URLConnection urlConnection;													
    	try {																			
        	if (PlayerController.getPlayerClient(userId).isConnected()) {											
        		URL url = new URL("http://tcg.dyndns.info:8081/mcslcard/data/initPlayerData.jsp?userId=" + userId);
					
    			urlConnection = url.openConnection();
    			JSONObject jsonObjNest = new XMLParser().parseStream(					
    					(InputStream)urlConnection.getContent());
    			JSONObject jsonObj = new JSONObject();								
    			jsonObj.put("cmd", "INIT_PLAYER_DATA");								
    			jsonObj.put("parameters", jsonObjNest);								
    			jsonObj.put("scene", "ALL_SCENE");									
    			sendSessionMsgToDarkstar(PlayerController.getPlayerClient(userId),jsonObj.toString());			
    			//darkstarBridge.sendToAgent(jsonObj.toString());							
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	public static boolean login(String userId, String password) {
		if(getPlayer(userId) !=  null){ // Check if is already logged in and the user client was added to the map
			return loginTCG(userId, password);  // if client  was added to the map, simply return loginTCG
		}
		else{
			boolean logged = loginDarkstar(userId, password);
			System.out.println("User " + userId + " is logged? " + logged);
			if (logged){
				getPlayerInitData(userId);
				loadLoginSequence(userId);
			}
			return logged;
		}
		
	}
    
	
	public static void removePlayer(String userId) {
		Player player  = getPlayer(userId);
		
		SimpleClient simpleClient = simpleClientMap.get(player);
		if (player != null && !player.isFlashClient() && simpleClient.isConnected()){ //If is HTML client
			simpleClient.logout(false);
			simpleClient = null;
		}

		simpleClientMap.remove(player); //Removes HTML or Flash clients
		List<IOClient> socketIOClients = IOClientMap.get(player);		
		
		if(socketIOClients != null && ! socketIOClients.isEmpty()){
			for (IOClient ioClient : socketIOClients) {
				ioClient.disconnect();
				ioClient = null;
			}
		}
		IOClientMap.remove(player);
	}
	
	public static void removeSocketIOClient(String sessionId) {
		for (Player player : IOClientMap.keySet()) {
			if(!player.isFlashClient()){
				List<IOClient> listIOClients = IOClientMap.get(player);
				if(listIOClients!= null && !listIOClients.isEmpty()){
					for (IOClient ioClient : listIOClients) {
						if (ioClient.getSessionID().equals(sessionId)){
							listIOClients.remove(ioClient);
							if(listIOClients.isEmpty()){
								removePlayer(player.getUserId());
							}
						}
					}
				}
			}
		}
	}
	
	public static void broadcastLoggedPlayers(){
		GameHandler.emit("updateLoggedUsers", PlayerController.getLoggedPlayersNames());
	}
	
	
	public static void createRoom(String userId, String roomName, String roomPassword) {
    	try {																		
			JSONObject jsonObj = new JSONObject();
			jsonObj.append("cmd", "CREATE_ROOM");
			jsonObj.append("scene", "LOBBY");
			JSONObject jsonObjNest = new JSONObject();
			jsonObjNest.append("roomName", roomName);
			jsonObjNest.append("roomPwd", roomPassword);
			jsonObj.append("parameters", jsonObjNest);
			PlayerController.sendSessionMsgToDarkstar(getPlayerClient(userId),jsonObj.toString()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	
	
}
