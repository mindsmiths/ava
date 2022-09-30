from django.utils.translation import ugettext_lazy as _
from jet.dashboard import modules
from jet.dashboard.dashboard import Dashboard

from apps import dashboard_modules


class CustomIndexDashboard(Dashboard):
    columns = 3

    def init_with_context(self, context):
        self.available_children.extend([modules.LinkList,
                                        dashboard_modules.MatchesModule])
        self.children.extend([
            dashboard_modules.MatchesModule(),
            modules.LinkList(
                _('Actions'),
                children=[
                    {
                        'title': _('Trigger lunch cycle'),
                        'url': '/trigger/lunch_cycle',
                    },
                    {
                        'title': _('Trigger lunch pairing'),
                        'url': '/trigger/lunch_pairing',
                    },
                    {
                        'title': _('Trigger familiarity quiz'),
                        'url': '/trigger/familiarity_quiz',
                    },
                    {
                        'title': _('Trigger statistics email'),
                        'url': '/trigger/statistics_email',
                    },
                    {
                        'title': _('Trigger ice breaker'),
                        'url': '/trigger/ice_breaker',
                    },
                ],
                column=0,
                order=0
            )
        ])
