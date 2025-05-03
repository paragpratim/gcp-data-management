output "data-plane-data-landing-zone-bucket" {
  value = google_storage_bucket.bucket_landing_zone.name
    description = "The name of the data plane landing zone bucket"
}

output "data-plane-data-raw-zone-bucket" {
  value = google_storage_bucket.bucket_raw_zone.name
    description = "The name of the data plane raw zone bucket"
}
