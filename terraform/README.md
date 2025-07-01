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
---

For more details on the overall project, see the main [README](../README.md).
