provider "kind" {}

resource "kind_cluster" "oficina" {
  name            = var.cluster_name
  wait_for_ready  = true
  kubeconfig_path = pathexpand("~/.kube/${var.cluster_name}-config")

  kind_config {
    kind       = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"
    node { role = "control-plane" }
    node { role = "worker" }
  }
}
