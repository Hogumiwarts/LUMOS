#!/bin/bash

set -e

docker-compose \
  --project-name infra \
  -f dev/docker-compose.infrastructure.dev.yml \
  --env-file dev/.env.dev up -d

docker-compose \
  --project-name ingress \
  -f dev/docker-compose.ingress.dev.yml \
  --env-file dev/.env.dev up -d
