from django.contrib import admin

from .models import Employee
from .models import Department

admin.site.register(Employee)
admin.site.register(Department)
