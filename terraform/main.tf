# Create GCS bucket in data plane
resource "google_storage_bucket" "bucket_landing_zone" {
  name     = "data-landing-zone-${var.project_id_data}"
  location = var.region
  provider = google.data-plane
  provisioner "local-exec" {
    command = "gsutil cp -r ../sample-data/ gs://${google_storage_bucket.bucket_landing_zone.name}" 
  }
}

resource "google_storage_bucket" "bucket_raw_zone" {
  name     = "data-raw-zone-${var.project_id_data}"
  location = var.region
  provider = google.data-plane
}

resource "google_bigquery_dataset" "bq_dataset_user_data" {
  dataset_id                  = var.bq_dataset_user_data
  friendly_name               = "User Data"
  description                 = "User Data Dataset"
  location                    = "EU"
  provider                    = google.data-plane

  access {
    role          = "OWNER"
    user_by_email = var.bq_data_owner
  }
}

resource "google_bigquery_dataset" "bq_dataset_inventory_data" {
  dataset_id                  = var.bq_dataset_inventory_data
  friendly_name               = "Inventory Data"
  description                 = "Inventory Dataset"
  location                    = "EU"
  provider                    = google.data-plane


  access {
    role          = "OWNER"
    user_by_email = var.bq_data_owner
  }
}

resource "google_bigquery_dataset" "bq_dataset_changelog" {
  dataset_id                  = var.bq_dataset_changelog
  friendly_name               = "Liquibase Changelog"
  description                 = "Liquibase Changelog Dataset"
  location                    = "EU"
  provider                    = google.data-plane


  access {
    role          = "OWNER"
    user_by_email = var.bq_data_owner
  }
}