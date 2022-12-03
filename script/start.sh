REPOSITORY=/home/ubuntu/app
cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

echo "> $JAR_PATH 배포" #3
nohup java -jar -Dspring.profiles.active=prod $REPOSITORY/build/libs/app-server-0.0.1-SNAPSHOT.jar >nohup.out 2>&1 </dev/null &