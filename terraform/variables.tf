variable "project_id_compute" {
  description = "The project ID for the compute plane."
  type        = string 
}

variable "project_id_data" {
  description = "The project ID for the data plane."
  type        = string 
}
variable "region" {
  description = "The region for the resources."
  type        = string
}

variable "bq_dataset_user_data" {
  description = "The BigQuery dataset for user data."
  type        = string
}

variable "bq_dataset_inventory_data" {
  description = "The BigQuery dataset for inventory data."
  type        = string
}

variable "bq_dataset_changelog" {
  description = "The BigQuery dataset for liquibase changelog."
  type        = string
}

variable "bq_data_owner" {
  description = "The owners of the BigQuery datasets."
  type        = string
}