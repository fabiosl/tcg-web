'''
Created on May 17, 2012

@author: Fabio
'''
class LanguageDictionary:
    dic_en_US = {}
    dic_pt_BR = {}
    
    
    def get_language_dictionary(self, lang):
        if (lang == "pt_BR"):
            return self.dic_pt_BR
        elif lang == "en_US":
            return self.dic_en_US
    
    def __init__(self):
        
        self.dic_en_US["username"] = "Username"
        self.dic_en_US["password"] = "Password"
        self.dic_en_US["welcome"] = "Welcome"
        self.dic_en_US["welcome_message"] = "MCSL TCG is a discipline-independant educational reward game. This is the HTML5 version of the game, and it is currently under development."
        
        
        self.dic_pt_BR["welcome"] = "Bem vindo"
        self.dic_pt_BR["password"] = "Senha"
        self.dic_pt_BR["welcome_message"] = "MCSL TCG eh um \"jogo educacional-independente-de-disciplina\". Essa eh a versao HTML5 do jogo e encontra-se em fase de desenvolvimento."
        self.dic_pt_BR["username"] = "Nome do usuario"
        
    
    def get_default_language(self):
        return "en_US"
    
    def get_message(self, message, lang):
        if (lang == "pt_BR"):
            return self.dic_pt_BR[message]
        elif (lang == "en_US"):
            return self.dic_en_US[message]
        else: 
            return "NOTFOUND"
