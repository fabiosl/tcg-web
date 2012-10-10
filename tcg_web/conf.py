'''
Created on May 17, 2012

@author: Fabio
'''

tomcat_host  = "tcg.dyndns.info"
tomcat_port = 8081
tomcat_root = "mcslcard"
tomcat_jsp_directory = "JSP/"
tomcat_data_directory = "data/"
tomcat_server_domain = "http://" + str(tomcat_host) + ":" + str(tomcat_port) + "/" + str(tomcat_root) + "/"


sgs_host = "tcg.dyndns.info";
sgs_port = 842;

        
java_game_client_server = "localhost"
java_game_client_port = 9000

def getJavaGameClientHost():
    return "http://" + java_game_client_server + ":" + str(java_game_client_port);

def getServerDomain():
    return tomcat_server_domain

def getJspDirectory():
    return getServerDomain() + tomcat_jsp_directory

def getDataDirectory():
    return getServerDomain() + tomcat_data_directory

def getLoginURL():
    return getJspDirectory() + "Login_flash.jsp" #http://tcg.dyndns.info:8081/mcslcard/JSP/Login_flash.jsp?acc=X&pwd=Y

def getChangeUserDataURL():
    return getDataDirectory() + "changePlayerData.jsp" #http://tcg.dyndns.info:8081/mcslcard/data/changePlayerData.jsp?userId=XoldPwd=X&newPwd=Y&nickname=Z (POST!) )

def getUserInfoURL():
    return getDataDirectory() + "initPlayerData.jsp"

def getSaveCmdURL():
    return getDataDirectory()+ "saveCmd.jsp"

def getUserDecksURL():
    return getDataDirectory()+ "show_deck_name.jsp"

def getShowUserDeckURL():
    return getDataDirectory()+ "show_deck.jsp" #http://tcg.dyndns.info:8081/mcslcard/data/show_deck.jsp?language%5Fid=EN&deck%5Fid=0&user%5Fid=fabio1

def getSaveDeckURL():
    return getDataDirectory() + "add_deck.jsp" #http://tcg.dyndns.info:8081/mcslcard/data/add_deck.jsp?deck%5Fid=0&deck%5Fname=DeckInicial&user%5Fid=fabio&cardString=5462%2A5496%2A5495%2A5494%2A5493%2A5492%2A5491%2A5490%2A5489%2A5488%2A5487%2A5486%2A5485%2A5484%2A5483%2A5482%2A5481%2A5480%2A5479%2A5478%2A5477%2A5475%2A5474%2A5473%2A5472%2A5471%2A5470%2A5465%2A5464%2A5463

def getDeleteDeckURL():
    return getDataDirectory()+ "del_deck.jsp"
