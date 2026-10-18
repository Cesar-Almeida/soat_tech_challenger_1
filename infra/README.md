# Infraestrutura local

Os arquivos Terraform provisionam um cluster Kubernetes local com Kind: um control-plane e um worker. O PostgreSQL é provisionado pelos manifestos em `../k8s/postgres.yaml`, com volume persistente.

Execute `terraform init`, `terraform apply` e depois `kubectl apply -k ../k8s`. É necessário criar antes o Secret `oficina-api-secret`, sem versionar valores reais.
