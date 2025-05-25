# GCP Data Management Demo

This repository provides a sample setup for managing data infrastructure on Google Cloud Platform (GCP) using Terraform and demonstrates schema management with Liquibase for BigQuery.

## Contents

- **Terraform scripts** for provisioning:
  - Google Cloud Storage (GCS) buckets
  - BigQuery datasets for user and inventory data
- **Sample data** in Avro, JSON, and Parquet formats
- **Liquibase scripts** for managing BigQuery schema changes

---

## Prerequisites

- [Terraform](https://www.terraform.io/downloads.html) installed
- [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installed and authenticated
- [Liquibase](https://www.liquibase.org/download) installed
- Two GCP projects created:
  - One for the data plane
  - One for the compute plane
- Service account(s) with permissions to create GCS buckets and BigQuery datasets

---

## Setup

### 1. Configure Terraform Variables

Edit [`terraform/terraform.tfvars`](terraform/terraform.tfvars) and provide values for:

- `project_id_data` (GCP project for data plane)
- `project_id_compute` (GCP project for compute plane)
- `region` (GCP region)
- `bq_dataset_user_data`, `bq_dataset_inventory_data`, `bq_dataset_changelog` (BigQuery dataset names)
- `bq_data_owner` (email of dataset owner)

See [`terraform/variables.tf`](terraform/variables.tf) for all variables.

### 2. Initialize and Apply Terraform

```sh
cd terraform
terraform init
terraform apply
```

This will create the required GCS buckets and BigQuery datasets. Sample data from [`sample-data/`](sample-data/) will be uploaded to the landing zone bucket.

---

## Liquibase with BigQuery

Liquibase scripts are provided in [`bigquery-liquibase/`](bigquery-liquibase/). These demonstrate schema management for BigQuery.

### Environment Setup

Set the following environment variable before running Liquibase:

```sh
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/your/service-account-key.json"
export LIQUIBASE_COMMAND_URL="jdbc:bigquery://https://googleapis.com/bigquery/v2:443;ProjectId=<your GCP Project ID>;OAuthType=3;DefaultDataset=<changelog dataset>;"
```

### Running Liquibase

1. Edit [`bigquery-liquibase/liquibase.properties`](bigquery-liquibase/liquibase.properties) to match your BigQuery connection details.
2. Run Liquibase commands, for example:

```sh
cd bigquery-liquibase
liquibase --changeLogFile=rootchangelog.yaml update
```

---

## Sample Data

Sample data files are provided in [`sample-data/`](sample-data/):

- [`sample-data/avro/`](sample-data/avro/): Avro files and schema
- [`sample-data/json/`](sample-data/json/): JSON files
- [`sample-data/parquet/`](sample-data/parquet/): Parquet files

See [`sample-data/avro/README.txt`](sample-data/avro/README.txt) for details.

---

## License

See [LICENSE](LICENSE) for license information.
