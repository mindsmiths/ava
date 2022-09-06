from forge.core.api import BaseAPI, Future
from forge.core.api import api_interface

from services import pairing_algorithm
from .api import Result


@api_interface
class PairingAlgorithmAPI(BaseAPI):
    service_name = pairing_algorithm.SERVICE_NAME

    @staticmethod
    def do_something(someData: str) -> Future[Result]:  # TODO: change this
        """Does something"""  # TODO: write some documentation
