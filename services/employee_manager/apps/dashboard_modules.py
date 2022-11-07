from jet.dashboard.modules import DashboardModule

from matches.models import Match
from datetime import timedelta
from forge.utils.datetime import get_utc_datetime


class MatchesModule(DashboardModule):
    title = 'Matches'
    template = 'dashboard_modules/matches.html'
    column = 0
    weekCount = 4

    def init_with_context(self, context):
        def next_saturday(day):
            weekday = 5  # saturday
            days_ahead = weekday - day.weekday()
            if days_ahead <= 0:
                days_ahead += 7
            return day + timedelta(days_ahead + 7)

        by_week = []

        week_end = next_saturday(get_utc_datetime())
        week_start = week_end - timedelta(days=7)

        for week in range(0, self.weekCount):
            weekly = Match.objects.all().filter(date__gte=week_start) \
                                        .filter(date__lte=week_end)
            by_week.append(weekly)

            week_end = week_start
            week_start -= timedelta(days=7)

        self.children = by_week
