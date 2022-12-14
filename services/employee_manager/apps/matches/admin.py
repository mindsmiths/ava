from django.contrib import admin

from .models import Match


class MatchAdmin(admin.ModelAdmin):
    list_display = ('first_employee', 'second_employee', 'day_of_week', 'date')
    ordering = ('-date',)


admin.site.register(Match, MatchAdmin)
