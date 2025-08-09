# Get the project data for the data plane
data "google_project" "data_plane" {
  project_id = var.project_id_data
}

# Pub/Sub topic for data ingestion
resource "google_pubsub_topic" "data_ingestion_topic" {
  name = var.ingestion_pubsub_topic
  provider = google.compute-plane
}

# Subscription to the data ingestion topic
resource "google_pubsub_subscription" "data_ingestion_sub" {
  name  = var.ingestion_pubsub_subscription
  topic = google_pubsub_topic.data_ingestion_topic.id
  provider = google.compute-plane
}

# IAM binding to allow the GCS service agent from the data-plane project to publish to the Pub/Sub topic in the compute-plane project
resource "google_pubsub_topic_iam_member" "allow_gcs_sa_publish" {
  topic   = google_pubsub_topic.data_ingestion_topic.id
  role    = "roles/pubsub.publisher"
  member  = "serviceAccount:service-${data.google_project.data_plane.number}@gs-project-accounts.iam.gserviceaccount.com"
  provider = google.compute-plane
  depends_on = [ google_pubsub_topic.data_ingestion_topic ] # Ensure the topic is created before the IAM binding
}

# GCS bucket notification for Pub/Sub
resource "google_storage_notification" "landing_bucket_to_pubsub" {
  bucket         = var.landing_bucket
  topic          = google_pubsub_topic.data_ingestion_topic.id
  event_types    = ["OBJECT_FINALIZE"]
  payload_format = "JSON_API_V1"
  provider = google.data-plane
  depends_on = [ google_pubsub_topic_iam_member.allow_gcs_sa_publish ] # Ensure the IAM binding is created before the notification
}

# Service account for running Dataflow jobs in the compute plane project
resource "google_service_account" "dataflow_runner" {
  account_id   = "dataflow-runner"
  display_name = "Dataflow Runner Service Account"
  description  = "Service account for running Dataflow jobs on the compute plane project."
  provider = google.compute-plane
}

# Combined IAM roles for Dataflow runner service account
resource "google_project_iam_member" "dataflow_runner_combined" {
  for_each = toset([
    "roles/dataflow.worker",
    "roles/storage.objectAdmin",
    "roles/bigquery.user",
    "roles/logging.logWriter"
  ])
  project = var.project_id_compute
  role    = each.value
  member  = "serviceAccount:${google_service_account.dataflow_runner.email}"
  provider = google.compute-plane
}

# Storage bucket in the compute plane project for Dataflow backend
resource "google_storage_bucket" "dataflow_backend" {
  name     = var.dataflow_backend_bucket_name
  location = var.region
  force_destroy = true
  uniform_bucket_level_access = true
  provider = google.compute-plane
}



