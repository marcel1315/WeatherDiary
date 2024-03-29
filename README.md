# 날씨 일기 프로젝트

- 특정 날짜의 일기를 작성하면, 그 날짜의 날씨를 자동으로 불러와 추가해서 저장해주는 API 서버 프로그램입니다.

- 일기를 추가, 수정, 삭제, 조회 할 수 있습니다.

- 매일 새벽 1시에 당일 날씨를 불러와 DB에 저장합니다.

## 사용 기술

- Spring Boot 3.2.4

- Java 17

- Gradle

- MySQL

- JPA

- Lombok

- Swagger

- Logback

- Openweathermap API

## 실행환경 설정

- src/main/resources/application.properties 파일에서 아래 값을 실행환경에 맞게 수정해야 합니다.

```yaml
spring.datasource.url=jdbc:mysql://localhost:3306/[YOUR_DB_TABLENAME]?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=[YOUR_DB_USERNAME]
spring.datasource.password=[YOUR_DB_PASSWORD]
openweathermap.key=[YOUR_API_KEY]
```

## API 명세

- Swagger UI 사용

- http://localhost:8080/swagger-ui/index.html

## 모니터링

- logs/ 폴더에 .log 확장자로 로그를 저장합니다.