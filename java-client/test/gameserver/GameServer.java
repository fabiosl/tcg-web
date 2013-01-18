package gameserver;

import agent.PlayersController;

import com.yongboy.socketio.MainServer;

class CheckLoggedUsersThread extends Thread {
    public void run() {
    	PlayersController.debugIOClientMap();
    	PlayersController.debugSimpleClientMap();
    	try {
			Thread.sleep(5000);
			run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

public class GameServer {

	static boolean DEBUG = true;
	static final int GAME_SERVER_PORT = 9000;
	
	public static void main(String[] args) {
		MainServer gameServer = new MainServer(GameHandler.getInstance(), GAME_SERVER_PORT);
		gameServer.start();
		
		if(DEBUG){
			CheckLoggedUsersThread c = new CheckLoggedUsersThread();
			c.run();
		}
	}
}