#!/bin/bash
set -e
echo "🚀 Starting Blue App Services..."
docker-compose -f ./deploy/docker-compose.blue.yml up -d
