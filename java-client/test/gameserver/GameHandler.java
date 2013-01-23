package gameserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import agent.DarkstarAuthenticationException;
import agent.Player;
import agent.PlayersController;
import agent.TCGAuthenticationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.sgs.client.simple.SimpleClient;
import com.yongboy.socketio.server.IOHandlerAbs;
import com.yongboy.socketio.server.transport.GenericIO;
import com.yongboy.socketio.server.transport.IOClient;

public class GameHandler extends IOHandlerAbs {
    private static GameHandler instance; 

    private GameHandler(){
    }



    public static GameHandler getInstance(){
        if (instance == null)
            instance = new GameHandler();

        return instance;
    }
		
	@Override
	public void OnConnect(IOClient client) {
		System.out.println("A user connected :: " + client.getSessionID());
	}

	/**
	 * Disconnects the IOClients (Socket.IO clients)
	 */
	@Override
	public void OnDisconnect(IOClient client) {
		System.out.println("A user disconnected. ");
		
		GenericIO genericIO = (GenericIO) client;
		Object userIdObj = genericIO.attr.get("userId");

		if (userIdObj == null)
			return;
		String userId = userIdObj.toString();
		PlayersController.removeSocketIOClient(client.getSessionID());
		
		
		//PlayersController.getPlayerClient(userId).logout(true);
		//PlayersController.removePlayer(userId);
		//disconnect client from PlayersMap
		//PlayersController.broadcastLoggedPlayers();
	}

	
	public void broadcast(IOClient client, String message){
		super.broadcast(client, message);
	}
	
	
	@Override
	public void OnMessage(IOClient client, String oriMessage) {
		String jsonString = oriMessage.substring(oriMessage.indexOf('{'));
		jsonString = jsonString.replaceAll("\\\\", "");
		JSONObject jsonObject = JSON.parseObject(jsonString);
		String eventName = jsonObject.get("name").toString();
		
		if(!eventName.equals("ping")){
				System.out.println("Got a message: " + oriMessage);
		}
		
		
		if (eventName.equals("establishConnection")) {
			JSONArray argsArray = jsonObject.getJSONArray("args");
			String userId = argsArray.getString(0);
			String password = argsArray.getString(1);
			GenericIO genericIO = (GenericIO) client;
			genericIO.attr.put("userId", userId); 
			boolean logged = PlayersController.login(userId, password); 
			handleAckNoticName(client, oriMessage, logged);
			if(logged){
//    			PlayersController.broadcastLoggedPlayers();
    			PlayersController.getPlayer(userId).addSocketIOClient(client);
			}
			
		}
		else if (eventName.equals("createRoom")){
			GenericIO genericIO = (GenericIO) client;
			String userId = genericIO.attr.get("userId").toString();
			JSONArray argsArray = jsonObject.getJSONArray("args");
			String roomName = argsArray.getString(0);
			PlayersController.createRoom(userId, roomName, "");
		}
		else if (eventName.equals("chatMessage")) {
			GenericIO genericIO = (GenericIO) client;
			String userId = genericIO.attr.get("userId").toString();
			JSONArray argsArray = jsonObject.getJSONArray("args");
			String message = argsArray.getString(0);
			
			JSONObject parameters = new JSONObject();
			parameters.put("receiver", "");
			parameters.put("msg", message);
			parameters.put("talker", PlayersController.getPlayer(userId).getUserName());
			parameters.put("talkerId", userId);
			
			JSONObject json = new JSONObject();			
			json.put("cmd", "CHAT");
			json.put("scene", "ALL_SCENE");
			json.put("parameters", parameters);
			
			//emit("chatMessage", nickName, message); Echoed from Darkstar server, in the method handleReceivedLobbyChannelMessages.
			PlayersController.sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId), json.toString());
		} 
		else if (eventName.equals("logout")) {
			JSONArray argsArray = jsonObject.getJSONArray("args");
			String userId= argsArray.getString(0);
			boolean loggedOut=false;
			PlayersController.removePlayer(userId);
			loggedOut = true;
			PlayersController.broadcastLoggedPlayers();
		}
	}

	private void handleAckNoticName(IOClient client, String oriMessage,
			Object obj) {
		boolean aplus = oriMessage.matches("\\d:\\d{1,}\\+:.*:.*?");
		if (aplus) {
			String aPlusStr = oriMessage.substring(2,
					oriMessage.indexOf('+') + 1);
			ackNotify(client, aPlusStr, obj);
		}
	}

	@Override
	public void OnShutdown() {
		System.out.println("shutdown now ~~~");
	}

	public void emit(String eventName, Set<String> usersNames) {
		String content = String.format("{\"name\":\"%s\",\"args\":[%s]}",
				eventName, JSON.toJSONString(usersNames));
		System.out.println("Broadcasting: " + content);
		broadcast(content);
	}

//	public static void emit(String eventName, String... array) {
//		String args = "";
//		String sep = "";
//		for (String s: array) {
//			args += sep + "\"" + s.toString() + "\"";
//			sep = ",";
//		}
//		
//		String content = String.format("{\"name\":\"%s\",\"args\":[%s]}",eventName, args);
//		System.out.println("Broadcasting: " + content);
////		super.broadcast(content);
//		getInstance().broadcast(content);
//	}
//
//	private void emit(IOClient client, String eventName, String message,
//			String message2) {
//		String content = String.format(
//				"{\"name\":\"%s\",\"args\":[\"%s\",\"%s\"]}", eventName,message, message2);
//		System.out.println("Broadcasting: " + client + " "  + content);
//		super.broadcast(client, content);
//	}

}