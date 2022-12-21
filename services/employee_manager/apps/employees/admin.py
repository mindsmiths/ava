from django.contrib import admin
from django.utils.decorators import method_decorator
from django.views.decorators.http import require_POST
from import_export import resources
from import_export.admin import ImportExportModelAdmin
from import_export.fields import Field

from .models import Employee


class EmployeeResource(resources.ModelResource):
    # id = Field(attribute='id', readonly=True)

    class Meta:
        model = Employee
        import_id_fields = ['email']
        exclude = ['id']
        skip_unchanged = True
        fields = ['first_name', 'last_name', 'email']

    def get_instance(self, instance_loader, row):
        """
        If all 'import_id_fields' are present in the dataset, calls
        the :doc:`InstanceLoader <api_instance_loaders>`. Otherwise,
        returns `None`.
        """
        try:
            return self.get_queryset().get(email=row["email"])
        except Exception:
            pass


class EmployeeAdmin(ImportExportModelAdmin):
    list_display = ('first_name', 'last_name', 'email', 'activebox')
    resource_class = EmployeeResource

    @admin.display(description='active')
    def activebox(self, obj):
        return "%s" % obj.active

    # def import_action(self, request, *args, **kwargs):
    #
    #     super().import_action(request, *args, **kwargs)

    @method_decorator(require_POST)
    def process_import(self, request, *args, **kwargs):
        data = super().process_import(request, *args, **kwargs)

        return data


admin.site.register(Employee, EmployeeAdmin)
