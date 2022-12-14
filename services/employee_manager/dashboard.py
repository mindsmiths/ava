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
                        'title': _('Trigger onboarding'),
                        'url': '/trigger/onboarding',
                    },
                    {
                        'title': _('Trigger monthly core'),
                        'url': '/trigger/monthly_core',
                    },
                ],
                column=0,
                order=0
            )
        ])
