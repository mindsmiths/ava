from django.shortcuts import redirect
from django.contrib import messages

from forge.core.api.base import Event
from services.employee_manager.api.reply import ManualTriggerEvent
from employees.models import Employee


def home(request):
    return redirect('/admin/')


def trigger(request, trigger_type: str):
    ManualTriggerEvent(triggerType=trigger_type).emit()
    messages.success(request,
                     f'Action "{trigger_type}" trigger sent succesfully!')
    return redirect('/admin/')
