from django.contrib import admin
from .models import Employee
from import_export.admin import ImportExportModelAdmin
from django.contrib import admin




class EmployeeAdmin(ImportExportModelAdmin, admin.ModelAdmin):
    list_display = ('firstName', 'lastName', 'email', 'activebox',)

    @admin.display(description='active')
    def activebox(self, obj):
        return ("%s" % (obj.active))      

admin.site.register(Employee, EmployeeAdmin)
