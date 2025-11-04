terraform {
  backend "gcs" {
    bucket = var.tf_state_bucket
    prefix = "contract-application"
  }
}
