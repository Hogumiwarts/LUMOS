#!/bin/bash

docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.dev.yml --env-file .env.dev up --build -d postgres redis redis-insight zookeeper-0 kafka-0 kafka-ui