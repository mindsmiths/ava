from typing import Dict
from typing import List

from forge.core.api.base import BaseAPI
from forge.core.api.callbacks import Future
from forge.core.api.decorators import api_interface

from services import pairing_algorithm
from .api import EmployeeAvailability
from .api import Matches


@api_interface
class PairingAlgorithmAPI(BaseAPI):
    service_id = pairing_algorithm.SERVICE_ID

    @staticmethod
    async def generate_pairs(employeeAvailabilities: List[EmployeeAvailability],
                             employeeConnectionStrengths: Dict[str, Dict[str, float]]) -> Future[Matches]:
        """Generates pairs of people that should go on a lunch together"""
