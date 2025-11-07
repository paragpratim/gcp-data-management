output "project_id" {
  description = "The GCP project ID"
  value       = module.contract_application.project_id
}

output "region" {
  description = "The GCP region"
  value       = module.contract_application.region
}

output "service_account_email" {
  description = "The service account email"
  value       = module.contract_application.service_account_email
}

output "artifact_registry_repository" {
  description = "The Artifact Registry repository name"
  value       = module.contract_application.artifact_registry_repository
}
