package agent;
import java.util.ArrayList;

/** class */
/**
 * The Room class is used to manage the rooms that are created by
 * user agents and other players to initiate (and assuming both parties
 * agree, play) a game.
 * 
 */
public class Room {
	/** variables */
	/** int to store room id */
	private int roomSid;
	/** string to store room name */
	private String roomName;
	/** string to store password if required by room */
	private String roomPassword;
	/** string to store username of room creator */
	private String roomOwner;
	/** int to store base life points */
	private int roomBaseLife;
	/** integer to store number of room slots full */
	private int roomSlotsFull;
	/** string to store player 1 */
	private String player1;
	/** string to store player 2 */
	private String player2;
	/** string to store player 3 */
	private String player3;
	/** string to store player 4 */
	private String player4;
	
	
	
	
	/** constructor */
	/**
	 * Creates the Room object to manage rooms and create the setup
	 * to initiate games, used when agent creates a room
	 *
	 * @param roomSid				room id
	 * @param roomName				room name
	 * @param roomPassword			room password (if applicable)
	 * @param roomOwner				room owner name
	 * @param roomSlotsFull			number of slots full in room
	 */
	public Room(int roomSid, String roomName, String roomPassword, String roomOwner, int roomSlotsFull) {
		this.roomSid = roomSid;																			// set room id
		this.roomName = roomName;																		// set room name
		this.roomPassword = roomPassword;																// set room password
		this.roomOwner = roomOwner;																		// set room owner
		this.player1 = roomOwner;																		// set player 1 to owner
		this.roomSlotsFull = roomSlotsFull;																// set room base life
	}
	
	/** Creates the basic Room object to add rooms to the room list
	 *
	 * @param roomSid				room id
	 * @param roomName				room name
	 * @param roomPassword			room password (if applicable)
	 * @param roomOwner				room owner name
	 * @param roomSlotsFull			number of slots full in room
	 */
	public Room(int roomSid, String roomName, String roomPassword, int roomSlotsFull) {
		this.roomSid = roomSid;																			// set room id
		this.roomName = roomName;																		// set room name
		this.roomPassword = roomPassword;																// set room password
		this.roomSlotsFull = roomSlotsFull;																// set room base life
	}
	
	/** get and set methods */
	public int getRoomSid() {
		return roomSid;
	}

	public void setRoomSid(int roomSid) {
		this.roomSid = roomSid;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomPassword() {
		return roomPassword;
	}

	public void setRoomPassword(String roomPassword) {
		this.roomPassword = roomPassword;
	}

	public String getRoomOwner() {
		return roomOwner;
	}

	public void setRoomOwner(String roomOwner) {
		this.roomOwner = roomOwner;
	}

	public int getRoomBaseLife() {
		return roomBaseLife;
	}

	public void setRoomBaseLife(int roomBaseLife) {
		this.roomBaseLife = roomBaseLife;
	}
	
	public String getPlayer1() {
		return player1;
	}

	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	public String getPlayer2() {
		return player2;
	}

	public void setPlayer2(String player2) {
		this.player2 = player2;
	}

	public String getPlayer3() {
		return player3;
	}

	public void setPlayer3(String player3) {
		this.player3 = player3;
	}

	public String getPlayer4() {
		return player4;
	}

	public void setPlayer4(String player4) {
		this.player4 = player4;
	}

	public int getRoomSlotsFull() {
		return roomSlotsFull;
	}

	public void setRoomSlotsFull(int roomSlotsFull) {
		this.roomSlotsFull = roomSlotsFull;
	}
	public boolean equals(Room r2){
		return (this.roomName == r2.roomName && this.roomOwner == r2.roomOwner);
	}
	
	public String getFriendlyId(){
		return this.getRoomName() + "_" +this.getRoomOwner();
	}
}
