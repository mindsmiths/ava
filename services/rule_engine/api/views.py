from forge.core.db import DBView
from datetime import datetime


class Match(DBView):
    firstEmployeeId: str
    secondEmployeeId: str
    dayOfWeek: str
    date: datetime

    @classmethod
    def get_service_name(cls) -> str:
        return "rule_engine"
