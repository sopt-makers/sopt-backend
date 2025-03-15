#!/bin/bash

reload_nginx() {
    local PORT=$1

    echo "▶️ Nginx Reload (Port switching applied) ..."

    echo "set \$service_url http://127.0.0.1:${PORT};" | sudo tee /etc/nginx/conf.d/app-url.inc
    sudo nginx -s reload
    echo "Current running Port after switching: $(sudo cat /etc/nginx/conf.d/app-url.inc)"
}