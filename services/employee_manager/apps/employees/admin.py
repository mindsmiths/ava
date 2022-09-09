from django.contrib import admin
from import_export.admin import ImportExportModelAdmin

from .models import Employee


class EmployeeAdmin(ImportExportModelAdmin, admin.ModelAdmin):
    list_display = ('firstName', 'lastName', 'email', 'activebox',)

    @admin.display(description='active')
    def activebox(self, obj):
        return ("%s" % (obj.active))


admin.site.register(Employee, EmployeeAdmin)
