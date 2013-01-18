# Create your views here.
from django.http import HttpResponse;
from django.template import Context, loader
from django.shortcuts import render_to_response
from tcg_app.models import User, Card, AvatarInfo, ToolInfo
from tcg_web.lang_dict import LanguageDictionary
import tcg_web.conf as conf
from http_handler import HTTPHandler
from django import forms
from django.views.decorators.csrf import csrf_exempt
from xml.dom.minidom import parseString
from django.http import Http404

import random

ld = LanguageDictionary()
http_handler = HTTPHandler()
cards_dict = {}

users_dictionary = {} #logged user id -> password; 

def _setUserInfoOnCache(request, user_id):
    paramDict = {'userId': user_id}
    result = http_handler.do_post(conf.getUserInfoURL(), paramDict)
    dom = parseString(result)
    request.session['user_id'] = user_id
    request.session['user_nickname'] = dom.getElementsByTagName("userNickName")[0].firstChild.data
    request.session['user_score'] = dom.getElementsByTagName("userScore")[0].firstChild.data 
    request.session['user_play_counter'] = dom.getElementsByTagName("userPlayCounter")[0].firstChild.data 
    request.session['user_win_counter'] = dom.getElementsByTagName("userWinCounter")[0].firstChild.data 
    request.session['user_interrupt_counter'] = dom.getElementsByTagName("userInterruptCounter")[0].firstChild.data 
    request.session['user_rank'] = dom.getElementsByTagName("userRank")[0].firstChild.data 
    

def _getUserInfoDictionary(request):
    if(_isLogged(request)):
        result = {}
        result['user_id'] = request.session.get('user_id', None)
        result['user_nickname'] = request.session.get('user_nickname', None)
        result['user_score'] = request.session.get('user_score', None)
        result['user_play_counter'] = request.session.get('user_play_counter', None)
        result['user_win_counter'] = request.session.get('user_win_counter', None)
        result['user_interrupt_counter'] = request.session.get('user_interrupt_counter', None)
        result['user_rank'] = request.session.get('user_rank', None)
        return result

def _debugUserInfo(request):
    print "################# DEBUG #####################"
    print "user_id: " + request.session['user_id']
    print "user_nickname: " + request.session['user_nickname']
    print "user_score: " + request.session['user_score'] 
    print "user_play_counter: " + request.session['user_play_counter']
    print "user_win_counter: " + request.session['user_win_counter'] 
    print "user_interrupt_counter: " + request.session['user_interrupt_counter']
    print "user_rank: " + request.session['user_rank'] 

def _getViewsConfig(request):
    config = ld.get_language_dictionary(_get_language(request))
    config["is_logged"] = _isLogged(request);
    config['login_error'] = False
    config['java_game_client_host'] = conf.getJavaGameClientHost();
    if(_isLogged(request)):
        user_info = _getUserInfoDictionary(request)
        config = dict(config.items() + user_info.items())    
    config["user_id"] = _getUserId(request);
    config["password"] = users_dictionary.get(_getUserId(request))
 
    return config


def _getUserId(request):
    user_id = request.session.get('user_id', None)
    if(user_id != None and user_id != ""):
        return user_id
    return None

def _isLogged(request):
    return _getUserId(request) != None

def _get_language(request):
    lang = request.GET.get('lang', None)
    if(lang != None):
        request.session['lang'] = lang
        return lang
    else: 
        lang = request.session.get('lang', None)
        if(lang != None):
            return lang
        else: 
            return ld.get_default_language()
    
    

def _getAttributeFromResponseData(attribute, response_data_list):
    for var in response_data_list:
        if(var.startswith(attribute)):
            return var[len(attribute) + 1:len(var)]
    return None


def about(request):
    return render_to_response ('about.html', _getViewsConfig(request))

def logout(request):
    request.session['user_id'] = None
    request.session['user_nickname'] = None
    request.session['user_score'] = None 
    request.session['user_play_counter'] = None 
    request.session['user_win_counter'] = None 
    request.session['user_interrupt_counter'] = None 
    request.session['user_rank'] = None 
    request.session['user_decks_num'] = None 
    return render_to_response('home.html', _getViewsConfig(request))


def home(request):
    request.session.set_expiry(0);
    
    if(_isLogged(request)):
        return main_hall(request);
    else:
        return render_to_response('home.html', _getViewsConfig(request))

@csrf_exempt
def edit_account(request):
    data_was_modified = request.GET.get('modified', 'false') == 'true'
    if data_was_modified:     
        user_id = _getUserInfoDictionary(request)['user_id']
        name = request.POST.get("nickname", None)
        old_password = request.POST.get("old_password", None)
        new_password = request.POST.get("new_password", None)
        new_password_confirmation = request.POST.get("new_password_confirmation", None)
        paramDict = {'userId': user_id, 'oldPwd':old_password}
        
        if(new_password != '' and new_password == new_password_confirmation):
            paramDict['newPwd'] = new_password
    
        if(name != ''):
            paramDict['nickName'] = name;
        
        result = http_handler.do_post(conf.getChangeUserDataURL(), paramDict)
        data_dict = _getViewsConfig(request)
        if(result == "result=success"):
            _setUserInfoOnCache(request, user_id)

            if(new_password != '' and new_password == new_password_confirmation):
                users_dictionary [user_id] = new_password
            
            data_dict["changed_user_data_successfully"] = True
        else:
            data_dict["changed_user_data_successfully"] = False
            
        data_dict["change_user_data_result"] = result
            
        
        return render_to_response('control_panel.html', data_dict)
    else:
        data_dict = _getViewsConfig(request)
        data_dict["no_change_performed"] = True
        return render_to_response('control_panel.html', data_dict)

def _getDeck(request, deck_id):
    params = "?language_id=EN&deck_id=%d&user_id=%s" % (int(deck_id), _getUserId(request))
    response = http_handler.do_get(conf.getShowUserDeckURL() + params)
    return response
    
    
def _getUserDecks(request):
    params = "?seed=%d&user_id=%s" % (random.randint(0, 2000), _getUserId(request))
    response = http_handler.do_get(conf.getUserDecksURL() + params)
    dom = parseString(response)

    user_decks = dom.getElementsByTagName("deckName")
    
    return user_decks

def main_hall(request):
    if _isLogged(request):
        data_dict = _getViewsConfig(request)
        data_dict['user_decks'] = _getUserDecks(request)
        return render_to_response('main_hall.html', data_dict)
    else:
        return render_to_response('home.html', _getViewsConfig(request))

def _getAvatarInfo (dom_object):
    avatar_info = AvatarInfo()
    avatar_info.mhp = dom_object.getElementsByTagName("mhp")[0].firstChild.data
    avatar_info.attack = dom_object.getElementsByTagName("atk")[0].firstChild.data
    avatar_info.defense = dom_object.getElementsByTagName("def")[0].firstChild.data
    avatar_info.rng = dom_object.getElementsByTagName("rng")[0].firstChild.data
    avatar_info.class_type = dom_object.getElementsByTagName("CLASS")[0].firstChild.data
    avatar_info.element = dom_object.getElementsByTagName("element")[0].firstChild.data
    avatar_info.size = dom_object.getElementsByTagName("size")[0].firstChild.data
    return avatar_info
    
def _getToolInfo (dom_object):
    t = ToolInfo()
    t.effect_interval = dom_object.getElementsByTagName("effectInterval")[0].firstChild.data
    return t
    
def _getCardFromDomObject(dom_object):
    c = Card()
    c.name = dom_object.getElementsByTagName("cardName")[0].firstChild.data
    c.image = conf.getServerDomain() + dom_object.getElementsByTagName("cardPath")[0].firstChild.data
    c.info = dom_object.getElementsByTagName("cardInfo")[0].firstChild.data
    
    c.user_card_id = dom_object.getAttribute("userCardID");
    c.card_id = dom_object.getAttribute("cardID");
    c.card_type = dom_object.getAttribute("cardType");
    c.card_strength = dom_object.getAttribute("cardStrength");
    
    if (c.card_type == "A"):
        c.avatar_info = _getAvatarInfo(dom_object.getElementsByTagName("avatarInfo")[0])
    else: #Magic and Trap cards have Tool info instead of AvatarInfo
        c.tool_info = _getToolInfo(dom_object.getElementsByTagName("toolInfo")[0])
    cards_dict[c.card_id] = c
    return c
    
    
def _getCardsList(dom_card_list):    
    result = []
    
    for dom_card in dom_card_list.getElementsByTagName("card"): 
        c = _getCardFromDomObject(dom_card)
        result.append(c)
    return result
    
@csrf_exempt
def save_deck (request):
    user_id = request.session.get('user_id', None)
    deck_id = request.session.get('editing_deck_id', None)
    deck_name = request.POST.get('deckName', None);
    card_string = request.POST.get('cardString', None);
    params = "?deck_id=%s&deck_name=%s&user_id=%s&cardString=%s" % (deck_id, deck_name,user_id, card_string)
    
    
    data_dict = _getViewsConfig(request)
    
    result = http_handler.do_get(conf.getSaveDeckURL() + params)
    result = result.split("&")
    result = _getAttributeFromResponseData("result", result)
    data_dict['result'] = result 
    
    return render_to_response('simple_result.html',data_dict)


def create_deck(request):
    deck_xml = parseString(_getDeck(request, "-1")) #server expects -1 when user creates a deck for the first time.
    all_cards = deck_xml.getElementsByTagName("allcards")[0]
    deck_cards = deck_xml.getElementsByTagName("deckcards")[0]
    all_cards = _getCardsList(parseString(all_cards.toxml())) 
    deck_cards = _getCardsList(parseString(deck_cards.toxml()))
    data_dict = _getViewsConfig(request);
    data_dict['user_decks'] = _getUserDecks(request)
    data_dict['editing_deck_name'] = "New deck"
    data_dict['all_cards'] = all_cards
    data_dict['deck_cards'] = deck_cards
    data_dict['creating_new_deck'] = True;
   
    request.session['editing_deck_id'] = -1 #Server expects -1 on the request to create a new deck
    request.session['editing_deck_name'] = "New Deck"
    
    return render_to_response('edit_deck.html', data_dict)
              
              
def show_card (request,card_id):
    data_dict = _getViewsConfig(request)
    data_dict['card']= cards_dict[card_id]
    return render_to_response('card_view.html', data_dict)
              
def edit_deck(request, deck_id):
    if deck_id == None:
        return render_to_response('home.html', _getViewsConfig(request))
    else:
        if(int(deck_id) < 0 or int(deck_id) >= len(_getUserDecks(request))):
            raise Http404
        
        deck_xml = parseString(_getDeck(request, deck_id))
        all_cards = deck_xml.getElementsByTagName("allcards")[0]
        deck_cards = deck_xml.getElementsByTagName("deckcards")[0]
        all_cards = _getCardsList(parseString(all_cards.toxml())) 
        deck_cards = _getCardsList(parseString(deck_cards.toxml())) 
        
        data_dict = _getViewsConfig(request);
        data_dict['user_decks'] = _getUserDecks(request)
        data_dict['editing_deck_name'] = data_dict['user_decks'][int(deck_id)].firstChild.data
        data_dict['all_cards'] = all_cards
        data_dict['deck_cards'] = deck_cards

        request.session['editing_deck_id'] = deck_id
        request.session['editing_deck_name'] = data_dict['user_decks'][int(deck_id)].firstChild.data
        
        return render_to_response('edit_deck.html', data_dict)
        
@csrf_exempt
def delete_deck(request):
    user_id = _getUserId(request)    
    deck_id = request.session.get('editing_deck_id', None)
    data_dict = _getViewsConfig(request)
    params =  "?deck_id=%s&user_id=%s" % (deck_id, user_id)
    print "REQUEST: " + conf.getDeleteDeckURL() + params
    result = http_handler.do_get(conf.getDeleteDeckURL() + params)
    result = result.split("&")
    result = _getAttributeFromResponseData("result", result)
    data_dict['result'] = result 
    
    return render_to_response('simple_result.html',data_dict)

@csrf_exempt
def login(request):
    if(_isLogged(request)):
        return main_hall(request)
    else:
        username = request.POST.get('username', None)
        password = request.POST.get('password', None)
        params = "?acc=%s&pwd=%s" % (username, password)
        login_response = http_handler.do_get(conf.getLoginURL() + params)
        login_attributes = login_response.split("&")
        login_ok = _getAttributeFromResponseData('loginOK', login_attributes)    
        if(login_ok == 'true'):
            _setUserInfoOnCache(request, username)
            users_dictionary [username] = password
            
            return main_hall(request)
        else:
            data_dict = _getViewsConfig(request)
            data_dict['login_error'] = True
            return render_to_response('home.html', data_dict)
                                         
def contact(request):
    return render_to_response('contact.html', _getViewsConfig(request))


