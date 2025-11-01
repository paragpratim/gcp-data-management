# Create service account
resource "google_service_account" "contract_service_account" {
  account_id   = "svc-cr-data-contract"
  display_name = "Data Contract Service Account"
  description  = "Service account for data contract application"
}

# Assign BigQuery Admin role
resource "google_project_iam_member" "bigquery_admin" {
  project = var.project_id
  role    = "roles/bigquery.admin"
  member  = "serviceAccount:${google_service_account.contract_service_account.email}"
}

# Assign Storage Admin role
resource "google_project_iam_member" "storage_admin" {
  project = var.project_id
  role    = "roles/storage.admin"
  member  = "serviceAccount:${google_service_account.contract_service_account.email}"
}

# Assign Artifact Registry Writer role
resource "google_project_iam_member" "artifact_registry_writer" {
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.contract_service_account.email}"
}

# Create Artifact Registry repository
resource "google_artifact_registry_repository" "data_management" {
  location      = var.region
  repository_id = "data-management"
  description   = "Docker repository for data management applications"
  format        = "DOCKER"
}