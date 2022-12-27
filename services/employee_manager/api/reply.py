from forge.core.api.base import Event


class ManualTriggerEvent(Event):
    triggerType: str
