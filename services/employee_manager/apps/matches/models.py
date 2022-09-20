from django.db import models

from base.models import BaseModel
from django.forms import ModelForm, Textarea
from django.utils.translation import gettext_lazy as _


class Match(BaseModel):
    first_employee = models.CharField(max_length=200)
    second_employee = models.CharField(max_length=200)
    day = models.CharField(max_length=50)

    def __str__(self):
        return f"{self.first_employee} {self.second_employee} {self.day}"

    class Meta:
        verbose_name_plural = "matches"
