var Room = (function() {
    // "private" variables 
	var _needPwd;
	var _hostNickName;
	var _roomName;
    var _numOfPlayers;
    var _roomSid;
    var _isPlaying;
    
    // constructor
    function Room(_needPwd,_hostNickName,_roomName,_numOfPlayers,_roomSid,_isPlaying){
    	_needPwd = needPwd;
    	_hostNickName = hostNickName ;
    	_roomName = roomName;
        _numOfPlayers = numOfPlayers;
        _roomSid = roomSid;
        _isPlaying = isPlaying;
		
    };

    return Room;
})();