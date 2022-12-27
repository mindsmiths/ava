import logging

from asgiref.sync import sync_to_async

from forge.conf import settings as forge_settings
from forge.core.base import BaseService
from forge.core.api.decorators import on_change

from matches.models import Match
from services.rule_engine.api.views import Match as MatchView
from employees.models import Employee

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class EmployeeManagerListener(BaseService):

    @on_change(MatchView)
    
    async def create_match(self, match: MatchView):
        def inner():
            firstEmployee = Employee.objects.get(id=match.firstEmployeeId)
            secondEmployee = Employee.objects.get(id=match.secondEmployeeId)

            new_match = Match(
                first_employee=firstEmployee,
                second_employee=secondEmployee,
                day_of_week=match.dayOfWeek,
                date=match.date
                )
            new_match.save()

        await sync_to_async(inner)()
