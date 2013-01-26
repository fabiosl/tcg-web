package agent;
import gameserver.GameHandler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.TreeSet;
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
			Set<String> handledMessages = new TreeSet<String>(); 
			handledMessages.add("ADD_PLAYER_TO_LIST");
			handledMessages.add("REMOVE_PLAYER_FROM_LIST");
			handledMessages.add("ADD_ROOM_TO_BATTLE_LIST");
			handledMessages.add("REMOVE_ROOM_FROM_BATTLE_LIST");
			handledMessages.add("CHAT");
			
			if (handledMessages.contains(cmd)){
			    sendToClient(cmd, decodedMessage);
			}
			else{
                System.err.println("this message is not being handled");
            }
			
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void sendToClient(String cmd, String args){
	    String content = String.format("{\"name\":\"%s\",\"args\":%s}",cmd, args);
        PlayersController.getPlayer(this.userId).broadcastToSocketIOClients(content);
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