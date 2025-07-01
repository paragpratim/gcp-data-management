# Create GCS buckets in data plane
resource "google_storage_bucket" "bucket_landing_zone" {
  name     = var.landing_bucket
  location = var.region
  provider = google.data-plane
  provisioner "local-exec" {
    command = "gsutil cp -r ../sample-data/ gs://${google_storage_bucket.bucket_landing_zone.name}" 
  }
}

resource "google_storage_bucket" "bucket_raw_zone" {
  name     = var.raw_bucket
  location = var.region
  provider = google.data-plane
}

# Create BigQuery datasets in data plane
resource "google_bigquery_dataset" "bq_datasets" {
  for_each = toset(var.bq_datasets)
  dataset_id                  = each.value
  friendly_name               = each.value
  description                 = "${each.value} Dataset"
  location                    = "EU"
  provider                    = google.data-plane

  access {
    role          = "OWNER"
    user_by_email = var.bq_data_owner
  }
}
