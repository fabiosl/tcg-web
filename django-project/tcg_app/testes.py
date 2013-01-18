'''
Created on May 25, 2012

@author: Fabio
'''
from http_handler import *
from xml.dom.minidom import parseString

 
def getUserInfo(user_id):
    http_handler = HTTPHandler()
    paramDict = {'userId': user_id}
    
    result = http_handler.do_post("http://tcg.dyndns.info:8081/mcslcard/data/initPlayerData.jsp", paramDict)
    
    dom = parseString(result)
    
    dict_result = {}
    
    dict_result['user_id'] = user_id
    
    dict_result['user_nickname'] = dom.getElementsByTagName("userNickName")[0].firstChild.data
    dict_result['user_score'] = dom.getElementsByTagName("userScore")[0].firstChild.data 
    dict_result['user_play_counter'] = dom.getElementsByTagName("userPlayCounter")[0].firstChild.data 
    dict_result['user_win_counter'] = dom.getElementsByTagName("userWinCounter")[0].firstChild.data 
    dict_result['user_interrupt_counter'] = dom.getElementsByTagName("userInterruptCounter")[0].firstChild.data 
    dict_result['user_rank'] = dom.getElementsByTagName("userRank")[0].firstChild.data 
    return  dict_result

print None == None
#return render_to_response('control_panel.html', get_views_config(request))
