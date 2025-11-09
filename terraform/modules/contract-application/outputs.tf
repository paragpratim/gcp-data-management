output "project_id" {
  description = "The GCP project ID"
  value       = var.project_id
}

output "region" {
  description = "The GCP region"
  value       = var.region
}

output "service_account_email" {
  description = "The service account email"
  value       = google_service_account.contract_service_account.email
}

output "artifact_registry_repository" {
  description = "The Artifact Registry repository name"
  value       = google_artifact_registry_repository.data_management.name
}

output "liquibase_log_bucket" {
  description = "The GCS bucket for Liquibase logs"
  value       = google_storage_bucket.liquibase_log.name
}

output "liquibase_log_bucket_url" {
  description = "The GCS bucket URL for Liquibase logs"
  value       = google_storage_bucket.liquibase_log.url
}

output "bigquery_datasets" {
  description = "Map of created BigQuery dataset IDs and their details"
  value = {
    for dataset_id, dataset in google_bigquery_dataset.datasets :
    dataset_id => {
      id            = dataset.dataset_id
      project       = dataset.project
      location      = dataset.location
      self_link     = dataset.self_link
      friendly_name = dataset.friendly_name
    }
  }
}

output "dataset_ids" {
  description = "List of created BigQuery dataset IDs"
  value       = [for dataset in google_bigquery_dataset.datasets : dataset.dataset_id]
}
