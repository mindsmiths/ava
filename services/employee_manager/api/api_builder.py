from forge.conf import settings
from forge.core.api import api_interface, BaseAPI
from datetime import datetime


@api_interface
class EmployeeManagerAPI(BaseAPI):
    service_name = "employee_manager"

    @staticmethod
    def create_match(firstEmployeeId: str, secondEmployeeId: str,
                     date: datetime, dayOfWeek: str) -> None:
        """"""
