# Daou Japan 경력자 과제
목차
1. [요구사항 분석]([https://github.com/o-geti/daou-jp/edit/main/README.md#1-%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD-%EB%B6%84%EC%84%9D](https://github.com/o-geti/daou-jp/edit/main/README.md#1-%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD-%EB%B6%84%EC%84%9D))
2. [요구사항 구현 현황](https://github.com/o-geti/daou-jp?tab=readme-ov-file#2-%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD-%EA%B5%AC%ED%98%84-%ED%98%84%ED%99%A9)
3. [코드 실행 방법](https://github.com/o-geti/daou-jp?tab=readme-ov-file#3-%EB%8D%B0%EB%AA%A8%EB%B2%84%EC%A0%84-%EC%8B%A4%ED%96%89)
4. [코드 스타일 가이드](https://github.com/o-geti/daou-jp?tab=readme-ov-file#4-%EC%BD%94%EB%93%9C-%EC%8A%A4%ED%83%80%EC%9D%BC-%EA%B0%80%EC%9D%B4%EB%93%9C)
5. [테스트 환경](https://github.com/o-geti/daou-jp?tab=readme-ov-file#5-%ED%85%8C%EC%8A%A4%ED%8A%B8-%ED%99%98%EA%B2%BD)
6. [설계](https://github.com/o-geti/daou-jp?tab=readme-ov-file#6-%EC%84%A4%EA%B3%84)
7. [Rate limit 설정](https://github.com/o-geti/daou-jp?tab=readme-ov-file#7rate-limit-%EC%84%A4%EC%A0%95)

## 1. 요구사항 분석
0. 공통.
   1. "시간|가입자수|탈퇴자수|결제금액|사용금액|매출금액" 형식존재.
   2. csv, txt 이외의 형식에도 유연하게 파일로 이루어진 경우에는 읽어들여 데이터베이스에 저장.
   3. 파일 이외에도 사용자가 직접 데이터 삽입도 가능.
   4. 각 요청마다 공통적으로 request, response 타임스탬프 기록
   5. 지정된 로그 형식 준수
1. 스케줄러 구현 
   1. 스케줄러로 매일 자정, 0-2의 요구사항을 만족시켜야함.
   2. 만일 파일 중간에 오류가 발생할 경우 미입력으로 처리한다. -> 데이터베이스에 저장하지않음.
   3. 같은 시간대의 통계자료가 있을 경우 데이터 정합성 로깅을하고 나머지 통계데이터들은 저장한다.
2. REST API 구현
   1. 0-3의 요구사항을 사용자가 직접 API로 호출하여 데이터 삽입이 가능하게끔 구현한다.
   2. 공통 예외처리.
3. 보안 정책에 맞춘 기능 구현
   1. 인증
      1. 2번 요구사항에서 인증된 사용자만 데이터 삽입, 조회등의 행위를 가능하게 한다.
      2. 토큰베이스로 구성한다. 
   2. 트래픽 제한
      3. 특정 API를 대상으로 많은 트래픽을 요청하면 블록 처리한다.

## 2. 요구사항 구현 현황
0. 공통
   1. 파일을 읽어들여 데이터베이스에 저장. ✅
   2. csv, txt 이외의 형식에도 유연하게 파일로 이루어진 경우에는 읽어들여 데이터베이스에 저장. ✅
   3. 파일 이외에도 사용자가 직접 데이터 삽입도 가능.✅
   4. 각 요청마다 공통적으로 request, response 타임스탬프 기록 ✅
   5. 지정된 로그 형식 준수 ✅
   6. 유닛테스트 ✅
1. 스케줄러 구현 
   1. 스케줄러로 매일 자정, 0-2의 요구사항을 만족시켜야함. ✅
   2. 만일 파일 중간에 오류가 발생할 경우 미입력으로 처리한다. -> 로깅 후 데이터베이스에 저장하지않음. ✅
   3. 같은 시간대의 통계자료가 있을 경우 데이터 정합성 로깅을하고 나머지 통계데이터들은 저장한다. ✅
2. REST API 구현
   1. 0-3의 요구사항을 사용자가 직접 API로 호출하여 데이터 삽입이 가능하게끔 구현한다. ✅
   2. 공통 예외처리. ✅
   3. 지정된 IP이외의 REST요청이 올 경우 Deny 처리 ✅ 
   4. 수정 기능 구현 ✅
   5. 삭제 기능 구현 ✅
3. 보안 정책에 맞춘 기능 구현
   1. 인증 ✅
      1. 2번 요구사항에서 인증된 사용자만 데이터 삽입, 조회등의 행위를 가능하게 한다. ✅
      2. 토큰베이스로 구성한다. ✅
   2. 트래픽 제한 
      1. 특정 API를 대상으로 많은 트래픽을 요청하면 블록 처리한다. ✅

## 3. 데모버전 실행
### 3-0 데모버전 실행 전 준비사항
   1. docker cli (docker compose로 mysql 실행해야합니다!) - 둘 중 하나를 선택해주세요. [도커 데스크탑 다운로드](https://www.docker.com/products/docker-desktop/) 혹은 [rancher desktop 다운로드](https://rancherdesktop.io/)
      * 엔터프라이즈 규모이시라면 rancher desktop을 권장합니다.
   2. java jdk 21 (21버전 미만의 경우, `./gradlew bootrun` 실행시 에러가 발생합니다!) - [테무린 자바21 다운로드](https://adoptium.net/temurin/releases/)
### 3-1. Mysql 실행
```sh
docker compose up -d
```
Mysql id, pw, port 정보 입니다. <br>
id : daoujapan <br>
pw : daou1234! <br>
port : 23306

### 3-2. 애플리케이션 실행하기.
```sh
./gradlew bootRun
```
### 3-3 토큰 발급 및 API사용 방법
1. http://localhost:8080/login 으로 접속하여 로그인 후 토큰을 발급받으세요.
   1. 로그인 정보<br>
      username: test123<br>
       password: 1234
2. Swagger에 접속해주세요. - http://localhost:8080/swagger-ui/index.html#/
3. Swagger의 Authroize에 발급한 토큰을 넣어주주세요.
4. 원하시는 API를 명세 규격에 맞게 사용해주세요.

## 4. 코드 스타일 가이드
spotless 를 사용하여 일관된 코드 룰을 가져갑니다. <br>
아래는 코드 룰입니다.
1. 구글 포맷 사용
2. import 순서는 'java' -> 'jakarta' -> '' -> 'com.minsu' 순입니다.<br>
   '' 표시인 빈값은 기타 의존성에 대한 순서이며 영문 순입니다. 
3. 미사용 import 제거
4. 끝에 띄어쓰기있으면 제거.
5. 항상 파일의 마지막 라인은 빈 라인으로 채우기
```groovy
spotless {
    java {
        googleJavaFormat('1.24.0')

        importOrder 'java', 'jakarta', '', 'com.minsu'

        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
```

## 5. 테스트 환경
### 5-1환경 개요.
테스트 데이터베이스는 h2로 사용했습니다.
테스트 시작시 init_ddl과 data.sql 설정하여 자동으로 테이블과 행 정보가 등록됩니다.<br>
테스트 teardown시 자동으로 h2의 데이터베이스의 DDL이 영속되지않고, 삭제됩니다.<br>
logback 설정은 기존 상용환경과 다르게 로그 레벨을 디버깅 레벨로 설정했습니다.<br>

### 5-2 실행방법
```sh
# 프로젝트 루트에서
./gradlew test
```

### 5-3레포트 파일
테스트 레포트 결과 파일은 아래에 있습니다.
1. gradle 결과 레포트 html - `build/reports/tests/test/index.html`
2. jacoco 커버리지 레포트 html - `build/reports/jacoco/test/html/index.html`

## 6 설계.
### 6-1. 테이블 설계 이미지
![image](https://github.com/o-geti/daou-jp/blob/main/doc-static/table.png?raw=true)
### 6-2. 소스 아키텍처
```
.
├── config - 애플리케이션 설정 패키지입니다.
│
├── controllers
│   ├── advisor - 공통 예외처리 패키지입니다.
│   └── statistic - 통계자료를 API 요청 컨트롤러입니다.
│
├── data
│   ├── request - 요청관련 데이터 객체가 정의된 패키지입니다
│   ├── response - 응답관련 (공통, 페이징) 데이터 객체가 정의된 패키지입니다.
│   └── statistics - 통계와 관련된 DTO성 객체입니다.
│
│
├── domains
│   └── statistics
│       ├── BaseEntity.java - 각 통계 테이블의 공통 컬럼을 가진 베이스 엔티티입니다. 
│       ├── amount
│       │   ├── PaymentAmountStatisticEntity.java - 결제금액 엔티티
│       │   ├── SalesAmountStatisticEntity.java - 매출금액 엔티티
│       │   └── UsageAmountStatisticEntity.java - 사용금액 엔티티
│       └── member
│           ├── LeaverStatisticEntity.java - 탈퇴자 통계 엔티티
│           └── SubscriberStatisticEntity.java - 가입자 통계 엔티티
│
├── enums - enum 객체들을 모아 관리하는 패키지입니다.
│
├── exception - 예외 클래스들을 관리하는 패키지입니다.
│
├── helper - 유틸성, 개발에 도움을 주는 도구 형식으로 분류 되는 객체들을 모아 관리합니다.
│
├── mapper - DTO <-> 엔티티 간 변환 시켜주는 기능을 담은 패키지입니다.
│
├── parser - 구문 변환 관련 객체들을 모아놓은 패키지입니다.
│
├── repositories - 데이터베이스와 엔티티간 연결시켜주는 패키지입니다.
│
├── scheduler - 스케줄러가 정의 된 패키지입니다.
│
├── security - 보안관련, 로그인 관련 객체를 모은 패키지입니다.
│
└── services - 도메인 관련 비즈니스 로직을 처리하는 패키지입니다.
```
### 6-3. 유스케이스별 흐름.
#### 1. 로그인
![image](https://github.com/o-geti/daou-jp/blob/main/doc-static/Login.png?raw=true)
#### 2. 데이터 요청
![image](https://github.com/o-geti/daou-jp/blob/main/doc-static/resourceAPI.png?raw=true)
#### 3. rate limit 에러 케이스
![image](https://github.com/o-geti/daou-jp/blob/main/doc-static/toomany.png?raw=true)

## 7.Rate limit 설정
1. Resilience4j를 이용하여 rate limit 설정을 진행했습니다.
2. 해당 패키지를 선택한이유는 어노테이션으로 서비스 레이어에 태그만 달아,<br>
나머지 설정을 yml로 하여 설정하기 좋은 구조라 채택했습니다.
3. 설정값의 자세한 내용은 application.yml에 설정 상세 작성되어있습니다. - [링크](https://github.com/o-geti/daou-jp/blob/main/src/main/resources/application.yml)


