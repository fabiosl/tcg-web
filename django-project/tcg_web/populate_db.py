'''
Created on May 15, 2012

@author: Fabio
'''

from tcg_app.models import User

u = User(name='Fabio', username='fabiosl' ,password='123', email='a@b.com')
u.save()
