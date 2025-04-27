#!/usr/bin/env bash

DOCKER_REPOSITORY_URI="969026361001.dkr.ecr.ap-northeast-2.amazonaws.com"
IMAGE_NAME="ordersystem-backend"
REGION="ap-northeast-2"

aws ecr get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${DOCKER_REPOSITORY_URI}/${IMAGE_NAME}

docker-compose -f /home/ec2-user/deployment/docker-compose.yaml up -d
