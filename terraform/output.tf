output "data-plane-data-landing-zone-bucket" {
  value = google_storage_bucket.bucket_landing_zone.name
    description = "The name of the data plane landing zone bucket"
}

output "data-plane-data-raw-zone-bucket" {
  value = google_storage_bucket.bucket_raw_zone.name
    description = "The name of the data plane raw zone bucket"
}

output "data-plane-bq-dataset-user-data" {
  value = google_bigquery_dataset.bq_dataset_user_data.dataset_id
    description = "The name of the data plane BigQuery dataset for user data"
}

output "data-plane-bq-dataset-inventory-data" {
  value = google_bigquery_dataset.bq_dataset_inventory_data.dataset_id
    description = "The friendly name of the data plane BigQuery dataset for inventory data"
}

output "data-plane-bq-dataset-changelog" {
  value = google_bigquery_dataset.bq_dataset_changelog.dataset_id
    description = "The friendly name of the data plane BigQuery dataset for changelog"
}