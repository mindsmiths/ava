import logging
from typing import List

from forge.conf import settings as forge_settings
from forge.core.api import api
from forge.core.base import BaseService

from .api import Result
from .api import AgentAvailableDays
from .api import FinalPairWithDays

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class PairingAlgorithm(BaseService):

    @api
    def generate_pairs(self,
                       availabilityList: List[AgentAvailableDays]) -> Result:
        finalPairs: List[FinalPairWithDays] = []
        alreadyAdded = set()
        n = len(availabilityList)
        found = False

        for i in range(n):
            if availabilityList[i].agentId in alreadyAdded:
                continue
            for j in range(i+1, n):
                if availabilityList[j].agentId in alreadyAdded:
                    continue
                for k in range(5):
                    if (availabilityList[i].availableDays[k] == 1 and
                            availabilityList[j].availableDays[k] == 1):
                        alreadyAdded.add(availabilityList[i].agentId)
                        alreadyAdded.add(availabilityList[j].agentId)
                        finalPairs.append(FinalPairWithDays(
                            person1=availabilityList[i].agentId,
                            person2=availabilityList[j].agentId,
                            day=k))
                        found = True
                        break
                if found:
                    found = False
                    break

        return Result(result=finalPairs)
