# from .models import Match
from forge.core.api import on_event
from forge.core.base import BaseService
from forge.conf import settings as forge_settings

import logging
logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class MatchListener(BaseService):
    pass
    # def __init__(self):
    #     super().__init__()

    # @on_event(Match)
    # def on_event(self, match: Match):
    #     logger.info(match)
    #     pass
