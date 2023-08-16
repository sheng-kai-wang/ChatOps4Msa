#!/bin/bash

# close all the containers.
docker compose down

# end the ChatOps4Msa
sh end_chatops4msa.sh

# print all containers
docker ps -as --format "table {{.Image}}\t{{.Status}}\t{{.Names}}\t{{.Ports}}"