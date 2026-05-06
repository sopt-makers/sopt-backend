#!/bin/bash
deploy_container() {
    local CONTAINER_NAME=$1
    local PORT=$2
    export ACTUATOR_PORT=$3

    echo "▶️ Switching to ${CONTAINER_NAME} at Port ${PORT} (Actuator: ${ACTUATOR_PORT}) ..."
    echo "docker-compose pull & up ..."

    docker-compose pull redis
    docker-compose up -d redis
    docker-compose pull ${CONTAINER_NAME}
    docker-compose up -d ${CONTAINER_NAME}
}