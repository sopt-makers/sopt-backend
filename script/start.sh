# appspec 내용 -> app 이라는 패키지명으로 통일화
DEFAULT_PATH=/home/ubuntu/app
# shellcheck disable=SC2164
cd $DEFAULT_PATH

if [ ! -f ./src/main/resources/application-local.yml ]; then
  # Dev 서버 배포의 경우
#  APPLICATION_PACKAGE=sopt-backend
  PROFILE=prod
elif [ ! -d ./src/main/resources/application-prod.yml ]; then
  # Prod 서버 배포의 경우
#  APPLICATION_PACKAGE=app
  PROFILE=local
fi

# shellcheck disable=SC2010
# JAR_NAME=$(ls $DEFAULT_PATH/build/libs/ | grep '.jar' | tail -n 1)
# JAR_PATH=$DEFAULT_PATH/build/libs/$JAR_NAME

# echo "> 애플리케이션 pid 확인"
# JAR_PID=$(pgrep -f $JAR_NAME)

JAR_NAME=app-server-0.0.1-SNAPSHOT.jar
JAR_PID=$(pgrep -f $JAR_NAME)

if [ -z $JAR_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> sudo kill -15 $JAR_PID"
  sudo kill -15 $JAR_PID
  sleep 10
fi

echo "> $JAR_PATH 배포" #3
sudo nohup java -jar -Dspring.profiles.active=$PROFILE $DEFAULT_PATH/build/libs/$JAR_NAME >nohup.out 2>&1 </dev/null &
