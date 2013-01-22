var GameSocket = (function() {
    // "private" variables 
    var _socket;
    
    // constructor
    function GameSocket(host){
    	_socket = io.connect(host)
		_socket.on('connect', notifyChat_UI);
		_socket.on('updateLoggedUsers', updateLoggedUsers_UI);
		_socket.on('roomCreation', roomCreation_UI)
		_socket.on('chatMessage', writeMessageOnChat_UI);
		_socket.on('reconnect',reconnect_UI);
		_socket.on('reconnecting', reconnecting_UI);
		_socket.on('error', error_UI);
		_socket.on('updateRooms', updateRooms_UI)
		
    };

	GameSocket.prototype.connect = function(user_id,password) {
		_socket.emit('establishConnection', user_id, password,  function(set) {
			clear_UI();
			return $('#chat').addClass('nickname-set');
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