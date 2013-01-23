package agent.processor;

import java.util.Set;
import java.util.TreeSet;

import gameserver.GameHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import agent.PlayersController;
import agent.util.Util;

/*This project needs a MAJOR refactoring. The Java Client was developed by someone else and I'm currently refactoring almost everything.*/

public class DarkstarProcessor {
	private static DarkstarProcessor instance;
	private DarkstarProcessor(){
	}
	public static DarkstarProcessor getInstance(){
		if(instance == null){
			instance = new DarkstarProcessor();
		}
		return instance;
	}
	
	
    private void requestPlayerData(String userId) {
    	try {																			
			JSONObject jsonObj = new JSONObject();										
			jsonObj.append("cmd", "LOAD_PLAYER_DATA");									
			jsonObj.append("scene", "ALL_SCENE");										
			PlayersController.sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId), jsonObj.toString());
		} catch (Exception e) {															
			e.printStackTrace();														
		}
    }


    public void requestPlayerDecks(String userId) {
    	try {																			
			JSONObject jsonObj = new JSONObject();										
			jsonObj.append("cmd", "LOAD_PLAYER_DECK");									
			jsonObj.append("scene", "ALL_SCENE");										
			PlayersController.sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId), jsonObj.toString());											
		} catch (Exception e) {															
			e.printStackTrace();														
		}
    }
    
//    /**
//     * Requests the player list connected to the game
//     */
//    public void requestPlayerList() {
//    	try {																			// try json object creation and actions
//			JSONObject jsonObj = new JSONObject();										// create new JSON object
//			jsonObj.append("cmd", "LOAD_PLAYER_LIST");									// append cmd value to JSON object
//			jsonObj.append("scene", "ALL_SCENE");										// append scene value to JSON object
//			sendToBridge(jsonObj.toString());											// send JSON encoded string to the Darkstar server
//		} catch (Exception e) {															// catch exceptions
//			e.printStackTrace();														// print stack trace
//		}
//    }
//    /**
//     * Request the room list 
//     */
//    public void requestRoomList() {
//    	try {																			// try json object creation and actions
//			JSONObject jsonObj = new JSONObject();										// create new JSON object
//			jsonObj.append("cmd", "LOAD_ROOM_LIST");									// append cmd value to JSON object
//			jsonObj.append("scene", "LOBBY");										// append scene value to JSON object
//			sendToBridge(jsonObj.toString());											// send JSON encoded string to the Darkstar server
//		} catch (Exception e) {															// catch exceptions
//			e.printStackTrace();														// print stack trace
//		}
//    }
//
//    /**
//     * Request the deck data for the user agent
//     */
//    public void requestPlayerDecks() {
//    	try {																			// try json object creation and actions
//			JSONObject jsonObj = new JSONObject();										// create new JSON object
//			jsonObj.append("cmd", "LOAD_PLAYER_DECK");									// append cmd value to JSON object
//			jsonObj.append("scene", "ALL_SCENE");										// append scene value to JSON object
//			sendToBridge(jsonObj.toString());											// send JSON encoded string to the Darkstar server
//		} catch (Exception e) {															// catch exceptions
//			e.printStackTrace();														// print stack trace
//		}
//    }
	
    private void requestPlayerList(String userId) {
    	try {
			JSONObject jsonObj = new JSONObject();										
			jsonObj.append("cmd", "LOAD_PLAYER_LIST");									
			jsonObj.append("scene", "ALL_SCENE");
			PlayersController.sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId), jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public void loadGotoLobby(JSONObject jsonObj, String userId) {
		requestPlayerData(userId);														
		requestPlayerList(userId);														
		requestRoomList(userId);																
		requestPlayerDecks(userId);															
		requestJoinChannel(userId, "LobbyChannel");
	}
	
	public void loadRoomList(JSONObject jsonObj){
    	try{
    	    JSONObject parameters = jsonObj.getJSONObject("parameters");
    	    JSONArray roomsJsonArray = parameters.getJSONArray("rooms");
    	    
    	    Set<String> result = new TreeSet<String>();
    	    for (int i = 0; i < roomsJsonArray.length(); i++) {
    	        JSONObject roomJson = roomsJsonArray.getJSONObject(i);
    	        result.add(roomJson.toString());
    	    }   
    	    GameHandler.getInstance().emit("updateRooms", result);
    	} catch (JSONException e) {
 	        e.printStackTrace();
    	}
	}
	 
    public void requestJoinChannel(String userId,String channelName) {
    	try {																			
			JSONObject jsonObjNest = new JSONObject();									
			jsonObjNest.append("channelName", channelName);								
    		JSONObject jsonObj = new JSONObject();										
			jsonObj.append("cmd", "JOIN_CHANNEL");										
			jsonObj.append("scene", "ALL_SCENE");										
			jsonObj.append("parameters", jsonObjNest);									
			PlayersController.sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId), jsonObj.toString());
		} catch (Exception e) {															
			e.printStackTrace();														
		}
    }    

	private void requestRoomList(String userId) {
    	try {																			
			JSONObject jsonObj = new JSONObject();										
			jsonObj.append("cmd", "LOAD_ROOM_LIST");									
			jsonObj.append("scene", "LOBBY");										
			PlayersController.sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId), jsonObj.toString());											
		} catch (Exception e) {															
			e.printStackTrace();														
		}
    }
	
	public void processCommand(JSONObject jsonObj) {
		try {
			String cmd = Util.getStringFromJson(jsonObj, "cmd");
			if (("INIT_PLAYER_DATA").equals(cmd)) {		
				String userId = Util.getStringFromJson(jsonObj, "userId");
				JSONObject jsonObjNest = jsonObj.getJSONObject("parameters");
				if (jsonObjNest.getBoolean("success")) {						
					loadGotoLobby(jsonObj,userId);
				}
			} else if (("LOAD_PLAYER_LIST").equals(cmd)) {						
				JSONObject parameters = jsonObj.getJSONObject("parameters");
				JSONArray playersJsonArray = parameters.getJSONArray("playerList");
				Set<String> players = new TreeSet<String>();
				for (int i = 0; i < playersJsonArray.length(); i++) {
				    JSONObject playerJson = playersJsonArray.getJSONObject(i);
				    String playerName = playerJson .getString("nickName");
				    players.add(playerName);
				}
				GameHandler.getInstance().emit("updateLoggedUsers", players);
//				PlayersController.broadcastLoggedPlayers();
				
			}
			else if (("CREATE_ROOM").equals(cmd)) {
				//TODO: Do i need to do something here?
			}
			else if (("LOAD_ROOM_LIST").equals(cmd)) {
			    loadRoomList(jsonObj);
            }
            
			
//			else if (("LOAD_PLAYER_DATA").equals(cmd)) {							// load player data returned from bridge
//				loadPlayerData(jsonObject);											// load player data
//			} else if (("LOAD_ROOM_LIST").equals(cmd)) {							// list of rooms returned from bridge
//				loadRoomList(jsonObject);											// load room list
//			} else if (("RTN_LOAD_DECKS").equals(cmd)) {							// list of decks returned from bridge
//				loadDecksList(jsonObject);											// load list of decks
//				requestPlayerDeckCards(jsonObject); //meu							// request card data for deck
//			} else if (("RTN_LOAD_CARDDECK").equals(cmd)) {							// player deck has been loaded
//				loadDeck(jsonObject);												// load specified deck
//			} else if (("JOIN_CHANNEL").equals(cmd)) {								// join channel returned from bridge
//				loadJoinChannel(jsonObject);										// load join channel
//			} else if (("GOTO_LOBBY").equals(cmd)) {								// return to lobby returned from bridge
//				loadGotoLobby(jsonObject);											// load goto lobby
//			}
		} catch (JSONException e) {e.printStackTrace();}
	}
}
