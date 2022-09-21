from django.db import models

from base.models import BaseModel
from employees.models import Employee


class Match(BaseModel):
    first_employee = models.ForeignKey(Employee, on_delete=models.CASCADE,
                                       related_name='+')
    second_employee = models.ForeignKey(Employee, on_delete=models.CASCADE,
                                        related_name='+')
    day_of_week = models.CharField(max_length=50)
    date = models.DateTimeField(null=True, blank=True)

    def __str__(self):
        return f"{self.first_employee} {self.second_employee} \
                 {self.day_of_week} {self.date}"

    class Meta:
        verbose_name_plural = "matches"
