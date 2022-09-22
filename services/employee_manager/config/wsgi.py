"""
WSGI config

"""
import os
import sys

import forge
from django.core.wsgi import get_wsgi_application

# Have both project and service roots in path
if 'services/employee_manager' not in os.getcwd():
    sys.path.append(os.getcwd() + '/services/employee_manager')
else:
    sys.path.append(os.getcwd().replace('/services/employee_manager', ''))

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings.production')
forge.setup('employee_manager')

application = get_wsgi_application()

# if not Module.is_test():
from service import EmployeeManagerListener  # noqa: E402
EmployeeManagerListener().start_in_thread()
