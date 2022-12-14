from django.shortcuts import redirect
from django.contrib import messages

from forge.core.api.events import Signal
from forge.messaging.utils import get_input_topic_name
from rule_engine.api import RuleEngineAPI

from employees.models import Employee


def home(request):
    return redirect('/admin/')


def trigger(request, trigger_type):
    to = "CULTURE_MASTER"
    signal = Signal(type="ManualTriggerEvent", from_="employee_manager", to=to,
                    triggerType=trigger_type)
    RuleEngineAPI.send_signal(to, signal)

    for employee in Employee.objects.filter(active=True):
        Signal(
            type="ManualTriggerEvent", from_="employee_manager",
            to=get_input_topic_name("rule_engine"), triggerType=trigger_type,
            id=str(employee.id)
        ).emit()

    messages.success(request,
                     f'Action "{trigger_type}" trigger sent succesfully!')
    return redirect('/admin/')
