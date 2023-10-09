# SOPT APP PROJECT

> 🚀 SOPT 공식 앱으로, 출석/공지/네트워킹 미션 등 다양한 기능을 제공합니다.

![image](https://github.com/sopt-makers/sopt-backend/assets/63996052/e00e6014-04c4-4da5-81ec-85ab6b2a270b)

# PRODUCT
### [1기] 솝탬프 : 네트워킹 미션

솝트 네트워킹을 활성화시키기 위해 스탬프 미션 및 인증 플랫폼 제공

<img width="340" alt="스크린샷 2023-03-03 오전 12 34 02" src="https://user-images.githubusercontent.com/35520314/222474521-61cb1f6f-24dd-4304-ab6d-b3c6987a60c2.png">

### [2기] 공식앱 : 출석/공지/메이커스

활동 기수를 위한 출석/공지 기능과 메이커스 프로덕트 링크 제공

<img width="340" alt="스크린샷 2023-03-03 오전 12 34 02" src="https://github.com/sopt-makers/sopt-backend/assets/63996052/def334cc-96ce-4532-b4bd-717c19b6b2ee">

# PROJECT
- java version: 17
- springboot: 2.7.4

# DEPLOY
- [local] deploy
  - docker postgres 실행 
  - git clone https://github.com/sopt-makers/app-server.git (최초 실행)
  - ./gradlew clean build 
  - java -jar -Dspring.profiles.active=local build/libs/app-server-0.0.1-SNAPSHOT.jar
  - http://localhost:8080
- [dev] deploy
  - dev 서버 접속
  - git pull origin dev
  - ./gradlew clean build
  - java -jar -Dspring.profiles.active=local build/libs/app-server-0.0.1-SNAPSHOT.jar
  - https://app.dev.sopt.org
- [main] github actions
  - 배포 조건: main branch에 merge 되거나, makers-app-develop 태그 빌드하는 경우 <br>
  - gradle build -> s3 에 jar 업로드 -> codedeploy 배포
  - https://app.sopt.org

# FOLDERING

```tsx
app-server
│  .gitignore
│  appspec.yml
│  build.gradle
│  gradlew
│  gradlew.bat
│  README.md
│  settings.gradle
│
└─src/main
   │  
   ├─java/org/sopt/app
   │  │  AppApplication
   │  ├─application
   │  ├─common
   │  ├─domain
   │  ├─interfaces.postgres
   │  └─presentation
   │
   └─resources
       │  application.yml
       │  application-local.yml
       │  application-prod.yml
       └─database
```

# SECRETS
- 인수인계 담당자에게 아래 항목들을 요청해주세요! (2기 [ozzing](https://github.com/ozzing))
  - application-prod.yml
  - application-local.yml
  - prod database info
  - dev database info
  - prod pem key
  - dev pem key
- AWS 관련 권한은 현 기수 AWS 담당자에게 적절한 권한을 요청해주세요!
  - ex) s3 access key, secret key
- 이 외에도 필요한 항목이 있으면 요청해주세요!

# ETC
- Playground OAuth [바로가기](https://www.notion.so/parangjy/3596d3abc6304004a07d1fc79981d8bc)
- docker postgres 실행 방법 [바로가기](https://github.com/sopt-makers/app-server/wiki/Local에서-Docker-postgres-실행하는-법)
- 2기 Swagger [바로가기](https://app.dev.sopt.org/swagger-ui/index.html)
- 2기 API 문서 [바로가기](https://parangjy.notion.site/3278da92a8f646aea4eba1d0f5a45f43?v=15ca2103aaec4bbaaaea7808c872484c)
- 1기 솝탬프 유스케이스 [바로가기](https://github.com/sopt-makers/app-server/wiki/솝탬프-프로젝트-유스케이스)
- 1기 API 문서 [바로가기](https://parangjy.notion.site/166132ae964d4bc483c71e507497bb9c)

# Architecture
Layered Architecture방식을 채택.
presentation layer, domain layer, interface layer, application layer, facade layer로 구성되어있다.
## Presentation Layer
presentation layer는 사용자의 요청을 받아서 응답을 해주는 역할을 한다.
해당 layer에서는 facade layer 혹은 application layer를 호출하여 사용자의 요청에 대한 응답을 해준다.
해당 layer에서는 response를 위한 dto를 정의하고, request를 위한 dto를 정의한다. 또한 response를 만드는 역할을 한다.
## Domain Layer
domain layer는 entity와 entity의 관계를 정의한다.
## Interface Layer
interface layer는 외부와의 통신을 위한 interface를 정의한다.
repository interface가 해당된다.
## Application Layer
application layer는 비즈니스 로직을 정의한다.
해당 layer에서는 service를 정의한다.
해당 layer에서는 entity를 dto로 변환하는 역할을 한다.
## Facade Layer
facade layer는 application layer를 호출하는 역할을 한다.
해당 layer에서는 service의 ochestrator 역할을 한다.
