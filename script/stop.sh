#!/bin/bash
kill $(ps aux | grep java | grep -v grep | awk '{print $2}')
echo "java stopped"