import logging
from typing import Set
from typing import List

from forge.conf import settings as forge_settings
from forge.core.api import api
from forge.core.base import BaseService

from .api import Matches
from .api import AvaAvailability
from .api import Match

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class PairingAlgorithm(BaseService):

    @api
    def generate_pairs(self,
                       avaAvailabilities: List[AvaAvailability]) -> Matches:
        all_matches: List[Match] = []
        already_added = set()

        for index, first in enumerate(avaAvailabilities):
            if first.agentId in already_added:
                continue
            for second in avaAvailabilities[index + 1:]:
                if second.agentId in already_added:
                    continue
                possible_days = (
                    first.availableDays
                    ).intersection(second.availableDays)
                if possible_days:
                    all_matches.append(Match(
                        first=first.agentId,
                        second=second.agentId,
                        day=possible_days.pop()
                    ))
                    already_added.add(first.agentId)
                    already_added.add(second.agentId)
                    break

        return Matches(allMatches=all_matches)
