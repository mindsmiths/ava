from django.db import models


class Department(models.Model):

    name = models.CharField(max_length=200)

    def __str__(self):
        return f"{self.name}"


class Employee(models.Model):
    name = models.CharField(max_length=200)
    surname = models.CharField(max_length=200)
    mail = models.CharField(max_length=200)
    department = models.ForeignKey(Department, on_delete=models.CASCADE, null=True)
    active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.name} {self.surname}"

