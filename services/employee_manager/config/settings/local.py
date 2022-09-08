# -*- coding: utf-8 -*-
"""
Local settings

- Run in Debug mode
- Use console backend for emails
- Add Django Debug Toolbar
- Add django-extensions as app
"""
from .common import *  # noqa: F403

DEBUG = True
TEMPLATES[0]['OPTIONS']['debug'] = True  # type: ignore # noqa: F405

SECRET_KEY = env('SECRET_KEY', default='BKZvpQTokpkC7ArkUBdXGIyYe9s1py2XvQMTcyt7nFIQolTaS5')  # noqa: F405
ALLOWED_HOSTS = ['*']
SITE_URL = 'http://localhost:8000'

MODULE = 'local'

CACHES = {
    'default': {
        'BACKEND': 'django.core.cache.backends.dummy.DummyCache',
        'LOCATION': '127.0.0.1:11211',
    }
}

MIDDLEWARE += []  # noqa: F405
INSTALLED_APPS += ()  # type: ignore # noqa: F405

INTERNAL_IPS = ['127.0.0.1']
