from django.db import models
from tcg_web.lang_dict import LanguageDictionary

lang_dic = LanguageDictionary();

class User(models.Model):
    name = models.CharField(max_length=20)
    username = models.CharField(max_length=10)
    password = models.CharField(max_length=20)
    email = models.EmailField()
    def __unicode__(self):
        return self.email + '/' + self.password
    
class AvatarInfo(models.Model):
    mhp = models.IntegerField();
    attack = models.IntegerField();
    defense = models.IntegerField();
    rng = models.IntegerField();
    class_type = models.IntegerField(max_length=20);
    element = models.IntegerField(max_length=20);
    size = models.IntegerField();
    
class ToolInfo (models.Model):
    effect_interval = models.IntegerField();
    
class Card(models.Model):
    user_card_id = models.IntegerField();
    card_id = models.IntegerField();
    card_type = models.CharField(max_length=1) #A,M,T
    card_strength = models.IntegerField();
    name = models.CharField(max_length=30)
    image = models.CharField(max_length=50)
    info = models.CharField(max_length=400)
    
    def __unicode__(self):
        return self.name+ "; " + "USER_CARD_ID: " + self.user_card_id;
    
class Deck(models.Model):
    cards = models.ManyToManyField(Card)