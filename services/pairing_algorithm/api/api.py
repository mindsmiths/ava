from typing import List
from typing import Set
from enum import Enum

from forge.core.models import ExtendableModel


class Days(str, Enum):
    MON = "MON"
    TUE = "TUE"
    WED = "WED"
    THU = "THU"
    FRI = "FRI"


class AvaAvailability(ExtendableModel):
    agentId: str
    availableDays: Set[Days]


class Match(ExtendableModel):
    first: str
    second: str
    day: Days


class AllMatches(ExtendableModel):
    allMatches: List[Match]
