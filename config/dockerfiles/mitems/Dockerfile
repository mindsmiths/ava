
FROM python:3.8-buster

# Pip dependencies
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "forge-sdk~=4.0.0a7"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "forge-cli~=4.0.0a6"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "cecs==4.0.0a1"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django~=3.2.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-environ~=0.8.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-debug-toolbar~=3.4"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-jet-reboot==1.3.1"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-json-widget~=1.1.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-split-json-widget==1.16"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-solo==1.2.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "django-tinymce~=3.4.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "psycopg2-binary~=2.9.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "requests==2.27.1"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "jsonfield~=3.1.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "beautifulsoup4~=4.11.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "gunicorn~=20.1.0"
RUN pip install -U --index-url https://{{NEXUS_PYTHON_READ_USERNAME}}:{{NEXUS_PYTHON_READ_PASSWORD}}@nexus.vingd.net/repository/pypi-group/simple "python-slugify~=5.0.0"

RUN mkdir -p /app/services

COPY __init__.py /app
WORKDIR /app

COPY services/__init__.py /app/services
COPY services/mitems /app/services/mitems

WORKDIR /app/services/mitems

CMD ["/bin/bash", "run.sh"]

