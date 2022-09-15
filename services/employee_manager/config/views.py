from django.shortcuts import redirect
from rule_engine.api import RuleEngineAPI
from forge.core.api.events import Signal
from django.contrib import messages


def home(request):
    return redirect('/admin/')


def trigger(request, trigger_type):
    to = "CULTURE_MASTER"
    signal = Signal(type="ManualTriggerEvent", from_="dashboard", to=to,
                    triggerType=trigger_type)
    RuleEngineAPI.send_signal(to, signal)
    messages.success(request,
                     f'Action "{trigger_type}" trigger sent succesfully!')
    return redirect('/admin/')
