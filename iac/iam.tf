resource "aws_iam_openid_connect_provider" "oidc-git" {
  client_id_list  = [
    "sts.amazonaws.com"
  ]

  thumbprint_list = [
    "1b511abead59c6ce207077c0bf0e0043b1382612"
  ]

  url             = "https://token.actions.githubusercontent.com"
}

resource "aws_iam_role" "ecr-role" {
  name = "ecr-role"
  assume_role_policy = jsonencode({
    Statement = [
      {
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
            "token.actions.githubusercontent.com:sub" = "repo:eusouodaniel/rocketseat.ci.api:ref:refs/heads/main"
          }
        }
        Effect = "Allow"
        Principal = {
          Federated = "arn:aws:iam::403429280851:oidc-provider/token.actions.githubusercontent.com"
        }
      }
    ]
    Version = "2012-10-17"
  })
  tags = {
    IAC = "True"
  }
}