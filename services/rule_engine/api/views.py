from forge.core.db import EmittableDataModel
from datetime import datetime


class Match(EmittableDataModel):
    firstEmployeeId: str
    secondEmployeeId: str
    dayOfWeek: str
    date: datetime

    @classmethod
    def get_service_name(cls) -> str:
        return "rule_engine"
