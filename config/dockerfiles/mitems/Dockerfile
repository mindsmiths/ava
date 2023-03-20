
FROM python:3.8-buster

# Pip dependencies
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "forge-sdk~=5.0.0b0" "forge-cli~=5.0.0b0" "cecs==4.0.0a1"  "django~=3.2.0" "django-environ~=0.8.0" "django-debug-toolbar~=3.4"  "django-jet-reboot==1.3.1" "django-json-widget~=1.1.0" "django-split-json-widget==1.16"  "django-solo==1.2.0" "django-tinymce~=3.4.0" "psycopg2-binary~=2.9.0" "requests==2.27.1"  "jsonfield~=3.1.0" "beautifulsoup4~=4.11.0" "gunicorn~=20.1.0" "python-slugify~=5.0.0"

RUN mkdir -p /app/services

COPY __init__.py /app
WORKDIR /app

COPY services/__init__.py /app/services
COPY services/mitems /app/services/mitems

WORKDIR /app/services/mitems

CMD ["/bin/bash", "run.sh"]

