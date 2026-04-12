#!/bin/bash

stop_container() {
    local CONTAINER_NAME=$1

    echo "▶️ Stopping ${CONTAINER_NAME} Container"
    docker-compose stop ${CONTAINER_NAME}

    echo "🗑️ Removing ${CONTAINER_NAME} Container"
    docker-compose rm -f ${CONTAINER_NAME}
}
