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

output "iap_client_id" {
  description = "IAP OAuth2 client ID"
  value       = google_iap_client.contract_app_client.client_id
  sensitive   = true
}

output "iap_client_secret" {
  description = "IAP OAuth2 client secret"
  value       = google_iap_client.contract_app_client.secret
  sensitive   = true
}