# SOPT APP PROJECT

> 🚀 SOPT 공식 앱으로, 출석/공지/네트워킹 미션 등 다양한 기능을 제공합니다.

## 1.솝탬프 : 네트워킹 미션

솝트 네트워킹을 활성화시키기 위해 스탬프 미션 및 인증 플랫폼 제공

<img width="340" alt="스크린샷 2023-03-03 오전 12 34 02" src="https://user-images.githubusercontent.com/35520314/222474521-61cb1f6f-24dd-4304-ab6d-b3c6987a60c2.png">


## 2. 프로젝트 설치
- java version: 17
- springboot: 2.7.4 (3점대로 올리는 것 추천)

### Quick start
> local 에서 docker postgres 를 우선 실행해야합니다. [ wiki 참고 ](https://github.com/sopt-makers/app-server/wiki/Local에서-Docker-postgres-실행하는-법)
```
git clone https://github.com/sopt-makers/app-server.git

./gradlew clean build 

java -jar -Dspring.profiles.active=local build/libs/app-server-0.0.1-SNAPSHOT.jar

```
http://localhost:8080 으로 접속 가능합니다.

## 3. 부록

3-1. github actions

배포 조건: main branch에 merge 되거나, makers-app-develop 태그 빌드하는 경우 <br>
- gradle build -> s3 에 jar 업로드 -> codedeploy 배포

3-2. API 문서 [바로가기](https://parangjy.notion.site/166132ae964d4bc483c71e507497bb9c)

3-3. 유스케이스 [바로가기](https://github.com/sopt-makers/app-server/wiki/솝탬프-프로젝트-유스케이스)