from typing import List
from typing import Dict
from forge.core.api.callbacks import Future
from forge.core.api.decorators import api_interface
from forge.core.api.base import BaseAPI

from services import pairing_algorithm
from .api import Matches
from .api import EmployeeAvailability


@api_interface
class PairingAlgorithmAPI(BaseAPI):
    service_id = pairing_algorithm.SERVICE_ID

    @staticmethod
    async def generate_pairs(
            self,
            employeeAvailabilities: List[EmployeeAvailability],
            employeeConnectionStrengths: Dict[str, Dict[str, float]]) -> Future[Matches]:
        """Generates pairs of people that should go on a lunch together"""
