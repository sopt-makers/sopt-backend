#!/bin/bash

HEALTH_CHECK_URL=/api/v2/health

health_check() {
  local PORT=$1
  local RETRIES="${RETRIES:-5}"

  echo "▶️ Start health check after 20 seconds"
  sleep 20

  for retry_count in $(seq 1 $RETRIES); do
    echo "🔎 Health Check on Port ${PORT} (Attempt: ${retry_count}/${RETRIES})..."
    sleep 5

    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${PORT}${HEALTH_CHECK_URL})

    if [ "$HTTP_STATUS" -eq 200 ]; then
      echo "✅ Success! Server is healthy (HTTP 200)."
      return 0
    else
      echo "⚠️ Health check failed. Expected 200 but got ${HTTP_STATUS}."
    fi

    if [ $retry_count -eq $RETRIES ]; then
      echo "❌ Health check failed after $RETRIES attempts."
      return 1
    fi
  done
}