# Generic Project Setup Variables
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

variable "raw_bucket" {
  description = "The name of the raw bucket for data storage."
  type        = string
}

variable "landing_bucket" {
  description = "The name of the landing bucket for data ingestion."
  type        = string
}

# BigQuery Datasets Variables
variable "bq_datasets" {
  description = "The list of BigQuery datasets to create."
  type        = list(string)
}

variable "bq_data_owner" {
  description = "The owners of the BigQuery datasets."
  type        = string
}

# PubSub Topics for Data Ingestion
variable "ingestion_pubsub_topic" {
  description = "The name of the PubSub topic for ingestion."
  type        = string
}

# PubSub Subscriptions for Data Ingestion
variable "ingestion_pubsub_subscription" {
  description = "The name of the PubSub subscription for ingestion."
  type        = string
}

variable "dataflow_backend_bucket_name" {
  description = "The name of the GCS bucket to be used as the Dataflow backend in the compute plane project."
  type        = string
}

