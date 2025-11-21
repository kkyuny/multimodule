# Spring Multi Module

## 1. 강의 개요
Spring Boot 기반 서비스에서 **API 모듈(실행)** 과  
**Common 모듈(라이브러리)** 을 분리하여 재사용성과 유지보수성을 높이는 구조를 배운다.

---

## 2. Spring Multi Module 개념

### 멀티모듈 목적
- 공통 코드 재사용 (Enum, DTO, Entity 등)
- API/Batch/Admin 등 여러 서비스에서 사용 가능
- 모듈 단위 빌드 속도 향상
- 패키지/책임 분리로 유지보수 쉬움

### 구조 예시
multimodule(root)/
├── settings.gradle.kts
├── build.gradle.kts (root 최소 설정)
├── module-api/ (Spring Boot 실행)
└── module-common/ (라이브러리, Boot 아님)

---

## 3. 프로젝트 생성

### module-root
- settings.gradle.kts
```kotlin
rootProject.name = "multimodule"

include("module-api")
include("module-common")
```
- root의 build.gradle.kts
- root 아래에 사용될 모듈을 include 한다.

- build.gradle.kts
``` kotlin
plugins {}

allprojects {
    repositories {
        mavenCentral()
    }
}
```
- root의 gradle 설정은 완전히 비워둔다.
- root는 단순히 프로젝트 entry(그룹) 역할만 하기 때문이다.

### module-api 
- build.gradle.kts
```kotlin
dependencies {
    implementation(project(":module-common"))
}
```
- 디펜던시에 common 모듈을 사용하기 위한 코드를 추가한다.

- Application 클래스
```java
@SpringBootApplication(scanBasePackages = "dev.be")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
```
- common에 등록된 component도 사용하기 때문에 스캔 범위를 입력해야한다.
- 어플리케이션의 실행 위치를 변경해도 된다.

### module-common (라이브러리 모듈)
- 특징
  - Spring Boot plugin ❌
  - bootJar ❌
  - main() 없음
  - 단순 library (api 모듈의 dependency)

- build.gradle.kts
```kotlin
plugins {
    ...
    id("java-library")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }
```
- implementation(...): 내부에서만 사용(외부 모듈에 노출 X)
- api(...): 외부 모듈도 이 의존성을 함께 사용하도록 노출
- api()를 선언하면 Common 모듈에서 JPA / DTO / Exception 등을 내보낼 수 있다.(외부에서 사용할 수 있다.)
- api() 사용을 위해 플러그인에 id("java-library") 추가도 필요하다.
- xxx-plain.jar만 필요하기 때문에 tasks 설정을 추가한다.

## 4. Profile 설정 
- 환경별 파일 생성
  - application-local.yml
  - application-prod.yml

- 로컬(IDE)에서 적용: Run/Debug Configurations → Active Profiles: local
- 배포 시 적용(jar가 있는 디렉토리에서): java -Dspring.profiles.active=prod -jar app.jar