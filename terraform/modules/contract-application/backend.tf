terraform {
  backend "gcs" {
    bucket = "data-management-tf-state-e0e9"
    prefix = "contract-application"
  }
}
