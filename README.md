# GCP Data Management

This repository demonstrates best practices and approaches for managing data and related resources on the Google Cloud Platform (GCP). It serves as a reference for provisioning, organizing, and governing data and infrastructure using modular, extensible tools and workflows. The goal is to showcase how to efficiently manage data assets, automate resource provisioning, and enable scalable data operations on GCP.

## Features

- **Extensible Architecture**: Designed to support additional data management and governance features in the future.
- **Infrastructure as Code with Terraform**: Provision and manage GCP resources such as Google Cloud Storage (GCS) buckets and BigQuery datasets using Terraform scripts. (See [`terraform/README.md`](terraform/README.md) for setup and usage.)
- **Schema Management with Liquibase**: Integrate Liquibase for version-controlled schema changes in BigQuery. (See [`bigquery-liquibase/README.md`](bigquery-liquibase/README.md) for details.)

---

## Getting Started

Before using this repository, complete the following steps:

1. **Install the Google Cloud CLI (gcloud):**
   - Follow the instructions at [Install Google Cloud CLI](https://cloud.google.com/sdk/docs/install).

2. **Install Terraform:**
   - Download and install from the [Terraform website](https://www.terraform.io/downloads.html).

3. **Create Two GCP Projects:**
   - One project to host data resources.
   - One project to host compute resources.
   - This separation helps isolate resources and improve security/governance.

4. **Create Service Account(s):**
   - Create service account(s) with permissions to create and manage resources in both GCP projects.
   - Assign appropriate IAM roles.

See the individual feature folders for setup and usage instructions:

- [Terraform Setup](terraform/README.md)
- [Liquibase Integration](bigquery-liquibase/README.md)

---

## Sample Data

Sample data files are provided in `sample-data/`:
- `sample-data/avro/`: Avro files and schema
- `sample-data/json/`: JSON files
- `sample-data/parquet/`: Parquet files
---

## Roadmap

- Additional data management and governance features will be added in future releases.

## License

See [LICENSE](LICENSE) for license information.
