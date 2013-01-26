var GameSocket = (function() {
    // "private" variables 
    var _socket;
    
    // constructor
    function GameSocket(host){
    	_socket = io.connect(host)
		_socket.on('connect', connect_UI);

    	
    	_socket.on('LOAD_PLAYER_LIST', loadPlayerList);
		_socket.on('ADD_PLAYER_TO_LIST', addPlayerToList);
		_socket.on('REMOVE_PLAYER_FROM_LIST', removePlayerFromList);
		
		
		_socket.on('LOAD_ROOM_LIST', updateRooms_UI)
		_socket.on('ADD_ROOM_TO_BATTLE_LIST', addRoom_UI)
		_socket.on('REMOVE_ROOM_FROM_BATTLE_LIST', removeRoom_UI)
		
		
		_socket.on('CHAT', writeMessageOnChat_UI);
		
		_socket.on('reconnect',reconnect_UI);
		_socket.on('reconnecting', reconnecting_UI);
		_socket.on('error', error_UI);
		
		
    };

	GameSocket.prototype.connect = function(user_id,password) {
		_socket.emit('establishConnection', user_id, password,  function(set) {
			if(set){
				writeOnStatusBar("Connected to Game Server")
			} else{
				alert("Could not login to Darkstar Server right now. Server needs to be rebooted.")
				window.location = "/logout"
			}
	    });
    };
    
	GameSocket.prototype.worker = function() {
        _socket.emit('ping', 'ping');
        setTimeout(GameSocket.prototype.worker, 30000);
    };
    
    GameSocket.prototype.sendCreateRoomSignal = function(roomName){
        _socket.emit('createRoom', roomName);
    };
    
    GameSocket.prototype.sendChatMessage = function(message){
    	_socket.emit('chatMessage', message);
    };
    
    return GameSocket;
})();