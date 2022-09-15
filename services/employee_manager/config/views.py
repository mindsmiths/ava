from django.shortcuts import redirect
from rule_engine.api import RuleEngineAPI
from forge.core.api.events import Signal


def home(request):
    return redirect('/admin/')


def trigger(request, trigger_type):
    to = "CULTURE_MASTER"
    signal = Signal(type="ManualTriggerEvent", from_="dashboard", to=to, triggerType=trigger_type)
    RuleEngineAPI.send_signal(to, signal)
    return redirect('/admin/')