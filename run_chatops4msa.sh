#!/bin/bash

# create a new JAR file
mvn clean install -Dmaven.test.skip=true
cp ./target/*.jar app.jar

# terminate the original program
sh end_chatops4msa.sh

# execute the program using tmux
tmux new-session -d -s java-session "java -jar app.jar"
tmux send-keys -t java-session C-m
tmux detach-client -s java-session