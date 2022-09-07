# -*- coding: utf-8 -*-
"""
Production Configurations
"""
from __future__ import absolute_import, unicode_literals

from .common import *  # noqa: F403

REPO_SITE_SLUG = 'employee_manager_production'

DEBUG = False
SECRET_KEY = env('SECRET_KEY')  # noqa: F405

SITE_URL = env('SITE_URL')  # noqa: F405
INTERNAL_SITE_URL = env('INTERNAL_SITE_URL')  # noqa: F405
ALLOWED_HOSTS = ['localhost', SITE_URL.split('//')[-1], INTERNAL_SITE_URL.split('//')[-1]]

MODULE = 'production'
