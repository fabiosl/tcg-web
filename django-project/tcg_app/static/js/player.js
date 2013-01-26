var Player = (function() {
    // "private" variables 
	var nickName;
	var userId;
    
    // constructor
    function Player(nickName,userId){
    	this.nickName = nickName
    	this.userId = userId
    };
    
    return Player;
})();