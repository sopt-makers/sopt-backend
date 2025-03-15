DEFAULT_PATH="/home/ubuntu/app"
cd "$DEFAULT_PATH" || { echo "directory not exist"; exit 1; }

if [ ! -f ./src/main/resources/application-dev.yml ]; then
  PROFILE=prod
elif [ ! -f ./src/main/resources/application-prod.yml ]; then
  echo "start.sh does not work in dev"
  exit 0
fi

JAR_NAME=app-server-0.0.1-SNAPSHOT.jar
JAR_PATH=$DEFAULT_PATH/build/libs/$JAR_NAME
JAR_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$JAR_PID" ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> sudo kill -15 $JAR_PID"
  sudo kill -15 "$JAR_PID"
  sleep 10
fi

echo "> $JAR_PATH 배포"
sudo nohup java -jar -Dspring.profiles.active="$PROFILE" "$JAR_PATH" >nohup.out 2>&1 </dev/null &
