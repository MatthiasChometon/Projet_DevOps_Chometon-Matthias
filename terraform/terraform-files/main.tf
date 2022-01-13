terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }

  required_version = ">= 0.14.9"
}

data "template_file" "user_data" {
  template = file("ssh-web-app.yaml")
}

provider "aws" {
  profile                 = "default"
  region                  = "us-east-2"
  shared_credentials_file = "credential.txt"
}

resource "aws_instance" "app_server" {
  ami           = "ami-0d97ef13c06b05a19"
  instance_type = "t2.micro"
  user_data = data.template_file.user_data.rendered
  key_name = "deployer-keys-chometon"
  vpc_security_group_ids = [ aws_security_group.sg_default.id ]
  associate_public_ip_address = true

  tags = {
    Name   = "chometon-app"
    groups = "app"
    owner  = "matthias-chometon"
  }
}

resource "aws_key_pair" "deployer" {
  key_name = "deployer-keys-chometon"
  public_key = file("./ssh/id_rsa.pub")
}

resource "aws_security_group" "sg_default" {
  name = "sg_default-chometon"

  ingress {
    from_port = 22
    protocol  = "tcp"
    to_port   = 22
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    protocol  = "-1"
    to_port   = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8080
    protocol  = "tcp"
    to_port   = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }

}

output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.app_server.*.id
}

output "instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.app_server.*.public_ip
}

output userdata {
  value = "\n${data.template_file.user_data.rendered}"
}