#!/bin/bash

reload_nginx() {
    local PORT=$1
    local ACTUATOR_PORT=$2

    echo "▶️ Nginx Reload (Port switching applied) ..."

    # 메인 앱 포트 스위칭
    echo "set \$service_url http://127.0.0.1:${PORT};" | sudo tee /etc/nginx/conf.d/app-url.inc
    # Actuator 모니터링 포트 스위칭
    echo "set \$actuator_url http://127.0.0.1:${ACTUATOR_PORT};" | sudo tee /etc/nginx/conf.d/app-actuator-url.inc
    
    sudo nginx -s reload
    echo "Current running Port after switching: $(sudo cat /etc/nginx/conf.d/app-url.inc)"
    echo "Current running Actuator Port after switching: $(sudo cat /etc/nginx/conf.d/app-actuator-url.inc)"
}