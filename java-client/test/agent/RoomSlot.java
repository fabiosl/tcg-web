package agent;

/** class */
/**
 * The RoomSlot class is used to manage the slots in the rooms and games
 * that players occupy when they join a room and start a game
 */

public class RoomSlot {
	/** variables */
	/** int to store slot id - 0 = owner, 1 = player, 2 = observer, 3 = observer */
	private int slotId;
	/** boolean to store ready status */
	private boolean ready = false;
	/** player object to store player in slot */
	private Player player;
	
	/** constructor */
	/**
	 * Create the RoomSlot object to manage the slots used in the room and game
	 * objects
	 * 
	 * @param slotId
	 * @param player
	 */
	public RoomSlot(int slotId, Player player) {
		this.slotId = slotId;														// set slot id
		this.player = player;														// set player id
	}

	/** get and set methods*/
	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	
	
}
