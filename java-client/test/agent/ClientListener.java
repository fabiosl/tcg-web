package agent;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import agent.processor.DarkstarProcessor;
import agent.util.Util;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClientListener;


public class ClientListener implements SimpleClientListener {
	private String userId, password;
	
	public ClientListener(String userId, String password){
		this.userId = userId;
		this.password = password;
	}
	
	@Override
	  public ClientChannelListener joinedChannel(ClientChannel channel) {
        String channelName = channel.getName();											// get channel name and create string
        PlayersController.getChannelMap().put(channelName, channel);						// put channel name and channel in the map
        System.out.println(("Joined to channel " + channelName));
        return new DarkstarChannelListener(userId);										// return channel listener object
    }


	@Override
	public void receivedMessage(ByteBuffer message) {
		try {
			String decodedMessage = Util.decodeString(message);								
			System.out.println("DS Server session sent: " + decodedMessage);			
			JSONObject jsonObj;
			jsonObj = new JSONObject(decodedMessage);
			jsonObj.append("userId", this.userId);
			DarkstarProcessor.getInstance().processCommand(jsonObj);						
			//		sendToAgent(decodedMessage); TODO	
		}catch (JSONException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void reconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reconnecting() {
		// TODO Auto-generated method stub

	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		 return new PasswordAuthentication(userId, password.toCharArray());
	}

	@Override
	public void loggedIn() {
		System.out.println("WOHOOO, I've logged in!");

	}

	/**
     * Inherited from SimpleClientListener.  Indicates that user agent is
     * not logged in 
     */
    public void loginFailed(String reason) {
        System.err.println("Login failed: " + reason);
    }

    /**
	 * Inherited from ServerSessionListener.  Disconnects the bridge
	 * from the server
	 * 
	 * @param graceful	determines if disconnect should be graceful or not
	 * @param reason	provides a string with the reason for the disconnect
     */
    public void disconnected(boolean graceful, String reason) {
        System.err.println(("Disconnected: " + reason));
    }
    

}
