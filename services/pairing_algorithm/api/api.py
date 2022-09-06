from forge.core.models import ExtendableModel
from typing import List


class Result(ExtendableModel):
    result: List[List]


class AgentAvailableDays(ExtendableModel):
    agentId: str
    availableDays: List[bool]

    def getAvailableDays(self):
        return self.availableDays
