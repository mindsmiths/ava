from typing import List

from forge.core.api import BaseAPI, Future
from forge.core.api import api_interface

from services import pairing_algorithm
from .api import Matches
from .api import EmployeeAvailability


@api_interface
class PairingAlgorithmAPI(BaseAPI):
    service_name = pairing_algorithm.SERVICE_NAME

    @staticmethod
    def generate_pairs(self,
                       employeeAvailabilities: List[EmployeeAvailability]
                       ) -> Future[Matches]:
        """Generates pairs of people that should go on a lunch together""" 

