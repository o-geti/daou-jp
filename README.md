# DAU Japan 경력자 과제

## 요구사항 분석

0. 공통.
   1. "시간|가입자수|탈퇴자수|결제금액|사용금액|매출금액" 형식존재.
   2. 해당형식을 파일로 이루어진 경우에는 읽어들여 데이터베이스에 저장.
   3. 파일 이외에도 사용자가 직접 데이터 삽입도 가능.
1. 스케줄러 구현 
   1. 스케줄러로 매일 자정, 0-2의 요구사항을 만족시켜야함.
   2. 만일 파일 중간에 오류가 발생할 경우 미입력으로 처리한다.<br>
      (전체 미입력?, 해당 읽어들인 라인만 미입력?, 오류가 발생한 부분만 미입력?)
2. REST API 구현
   3. 0-3의 요구사항을 사용자가 직접 API로 호출하여 데이터 삽입이 가능하게끔 구현한다.
3. 보안 정책에 맞춘 기능 구현
   1. 인증
      1. 2번 요구사항에서 인증된 사용자만 데이터 삽입, 조회등의 행위를 가능하게 한다.
      2. 토큰베이스로 구성한다. 
   2. 트래픽 제한
      3. 특정 API를 대상으로 많은 트래픽을 요청하면 블록 처리한다.


## 테이블 설계 이미지
![image](https://github.com/oct-sky-out/dau-japan-project/blob/main/doc-static/table.png?raw=true)

## 코드 스타일 가이드
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