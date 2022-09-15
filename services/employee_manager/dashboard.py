from django.utils.translation import ugettext_lazy as _
from jet.dashboard import modules
from jet.dashboard.dashboard import Dashboard, AppIndexDashboard


class CustomIndexDashboard(Dashboard):
    columns = 3

    def init_with_context(self, context):
        self.available_children.append(modules.LinkList)
        self.children.append(modules.LinkList(
            _('Support'),
            children=[
                {
                    'title': _('Trigger lunch cycle'),
                    'url': '/trigger/lunch_cycle',
                },
                {
                    'title': _('Trigger lunch pairing'),
                    'url': '/trigger/lunch_pairing',
                },
            ],
            column=0,
            order=0
        ))
