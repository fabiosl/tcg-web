package agent;

import gameserver.GameHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

import agent.util.Util;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.simple.SimpleClient;
import com.yongboy.socketio.server.transport.IOClient;

/**
 * This class should be the controller that will handle the SimpleClients that
 * must be connected to SGS-Server (DarkStar Server).
 * 
 * @author fabiosl
 * 
 */
public class PlayersController {


    private static Map<String, Room> lobbyRooms = new HashMap<String, Room>();
    private static Map<String, ClientChannel> channelMap = new HashMap<String, ClientChannel>();
    private static Map<String, Player> clientMap = new HashMap<String, Player>();
//    private List<Room> availableRooms = new ArrayList<Room>();
//    private AgentProcessor agentProcessor = AgentProcessor.getInstance();

    public static Map<String, ClientChannel> getChannelMap() {
        return channelMap;
    }

    public synchronized static void addRoom(Room room) {
        if (room == null) {
            System.err.println("Trying to add a null room");
            return;
        }

        for (String key : lobbyRooms.keySet()) {
            Room r = lobbyRooms.get(key);
            if (room.equals(r)) { // This room already exists
                System.err.println("Are you trying to add a room that already exists?");
                return;
            }
        }
        lobbyRooms.put(room.getFriendlyId(), room);
    }

    public synchronized static void removeRoom(Room room) {
        if (room == null) {
            System.err.println("Trying to remove a null room");
            return;
        }

        for (String key : lobbyRooms.keySet()) {
            Room r = lobbyRooms.get(key);
            if (room.equals(r)) { // This room already exists
                lobbyRooms.remove(room);
                return;
            }
        }

        System.err.println("Could not remove the room " + room.getFriendlyId());
    }


    public Set<String> getLoggedPlayersNames() {
        Set<String> names = new TreeSet<String>();
        for (String playerId : clientMap.keySet()) {
            names.add(clientMap.get(playerId).getUserName());
        }
        return names;
    }

    public static synchronized void createPlayerClient(String userId,
            String password, boolean isFlashPlayer) {
        if (!clientMap.containsKey(userId)) {
            SimpleClient client = null;
            if (!isFlashPlayer) {
                client = new SimpleClient(new ClientListener(userId, password));
            }
            Player player = new Player(userId, password, isFlashPlayer);
            player.setDarkstarClient(client);
            clientMap.put(userId, player);
        } else {
            System.err.println("Are you trying to create a player that already exists?");
        }
    }

    public static Player getPlayer(String userId) {
        if (clientMap.get(userId) == null) {
            System.err.println("You are trying to get a player that does not exist: "+ userId);
        }
        return clientMap.get(userId);
    }

    public static SimpleClient getPlayerClient(String userId) {
        Player player = clientMap.get(userId);
        if (player == null) {
            System.err
                    .println("You are trying to get the Darkstar client of a player that does not exist.");
        }

        return player.getDarkstarClient();
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
            createPlayerClient(userId, password, false);
            playerClient = getPlayerClient(userId);
            playerClient.login(connectProps);
            Thread.sleep(5000);

            if (!playerClient.isConnected()) {
                PlayersController.removePlayer(userId);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println(exception.getMessage());
        }
        return playerClient != null && playerClient.isConnected();
    }


    private static void loadLoginSequence(String userId) {
        URLConnection urlConnection;
        CharSequence charSequence = "loginSeq=";
        String tempString = "";
        BufferedReader in;
        try {
            if (PlayersController.getPlayerClient(userId).isConnected()) {
                URL url = new URL("http://tcg.dyndns.info:8081/mcslcard/data/saveLogin.jsp?userId="+ userId);
                urlConnection = url.openConnection();
                in = new BufferedReader(new InputStreamReader(
                        (InputStream) urlConnection.getContent()));
                while ((tempString = in.readLine()) != null) {
                    if (tempString.contains(charSequence)) {
                        tempString = tempString.substring(
                                charSequence.length(), tempString.length());
                    }
                    JSONObject jsonObjNest = new JSONObject();
                    jsonObjNest.put("loginSeq", tempString);
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("cmd", "SET_LOGIN_SEQ");
                    jsonObj.put("parameters", jsonObjNest);
                    jsonObj.put("scene", "ALL_SCENE");
                    sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId),jsonObj.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSessionMsgToDarkstar(SimpleClient playerClient,
            String text) {
        try {
            if(!text.equals("ping"))
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
            if (PlayersController.getPlayerClient(userId).isConnected()) {
                URL url = new URL(
                        "http://tcg.dyndns.info:8081/mcslcard/data/initPlayerData.jsp?userId="
                                + userId);

                urlConnection = url.openConnection();
                JSONObject jsonObjNest = new XMLParser()
                        .parseStream((InputStream) urlConnection.getContent());
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("cmd", "INIT_PLAYER_DATA");
                jsonObj.put("parameters", jsonObjNest);
                jsonObj.put("scene", "ALL_SCENE");
                sendSessionMsgToDarkstar(PlayersController.getPlayerClient(userId),jsonObj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean login(String userId, String password) {
        if (clientMap.keySet().contains(userId)) { // Check if is already logged in and the user client was added to the map. If client was added to the map, simply return loginTCG.
            return loginTCG(userId, password);
        } else {
            boolean logged = loginDarkstar(userId, password);
            System.out.println("User " + userId + " is logged? " + logged);
            if (logged) {
                getPlayerInitData(userId);
                loadLoginSequence(userId);
            }
            return logged;
        }

    }

    public static void removePlayer(String userId) {
        System.out.println("I will remove " + userId + " from the players map.");
        Player player = clientMap.get(userId);
        // TODO: This verification should be unnecessary. Only HTML clients should be stored
        if (player != null && !player.isFlashClient() && player.getDarkstarClient().isConnected()) {
            player.getDarkstarClient().logout(false); //Do NOT try to logout forcing it. The code that is written after logout(true) will never be executed. API Bug.
            player.setDarkstarClient(null);
            for (IOClient socketIOClient : player.getSocketIOClients()) {
                socketIOClient.disconnect();
            }
        }

        clientMap.remove(userId);
        System.err.println("I have removed " + userId + " from the players map.");
        System.gc();
    }

    public static void removeSocketIOClient(String sessionId) {
        for (String userId : clientMap.keySet()) {
            Player player = clientMap.get(userId);
            if (player != null && !player.isFlashClient()) {
                for (IOClient socketIOClient : player.getSocketIOClients()) {
                    if (socketIOClient.getSessionID().equals(sessionId)) {
                        player.getSocketIOClients().remove(socketIOClient);
                        if (player.getSocketIOClients().isEmpty()) {
                            removePlayer(player.getUserId());
                        }
                    }
                }
            }

        }
    }

    public static void broadcastLoggedPlayers() {
        GameHandler.getInstance().emit("updateLoggedUsers",
                new TreeSet<String>(clientMap.keySet()));
    }

    public static void createRoom(String userId, String roomName,
            String roomPassword) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.append("cmd", "CREATE_ROOM");
            jsonObj.append("scene", "LOBBY");
            JSONObject jsonObjNest = new JSONObject();
            jsonObjNest.append("roomName", roomName);
            jsonObjNest.append("roomPwd", roomPassword);
            jsonObj.append("parameters", jsonObjNest);
            PlayersController.sendSessionMsgToDarkstar(getPlayerClient(userId),
                    jsonObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void debug() {
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        for (String userId : clientMap.keySet()) {
            Player player = clientMap.get(userId);
            System.out.println("    Player: "+ userId+ (player.isFlashClient() ? " - Flash Client": " - HTML Client"));
            System.out.println("    Socket.IO clients: " + player.getSocketIOClients().size());
            System.out.println("    Darkstar client: " + player.getDarkstarClient() + (player.getDarkstarClient() != null ? "" + player.getDarkstarClient().isConnected() : ""));
        }

        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    public static void pingDarkstarClients() {
        for (String userId : clientMap.keySet()) {
            Player p = clientMap.get(userId);
            if(p.getDarkstarClient().isConnected()){
                sendSessionMsgToDarkstar(p.getDarkstarClient(), "ping" );
            }
        }
    }

}
