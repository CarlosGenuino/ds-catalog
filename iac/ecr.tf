resource "aws_ecr_repository" "ds-catalog-ecr-api" {
  name = "ds-catalog-api-ci"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    iac: true
  }

}