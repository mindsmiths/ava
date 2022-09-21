import logging

from forge.conf import settings as forge_settings
from forge.core.base import BaseService
from forge.core.api import on_event, api

from matches.models import Match
from employees.models import Employee

from datetime import datetime

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class EmployeeManagerListener(BaseService):

    @api
    def create_match(self, firstEmployeeId: str, secondEmployeeId: str,
                     date: datetime, dayOfWeek: str):
        logger.info("creating match in service.py")

        firstEmployee = Employee.objects.get(id=firstEmployeeId)
        secondEmployee = Employee.objects.get(id=secondEmployeeId)

        match = Match.objects.create(
            first_employee=firstEmployee,
            second_employee=secondEmployee,
            day_of_week=dayOfWeek,
            date=date
        )
        match.save()
