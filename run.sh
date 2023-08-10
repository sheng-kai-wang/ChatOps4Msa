#!/bin/bash

# Create a JAR file (ChatOps4Msa)
mvn clean install -Dmaven.test.skip=true
cp ./target/*.jar app.jar

# Clone the Bookinfo microservices system
cd ..
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Productpage.git
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Reviews.git
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Ratings.git
git clone git@github.com:sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo-Details.git
cd ChatOps4Msa

# Close the previous container.
docker compose down

# Modify the permissions of prometheus/data
sudo chown -R nobody:nogroup prometheus/data

# Modify the permissions of grafana/data.
sudo chown -R 472:root grafana/data

# Start all containers
docker compose up -d

# Print all containers
docker ps -as --format "table {{.Image}}\t{{.Status}}\t{{.Names}}\t{{.Ports}}"