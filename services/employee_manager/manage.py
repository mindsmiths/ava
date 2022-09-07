#!/usr/bin/env python
import os
import sys

import forge

if __name__ == '__main__':
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "config.settings.local")
    try:
        from django.core.management import execute_from_command_line
    except ImportError:
        try:
            import django  # noqa: 504
        except ImportError:
            raise ImportError(
                "Couldn't import Django. Are you sure it's installed and "
                "available on your PYTHONPATH environment variable? Did you "
                "forget to activate a virtual environment?"
            )
        raise

    # Have both project and service roots in path
    if 'services/employee_manager' not in os.getcwd():
        sys.path.append(os.getcwd() + '/services/employee_manager')
    else:
        sys.path.append(os.getcwd().replace('/services/employee_manager', ''))

    if len(sys.argv) > 1 and sys.argv[1] == 'test':
        os.environ['MODULE'] = 'test'

    os.environ['PROJECT_SLUG'] = 'ava'
    forge.setup('employee_manager')

    execute_from_command_line(sys.argv)
