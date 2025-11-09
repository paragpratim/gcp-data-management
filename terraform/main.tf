# Call the contract-application module
module "contract_application" {
  source = "./modules/contract-application"

  project_id            = var.project_id
  region                = var.region
  datastore_database_id = var.datastore_database_id
  my_domain             = var.my_domain

  bigquery_datasets = var.bigquery_datasets
}