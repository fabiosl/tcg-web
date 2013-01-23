var GameSocket = (function() {
    // "private" variables 
    var _socket;
    
    // constructor
    function GameSocket(host){
    	_socket = io.connect(host)
		_socket.on('connect', connect_UI);
		_socket.on('updateLoggedUsers', updateLoggedUsers_UI);
		_socket.on('addLoggedUser', addLoggedUser_UI);
		_socket.on('removeLoggedUser', removeLoggedUser_UI);
		
		_socket.on('roomCreation', roomCreation_UI)
		_socket.on('chatMessage', writeMessageOnChat_UI);
		_socket.on('reconnect',reconnect_UI);
		_socket.on('reconnecting', reconnecting_UI);
		_socket.on('error', error_UI);
		_socket.on('updateRooms', updateRooms_UI)
		
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