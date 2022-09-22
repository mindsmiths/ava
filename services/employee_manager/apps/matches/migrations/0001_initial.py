# Generated by Django 3.2.15 on 2022-09-20 11:17

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('employees', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Match',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('date_created', models.DateTimeField(auto_now_add=True)),
                ('date_modified', models.DateTimeField(auto_now=True)),
                ('day', models.CharField(max_length=50)),
                ('date', models.DateTimeField(blank=True, null=True)),
                ('first_employee', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='+', to='employees.employee')),
                ('second_employee', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='+', to='employees.employee')),
            ],
            options={
                'verbose_name_plural': 'matches',
            },
        ),
    ]