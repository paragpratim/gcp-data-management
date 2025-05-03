# Create GCS bucket in data plane
resource "google_storage_bucket" "bucket_landing_zone" {
  name     = "data-landing-zone-${var.project_id_data}"
  location = var.region
  provider = google.data-plane
}

resource "google_storage_bucket" "bucket_raw_zone" {
  name     = "data-raw-zone-${var.project_id_data}"
  location = var.region
  provider = google.data-plane
}
