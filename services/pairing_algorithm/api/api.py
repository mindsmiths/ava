from typing import List
from enum import Enum

from forge.core.models import ExtendableModel
from forge.core.db import EmittableDataModel


class Days(str, Enum):
    MON = "MON"
    TUE = "TUE"
    WED = "WED"
    THU = "THU"
    FRI = "FRI"


class EmployeeAvailability(ExtendableModel):
    employeeId: str
    availableDays: List[Days]


class Match(ExtendableModel):
    first: str
    second: str
    day: Days


class Matches(ExtendableModel):
    allMatches: List[Match]


class LunchCompatibilityEdge(ExtendableModel):
    first: str
    second: str
    edgeWeight: float


class LunchCompatibilities(EmittableDataModel):
    edges: List[LunchCompatibilityEdge]

    @classmethod
    def get_service_name(cls) -> str:
        return "pairing_algorithm"
