from django.utils.translation import ugettext_lazy as _
from jet.dashboard import modules
from jet.dashboard.dashboard import Dashboard, AppIndexDashboard


class CustomIndexDashboard(Dashboard):
    columns = 3

    def init_with_context(self, context):
        self.available_children.append(modules.LinkList)
        self.children.append(modules.LinkList(
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
            ],
            column=0,
            order=0
        ))
