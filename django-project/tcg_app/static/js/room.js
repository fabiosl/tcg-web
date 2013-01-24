var Room = (function() {
    // "private" variables 
	var needPwd;
	var hostNickName;
	var roomName;
    var numOfPlayers;
    var roomSid;
    var isPlaying;
    
    // constructor
    function Room(needPwd,hostNickName,roomName,numOfPlayers,roomSid,isPlaying){
    	this.needPwd = needPwd
    	this.hostNickName = hostNickName
    	this.roomName = roomName
    	this.numOfPlayers = numOfPlayers
    	this.roomSid = roomSid
    	this.isPlaying = isPlaying
    };

    return Room;
})();