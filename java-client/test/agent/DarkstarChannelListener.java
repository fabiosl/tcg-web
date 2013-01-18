package agent;
import gameserver.GameHandler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;

/**
 * A simple channel listener to be used with the Darkstar Server
 * 
 */
public class DarkstarChannelListener implements ClientChannelListener {
	/** records the number of channels joined when the client joins the channel */
	private final int channelNumber;
	/** sequence generator for counting channels. */
	private final AtomicInteger channelNumberSequence = new AtomicInteger(1);
	private static final String MESSAGE_CHARSET = "UTF-8";

	private String userId;
	
	
	/**
	 * Inherited from ClientChannelListener. Uses channelNumber to keep a record
	 * of the number of channels joined
	 * 
	 * 
	 * Note that the listener will be given the channel on its callback methods,
	 * so it does not need to record the channel as state during the join.
	 * @param userId 
	 */
	public DarkstarChannelListener(String userId) {
		channelNumber = channelNumberSequence.getAndIncrement();
		this.userId = userId;
	}

	/**
	 * Inherited from ClientChannelListener. Displays a message when the user
	 * agent leaves the channel.
	 */
	public void leftChannel(ClientChannel channel) {
		System.out.println("Removed from channel " + channel.getName());
	}

	/**
	 * Inherited from ClientChannelListener. Displays a message when the user
	 * agent leaves the channel.
	 */
	public void receivedMessage(ClientChannel channel, ByteBuffer message) {
		String decodedMessage = decodeString(message); 
		System.out.println("DS Server channel sent: [" + channel.getName() + " / " + channelNumber + "] " + decodedMessage);
		
		if(channel.getName().equals("LobbyChannel")){
			handleReceivedLobbyChannelMessages(decodedMessage);	
		}else{
			System.out.println("The received message isn't being handled");
		}
		
	}

	private void handleReceivedLobbyChannelMessages(String decodedMessage) {
		System.out.println("The received message is being handled by handleReceivedLobbyChannelMessages");
		try {
			JSONObject jsonObj = new JSONObject(decodedMessage);
			String cmd = jsonObj.getString("cmd");
			String scene = jsonObj.getString("scene");
			JSONObject parameters = jsonObj.getJSONObject("parameters");
			
			if(cmd.equals("CHAT")){
				String args = formatArgs(parameters.getString("talker"),parameters.getString("msg"));
				String content = String.format("{\"name\":\"%s\",\"args\":[%s]}","chatMessage", args);
				PlayersController.broadcastToPlayerIOClients(PlayersController.getPlayer(this.userId), content);
			}
			
			else if(cmd.equals("ADD_PLAYER_TO_LIST")){
				if(PlayersController.getPlayer(parameters.getString("userId"))== null){
					PlayersController.createPlayerClient(parameters.getString("userId"), "", true);
				}
				PlayersController.broadcastLoggedPlayers();
			}
			else if (cmd.equals("REMOVE_PLAYER_FROM_LIST")){
				PlayersController.removePlayer(parameters.getString("userId"));
				PlayersController.broadcastLoggedPlayers();
			}
			
			else if (cmd.equals("ADD_ROOM_TO_BATTLE_LIST")){
				//TODO: Add room to battle list and notify the socketIO's users
				// {"cmd":"ADD_ROOM_TO_BATTLE_LIST","scene":"LOBBY","parameters":{"needPwd":false,"hostNickName":"Fabio Leal","roomName":"batalha","numOfPlayers":1,"roomSid":313,"isPlaying":false}}
				Room newRoom = new Room(parameters.getInt("roomSid"), parameters.getString("roomName"),"",parameters.getString("hostNickName"),1);
				PlayersController.addRoom(newRoom);
				
				String args = formatArgs(parameters.getString("hostNickName"),parameters.getString("roomName"), ""+parameters.getInt("numOfPlayers"), ""+parameters.getInt("roomSid"), ""+parameters.getBoolean("isPlaying"));
				String content = String.format("{\"name\":\"%s\",\"args\":[%s]}","roomCreation", args);
				PlayersController.broadcastToPlayerIOClients(PlayersController.getPlayer(this.userId), content);
			}
			
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String formatArgs(String... args){
		StringBuffer sb = new StringBuffer();
		String sep = "";
		for (String s: args) {
			sb.append( sep + "\"" + s.toString() + "\"");
			sep = ",";
		}
		return sb.toString();
	}
	
	public String decodeString(ByteBuffer buffer) {
		try {
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			return new String(bytes, MESSAGE_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new Error("Character set " + MESSAGE_CHARSET + " not found",e);
		}
	}

}