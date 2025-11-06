# Create service account
resource "google_service_account" "contract_service_account" {
  account_id   = "svc-cr-data-contract"
  display_name = "Data Contract Service Account"
  description  = "Service account for data contract application"
}

# Enable required APIs
resource "google_project_service" "required_apis" {
  for_each = toset([
    "bigquery.googleapis.com",
    "firestore.googleapis.com",
    "logging.googleapis.com",
    "artifactregistry.googleapis.com",
    "run.googleapis.com",
    "iap.googleapis.com",
    "storage.googleapis.com"
  ])

  service            = each.value
  disable_on_destroy = false
}

# Assign multiple roles to service account
resource "google_project_iam_member" "contract_service_account_roles" {
  for_each = toset([
    "roles/bigquery.admin",
    "roles/storage.admin",
    "roles/artifactregistry.writer",
    "roles/datastore.owner"
  ])

  project    = var.project_id
  role       = each.value
  member     = "serviceAccount:${google_service_account.contract_service_account.email}"
  depends_on = [google_service_account.contract_service_account]
}

# Create Artifact Registry repository
resource "google_artifact_registry_repository" "data_management" {
  location      = var.region
  repository_id = "data-management"
  description   = "Docker repository for data management applications"
  format        = "DOCKER"

  depends_on = [google_project_service.required_apis]
}

# Create GCS bucket for Liquibase logs
resource "google_storage_bucket" "liquibase_log" {
  name     = "liquibase-log-${var.project_id}"
  location = var.region

  versioning {
    enabled = true
  }

  # Set uniform bucket-level access
  uniform_bucket_level_access = true

  depends_on = [google_project_service.required_apis]
}

# # Restrict Cloud Run service access to specific users
# resource "google_cloud_run_service_iam_binding" "contract_app_access" {
#   location = var.region
#   service  = "contract-app"
#   role     = "roles/run.invoker"

#   members = var.authorized_members

#   depends_on = [google_project_service.required_apis]
# }