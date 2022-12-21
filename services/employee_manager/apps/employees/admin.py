from django.contrib import admin
from import_export import resources
from import_export.admin import ImportExportModelAdmin

from .models import Employee


class EmployeeResource(resources.ModelResource):
    class Meta:
        model = Employee
        import_id_fields = ['email']
        skip_unchanged = True
        fields = ['first_name', 'last_name', 'email']

    def before_save_instance(self, instance, using_transactions, dry_run):
        instance.dry_run = dry_run


class EmployeeAdmin(ImportExportModelAdmin):
    list_display = ('first_name', 'last_name', 'email', 'activebox')
    resource_class = EmployeeResource

    @admin.display(description='active')
    def activebox(self, obj):
        return "%s" % obj.active


admin.site.register(Employee, EmployeeAdmin)
