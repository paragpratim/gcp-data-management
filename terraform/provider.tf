provider "google" {
  project = var.project_id_data
  region  = var.region
  alias = "data-plane"
}

provider "google" {
  project = var.project_id_compute
  region  = var.region
  alias = "compute-plane"
}

