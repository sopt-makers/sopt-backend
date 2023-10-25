DEFAULT_PATH=/home/ubuntu
# shellcheck disable=SC2164
cd $DEFAULT_PATH

if [ ! -d app ]; then
  # Dev 서버 배포의 경우
  APPLICATION_PACKAGE=sopt-backend
  PROFILE=local
elif [ ! -d sopt-backend ]; then
  # Prod 서버 배포의 경우
  APPLICATION_PACKAGE=app
  PROFILE=prod
fi
echo "> Target Profile : $PROFILE"
# shellcheck disable=SC2164
cd $APPLICATION_PACKAGE
REPOSITORY=/home/ubuntu/$APPLICATION_PACKAGE리

# shellcheck disable=SC2010
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME


echo "> $JAR_PATH 배포" #3
nohup java -jar -Dspring.profiles.active=$PROFILE $REPOSITORY/build/libs/app-server-0.0.1-SNAPSHOT.jar >nohup.out 2>&1 </dev/null &