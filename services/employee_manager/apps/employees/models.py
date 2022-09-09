from django.db import models
from django.db.models.signals import post_save
from django.dispatch import receiver

from services.employee_manager.api.views import Employee as EmployeeView


class Employee(models.Model):
    id = models.AutoField(primary_key=True, db_index=True)
    first_name = models.CharField(max_length=200)
    last_name = models.CharField(max_length=200)
    email = models.EmailField()
    active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.first_name} {self.last_name}"

    def to_dict(self):
        return {
            "id": self.id,
            "firstName": self.first_name,
            "lastName": self.last_name,
            "email": self.email,
            "active": self.active
        }


@receiver(post_save, sender=Employee)
def employee_emit(sender, instance: Employee, *args, **kwargs):
    EmployeeView(**instance.to_dict()).emit()
