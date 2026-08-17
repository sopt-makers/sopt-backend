#!/bin/bash
deploy_container() {
    local CONTAINER_NAME=$1
    local PORT=$2
    export ACTUATOR_PORT=$3

    echo "▶️ Switching to ${CONTAINER_NAME} at Port ${PORT} (Actuator: ${ACTUATOR_PORT}) ..."
    echo "docker-compose pull & up ..."

    # redis는 pull X. 태그 고정과 함께 배포 시 캐시 전체 삭제를 막기 위함.
    # docker-compose pull redis
    docker-compose up -d redis
    docker-compose pull ${CONTAINER_NAME}
    docker-compose up -d ${CONTAINER_NAME}
}