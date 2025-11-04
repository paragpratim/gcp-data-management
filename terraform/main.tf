# Call the contract-application module
module "contract_application" {
  source = "./modules/contract-application"

  project_id    = var.project_id
  region        = var.region
  support_email = var.support_email
  iap_members   = var.iap_members
}