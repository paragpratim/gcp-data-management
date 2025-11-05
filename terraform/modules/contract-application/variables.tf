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

variable "support_email" {
  description = "Support email for OAuth consent screen"
  type        = string
  default     = ""
}

variable "iap_members" {
  description = "List of members who can access the application through IAP"
  type        = list(string)
  default     = []
}

variable "authorized_members" {
  description = "List of members authorized to invoke the Cloud Run service"
  type        = list(string)
  default     = []
}