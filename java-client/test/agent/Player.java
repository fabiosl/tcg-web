package agent;

import gameserver.GameHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.sun.sgs.client.simple.SimpleClient;
import com.yongboy.socketio.server.transport.IOClient;

/** class */
/**
 * The Player class is used to manage the data specific to the
 * Player.  The UserAgent inherits from the Player class
 * as the UserAgent is a type of player.
 * 
 */

public class Player {
	
	/** variables */
	/** string to store user id */
	private String userId;
	/** string to store user name */
	private String userName;
	/** int to store number of matches */
	private int matches;
	/** int to store number of wins */
	private int wins;
	/** int to store number of losses */
	private int losses;
	/** int to store number of interrupts */
	private int interrupt;
	/** int to store score of player */
	private int score;
	/** int to store rank of player */
	private int rank;
	private String password;
	
	private boolean isFlashClient;
	
	private List<IOClient> socketIOClients = new ArrayList<IOClient>();
    private SimpleClient darkstarClient;
    
    
	public List<IOClient> getSocketIOClients() {
        return socketIOClients;
    }

    public void setSocketIOClients(List<IOClient> socketIOClients) {
        this.socketIOClients = socketIOClients;
    }


    public void broadcastToSocketIOClients(String message) {
        if (getSocketIOClients() != null && !getSocketIOClients().isEmpty()) {
            for (IOClient ioClient : getSocketIOClients()) {
                GameHandler.getInstance().broadcast(ioClient, message);
                System.out.println("Sent to client: " + message);

            }
        } else {
            System.err.println("The player " + getUserId() + " don't have any socket.io clients connected right now");
        }
    }
    
    

    public SimpleClient getDarkstarClient() {
        if(this.darkstarClient == null){
            System.err.println("The Darkstar Client of this player is null. Be ready for a NullPointerException.");
        }
        return darkstarClient;
    }

    public void setDarkstarClient(SimpleClient darkstarClient) {
        this.darkstarClient = darkstarClient;
    }

	/**
	 * Creates player object for human players
	 * 
	 * @param userName
	 */
	public Player(String userId, String password) {
		this.userId = userId;
		this.password = password;
		setPlayerData();
		
	}
	
	public Player(String userId, String password, boolean isFlashClient) {
		this(userId,password);
		this.isFlashClient = isFlashClient;
	}
	
	public boolean isFlashClient(){
		return this.isFlashClient;
	}
	
	public void setPlayerData() {
		URLConnection urlConnection;													
    	try {																			
    		URL url = new URL("http://tcg.dyndns.info:8081/mcslcard/data/initPlayerData.jsp?userId=" + userId);
			urlConnection = url.openConnection();
			JSONObject jsonObjNest = new XMLParser().parseStream((InputStream)urlConnection.getContent());
			this.userName = jsonObjNest.getString("userNickName");
		} catch (Exception e) {															
			e.printStackTrace();														
		}
    }
	
	/** Players are equal if their userId's are equal 
	 * */
	public int compareTo(Player p){ 
		if (this.userId.equals(p.getUserId()))
				return 0;
		return 1;
	}
	
	/** get and set methods */
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getInterrupt() {
		return interrupt;
	}

	public void setInterrupt(int interrupt) {
		this.interrupt = interrupt;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public void addSocketIOClient(IOClient client) {
        if (socketIOClients == null){
            socketIOClients = new ArrayList<IOClient>();
        }
        socketIOClients.add(client);
        
    }
}
