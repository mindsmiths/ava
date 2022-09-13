import logging

from forge.conf import settings as forge_settings
from forge.core.base import BaseService

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class Service(BaseService):
    ...  # here you can create an API for other services, or maybe listen to events
