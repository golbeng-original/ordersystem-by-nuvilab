#!/usr/bin/env bash

DOCKER_REPOSITORY_URI="969026361001.dkr.ecr.ap-northeast-2.amazonaws.com"
IMAGE_NAME="ordersystem-backend"

# docker stop
docker-compose -f /home/ec2-user/deployment/docker-compose.yaml down

# docker image rm
IMAGE_ID=$(docker image ls -q -f reference="${DOCKER_REPOSITORY_URI}/${IMAGE_NAME}:latest")
echo "image id = ${IMAGE_ID}"

if [ "${IMAGE_ID}" ]; then
  docker image rm -f ${IMAGE_ID}
fi

sudo rm -rf /home/ec2-user/deployment