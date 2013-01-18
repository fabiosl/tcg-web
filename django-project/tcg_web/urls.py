from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin

admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    url(r'^favicon\.ico$', 'django.views.generic.simple.redirect_to', {'url': '/static/img/au-icon.png'}),
    url(r'^$', 'tcg_app.views.home', name='home'),
    url(r'^userlogin/', 'tcg_app.views.login'),
    url(r'^ranking/', 'tcg_app.views.ranking'),
    url(r'^about/', 'tcg_app.views.about'),
    url(r'^contact/', 'tcg_app.views.contact'),
    url(r'^logout/', 'tcg_app.views.logout'),
    url(r'^main_hall/', 'tcg_app.views.main_hall'),
    url(r'^edit_account/', 'tcg_app.views.edit_account'),
    url(r'^create_deck/', 'tcg_app.views.create_deck'),
    url(r'^edit_deck/(?P<deck_id>\d+)/$', 'tcg_app.views.edit_deck'),
    url(r'^show_card/(?P<card_id>\d+)/$', 'tcg_app.views.show_card'),
    url(r'^save_deck', 'tcg_app.views.save_deck'),
    url(r'^save_deck/', 'tcg_app.views.save_deck'),
    url(r'^delete_deck', 'tcg_app.views.delete_deck'),
    url(r'^delete_deck/', 'tcg_app.views.delete_deck'),
    url(r'^user/(?P<username>\w+)/$', 'tcg_app.views.user_detail'),
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),
    url(r'^admin/', include(admin.site.urls)),
    #url(r'', 'tcg_app.views.index'),
    # url(r'^$', 'tcg_web.views.home', name='home'),
    # url(r'^tcg_web/', include('tcg_web.foo.urls')),
    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),
    # Uncomment the next line to enable the admin:
)
