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
echo "> Target Profile : $PROFILE"
# shellcheck disable=SC2164
REPOSITORY=/home/ubuntu/$DEFAULT_PATH

# shellcheck disable=SC2010
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME


echo "> $JAR_PATH 배포" #3
nohup java -jar -Dspring.profiles.active=$PROFILE $REPOSITORY/build/libs/app-server-0.0.1-SNAPSHOT.jar >nohup.out 2>&1 </dev/null &