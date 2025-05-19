#!/bin/bash
set -e
echo "🚀 Starting Green App Services..."
docker-compose -f ./deploy/docker-compose.green.yml up -d
