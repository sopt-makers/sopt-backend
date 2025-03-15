#!/bin/bash
cd /home/ubuntu/app

if [ ! -f ./src/main/resources/application-dev.yml ]; then
  kill $(ps aux | grep java | grep -v grep | awk '{print $2}')
  echo "java stopped"
fi


