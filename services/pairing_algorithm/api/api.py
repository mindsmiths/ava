from forge.core.models import ExtendableModel
from typing import List


class AgentAvailableDays(ExtendableModel):
    agentId: str
    availableDays: List[bool]


class FinalPairWithDays(ExtendableModel):
    person1: str
    person2: str
    day: int


class Result(ExtendableModel):
    result: List[FinalPairWithDays]
