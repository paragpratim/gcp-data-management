output "data-plane-data-landing-zone-bucket" {
  value = google_storage_bucket.bucket_landing_zone.name
    description = "The name of the data plane landing zone bucket"
}

output "data-plane-data-raw-zone-bucket" {
  value = google_storage_bucket.bucket_raw_zone.name
    description = "The name of the data plane raw zone bucket"
}

output "data-plane-bq-datasets" {
  value = [for ds in google_bigquery_dataset.bq_datasets : ds.dataset_id]
  description = "The list of BigQuery datasets created in the data plane"
}
