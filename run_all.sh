#!/bin/bash

# clone the Bookinfo microservices system
cd ..
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Productpage.git
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Reviews.git
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Ratings.git
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Details.git
cd ChatOps4Msa

# close the previous container.
docker compose down

# modify the permissions of prometheus/data
sudo chown -R nobody:nogroup prometheus/data

# modify the permissions of grafana/data.
sudo chown -R 472:root grafana/data

# start all containers
docker compose up -d

# run the ChatOps4Msa
echo "Wait for RabbitMQ to start for 15 seconds..."
sleep 15
sh run_chatops4msa.sh

# print all containers
docker ps -as --format "table {{.Image}}\t{{.Status}}\t{{.Names}}\t{{.Ports}}"