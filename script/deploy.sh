#!/bin/bash
set -euo pipefail

# 홈 기준(기본: $HOME/app). 필요하면 워크플로우에서 APP_DIR=/custom/path 로 덮어써도 됨.
APP_DIR="${APP_DIR:-$HOME/app}"
cd "$APP_DIR"

# 공통 스크립트 로드
source "$APP_DIR/script/health_check.sh"
source "$APP_DIR/script/deploy_container.sh"
source "$APP_DIR/script/nginx_reload.sh"
source "$APP_DIR/script/stop_container.sh"

BLUE_CONTAINER_NAME="app-blue"
BLUE_PORT=9090
GREEN_CONTAINER_NAME="app-green"
GREEN_PORT=9091

DOCKER_PS_OUTPUT=$(docker ps --format "{{.Names}}" | grep -E "(${BLUE_CONTAINER_NAME}|${GREEN_CONTAINER_NAME})" || true)
RUNNING_CONTAINER_NAME="${DOCKER_PS_OUTPUT:-}"

ALL_PORTS=("${BLUE_PORT}" "${GREEN_PORT}")
AVAILABLE_PORT=()
RUNNING_SERVER_PORT=""

check_running_container() {
  if [[ "$RUNNING_CONTAINER_NAME" == "$BLUE_CONTAINER_NAME" ]]; then
      echo "Running Port: $BLUE_CONTAINER_NAME (:$BLUE_PORT)"
      RUNNING_SERVER_PORT=$BLUE_PORT
  elif [[ "$RUNNING_CONTAINER_NAME" == "$GREEN_CONTAINER_NAME" ]]; then
      echo "Running Port: $GREEN_CONTAINER_NAME (:$GREEN_PORT)"
      RUNNING_SERVER_PORT=$GREEN_PORT
  else
    echo "Running Port: None"
  fi
}

check_available_ports() {
  for item in "${ALL_PORTS[@]}"; do
    if [ "$item" != "$RUNNING_SERVER_PORT" ]; then
      AVAILABLE_PORT+=("$item")
    fi
  done

  if [[ ${#AVAILABLE_PORT[@]} -eq 0 ]]; then
      echo "❌ No available ports."
      exit 1
  fi
}

### --

check_running_container
check_available_ports

DEPLOY_TARGET_CONTAINER_NAME=""
DEPLOY_TARGET_PORT=""

if [ "$RUNNING_SERVER_PORT" == "$BLUE_PORT" ]; then # Green Up
  DEPLOY_TARGET_CONTAINER_NAME=${GREEN_CONTAINER_NAME}
  DEPLOY_TARGET_PORT=${GREEN_PORT}
elif [ "$RUNNING_SERVER_PORT" == "$GREEN_PORT" ]; then # Blue Up
  DEPLOY_TARGET_CONTAINER_NAME=${BLUE_CONTAINER_NAME}
  DEPLOY_TARGET_PORT=${BLUE_PORT}
else
  echo "❌ No running container found. Defaulting to $BLUE_CONTAINER_NAME."
  DEPLOY_TARGET_CONTAINER_NAME=${BLUE_CONTAINER_NAME}
  DEPLOY_TARGET_PORT=${BLUE_PORT}
fi

deploy_container "${DEPLOY_TARGET_CONTAINER_NAME}" ${DEPLOY_TARGET_PORT}

if ! health_check ${DEPLOY_TARGET_PORT}; then
  echo "❌ Health Check failed ..."
  stop_container "${DEPLOY_TARGET_CONTAINER_NAME}"
  exit 1
fi

reload_nginx ${DEPLOY_TARGET_PORT}

if [[ -n "$RUNNING_CONTAINER_NAME" ]]; then
  stop_container ${RUNNING_CONTAINER_NAME}
fi

echo "▶️ Final health check applied nginx port switching ..."
if ! health_check ${DEPLOY_TARGET_PORT}; then
  echo "❌ Server change failed ..."
  stop_container "${DEPLOY_TARGET_CONTAINER_NAME}"
  exit 1
fi

echo "✅ Server change successful 👍"