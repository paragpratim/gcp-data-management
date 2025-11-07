# Call the contract-application module
module "contract_application" {
  source = "./modules/contract-application"

  project_id = var.project_id
  region     = var.region
}