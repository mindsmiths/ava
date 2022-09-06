import logging
from typing import List

from forge.conf import settings as forge_settings
from forge.core.api import api
from forge.core.base import BaseService

from .api import Result
from .api import AgentAvailableDays

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class PairingAlgorithm(BaseService):

    @api
    def generate_pairs(self, pairData: List[AgentAvailableDays]) -> Result:

        tempList = pairData
        finalPairs = List[List]

        AgentAvailableDays.ge

        while(len(tempList)):
            for indPerson, person in enumerate(pairData):
                for indDay, day in enumerate(person.availableDays):


#        for i in range(0, len(pairData), 2):
#           pair = [pairData[i], pairData[i+1]]
#           finalPairs.append(pair)

        return Result(success=finalPairs)
