'''
Created on May 22, 2012

@author: Fabio
'''
import shutil
import os
import time
import datetime
import math
import urllib
import urllib2

from array import array

class HTTPHandler():
    
    def do_get (self, website):
        return urllib.urlopen(website).read()
    
    def do_post(self, url, dataDict):
        dataDict = urllib.urlencode(dataDict)
        req = urllib2.Request(url,dataDict)
        response = urllib2.urlopen(req)
        page = response.read()
        return page;
        
