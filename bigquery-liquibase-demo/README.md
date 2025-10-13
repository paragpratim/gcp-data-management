# Liquibase for BigQuery

This folder contains Liquibase scripts and configuration for managing BigQuery schema changes as part of the GCP Data Management project.

## Prerequisites

- [Liquibase](https://www.liquibase.org/download) installed
- [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installed and authenticated
- Service account with permissions to manage BigQuery resources
- BigQuery datasets already provisioned (see project Terraform setup)

---

## Environment Setup

Set the following environment variables before running Liquibase:

```sh
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/service-account-key.json"
export LIQUIBASE_COMMAND_URL="jdbc:bigquery://https://googleapis.com/bigquery/v2:443;ProjectId=<your GCP Project ID>;OAuthType=3;DefaultDataset=<changelog dataset>;"
```

---

## Usage

1. Edit `liquibase.properties` to match your BigQuery connection details.
2. Run Liquibase commands, for example:

```sh
cd bigquery-liquibase
liquibase --changeLogFile=rootchangelog.yaml status
liquibase --changeLogFile=rootchangelog.yaml update
liquibase --tag=a_tag
```

---

### Liquibase Live Demo

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/Zl-y9RUQ1pI/0.jpg)](https://www.youtube.com/watch?v=Zl-y9RUQ1pI "Liquibase with Bigquery")

---

## Resources

- [Liquibase Official Documentation](https://docs.liquibase.com/)

---

For overall project details, see the main [README](../README.md).
