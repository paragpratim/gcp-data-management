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

variable "bq_datasets" {
  description = "The list of BigQuery datasets to create."
  type        = list(string)
}

variable "bq_data_owner" {
  description = "The owners of the BigQuery datasets."
  type        = string
}