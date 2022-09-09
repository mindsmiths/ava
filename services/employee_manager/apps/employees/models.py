from django.db import models
from services.employee_manager.api.views import Employee as EmployeeView
from django.db.models.signals import post_save
from django.dispatch import receiver
from forge.utils.base import random_generator


class Employee(models.Model):
    id = models.AutoField(primary_key=True, db_index=True)
    firstName = models.CharField(max_length=200)
    lastName = models.CharField(max_length=200)
    email = models.EmailField()
    active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.firstName} {self.lastName}"

    def to_dict(self):
        return {
            "id": self.id,
            "firstName": self.firstName,
            "lastName": self.lastName,
            "email": self.email,
            "active": self.active
        }

@receiver(post_save, sender=Employee)
def Employeeupdated(sender, instance: Employee, *args, **kwargs):
    EmployeeView(**instance.to_dict()).emit()