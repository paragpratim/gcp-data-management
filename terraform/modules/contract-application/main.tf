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
  depends_on = [google_service_account.contract_service_account]
}

# Create Artifact Registry repository
resource "google_artifact_registry_repository" "data_management" {
  location      = var.region
  repository_id = "data-management"
  description   = "Docker repository for data management applications"
  format        = "DOCKER"
}

# Enable IAP API
resource "google_project_service" "iap_api" {
  service = "iap.googleapis.com"

  disable_on_destroy = false
}

# Create OAuth2 consent screen (required for IAP)
resource "google_iap_brand" "project_brand" {
  support_email     = var.support_email
  application_title = "Data Contract Manager"
  project           = var.project_id
}

# Create OAuth2 client for IAP
resource "google_iap_client" "contract_app_client" {
  display_name = "Contract App IAP Client"
  brand        = google_iap_brand.project_brand.name
}

# # Data source to get the Cloud Run service (deployed via GitHub Actions)
# data "google_cloud_run_service" "contract_app" {
#   name     = "contract-app"
#   location = var.region

#   depends_on = [google_artifact_registry_repository.data_management]
# }

# # Enable IAP on Cloud Run service
# resource "google_iap_web_iam_binding" "contract_app_access" {
#   project = var.project_id
#   role    = "roles/iap.httpsResourceAccessor"

#   members = var.iap_members

#   depends_on = [
#     google_project_service.iap_api,
#     google_iap_client.contract_app_client
#   ]
# }

# # Configure IAP settings for the Cloud Run service
# resource "google_iap_web_type_compute_iam_binding" "contract_app_iap_invoker" {
#   project = var.project_id
#   role    = "roles/run.invoker"

#   members = [
#     "serviceAccount:service-${data.google_project.project.number}@gcp-sa-iap.iam.gserviceaccount.com"
#   ]
# }

# # Data source to get project number
# data "google_project" "project" {
#   project_id = var.project_id
# }