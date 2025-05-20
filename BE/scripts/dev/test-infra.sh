#!/bin/bash

set -e

docker-compose \
  -f dev/docker-compose.infrastructure.dev.yml \
  --env-file dev/.env.dev up -d

docker-compose \
  -f dev/docker-compose.ingress.dev.yml \
  --env-file dev/.env.dev up -d
