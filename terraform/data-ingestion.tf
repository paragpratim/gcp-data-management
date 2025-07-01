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



