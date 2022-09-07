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
    def generate_pairs(self, pairData: List[AgentAvailableDays]) -> Result:
        finalPairs: List[FinalPairWithDays] = []
        alreadyAdded = set()
        n = len(pairData)

        for i in range(n):
            if pairData[i].agentId in alreadyAdded:
                continue
            for j in range(i+1, n):
                if pairData[j].agentId in alreadyAdded:
                    continue
                for k in range(5):
                    if (pairData[i].availableDays[k] == 1 and
                            pairData[j].availableDays[k] == 1):
                        alreadyAdded.add(pairData[i].agentId)
                        alreadyAdded.add(pairData[j].agentId)
                        finalPairs.append(FinalPairWithDays(
                            person1=pairData[i].agentId,
                            person2=pairData[j].agentId,
                            day=k))
                        break

        # temp "algorithm"
#        for i in range(0, n, 2):
#            pair = FinalPairWithDays(person1=pairData[i],
#                                     person2=pairData[i+1],
#                                     day=0)
#            finalPairs.append(pair)

        return Result(result=finalPairs)
