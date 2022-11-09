from typing import Type

from base.models import BaseModel
from django.db import models
from django.db.models.signals import post_save
from django.dispatch import receiver
from forge.core.api.base import DataChangeType

from services.employee_manager.api.views import Employee as EmployeeEvent


class Employee(BaseModel):
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
def employee_emit(sender: Type[Employee], instance: Employee, created: bool, *_, **__):
    EmployeeEvent(**instance.to_dict()).emit(DataChangeType.CREATED if created else DataChangeType.UPDATED)
