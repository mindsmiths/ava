from jet.dashboard.modules import DashboardModule

from matches.models import Match


class MatchesModule(DashboardModule):
    title = 'Matches'
    template = 'dashboard_modules/matches.html'
    column = 0

    def init_with_context(self, context):
        self.children = Match.objects.all()
