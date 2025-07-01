# Terraform Setup for GCP Data Management

This document provides instructions for provisioning GCP resources using Terraform as part of the GCP Data Management project.

## Prerequisites

- [Terraform](https://www.terraform.io/downloads.html) installed
- [Google Cloud SDK](https://cloud.google.com/sdk/docs/install) installed and authenticated
- GCP projects for data and compute purposes
- Service account(s) with permissions to create required resources on GCP projects

## Setup Steps

1. **Configure Terraform Variables**
   - Edit `terraform.tfvars` to provide your GCP project IDs, region, dataset names, and data owner email.
   - See `variables.tf` for all variables.

2. **Initialize and Apply Terraform**
   ```sh
   cd terraform
   terraform init
   terraform apply
   ```
   This will provision the required GCP resources. Sample data from `../sample-data/` will be uploaded to the landing area bucket.

## Infrastructure Provisioned by Terraform

### Infrastructure for General Project Setup
- Buckets for storing raw data
- Landing area bucket for data ingestion workflows
- BigQuery datasets for user data and inventory data

### BigQuery Dataset for Liquibase Demo
- Dedicated BigQuery dataset for Liquibase changelogs and schema management

### Pub/Sub and Notifications for Data Ingestion
- Pub/Sub topic for data ingestion events
- Pub/Sub subscription to process ingestion events
- GCS bucket notification: triggers Pub/Sub messages on OBJECT_FINALIZE events in the landing area bucket

### IAM Roles and Permissions
- Grants required permissions for service accounts
- Allows the GCS service agent from the data-plane project to publish messages to the Pub/Sub topic in the compute-plane project
---

For more details on the overall project, see the main [README](../README.md).
