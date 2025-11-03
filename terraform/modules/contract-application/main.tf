# Create service account
resource "google_service_account" "contract_service_account" {
  account_id   = "svc-cr-data-contract"
  display_name = "Data Contract Service Account"
  description  = "Service account for data contract application"
}

# Assign multiple roles to service account
resource "google_project_iam_member" "contract_service_account_roles" {
  for_each = toset([
    "roles/bigquery.admin",
    "roles/storage.admin",
    "roles/artifactregistry.writer"
  ])

  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.contract_service_account.email}"
}

# Create Artifact Registry repository
resource "google_artifact_registry_repository" "data_management" {
  location      = var.region
  repository_id = "data-management"
  description   = "Docker repository for data management applications"
  format        = "DOCKER"
}