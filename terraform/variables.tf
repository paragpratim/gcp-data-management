variable "project_id" {
  description = "The GCP project ID"
  type        = string
  default     = ""
}

variable "region" {
  description = "The GCP region"
  type        = string
  default     = ""
}

variable "authorized_members" {
  description = "List of members authorized to invoke the Cloud Run service"
  type        = list(string)
  default     = []
}