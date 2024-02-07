## 🥇K-trendiest 백엔드
한국의 다양한 컨텐츠의 트렌드 랭킹을 알려주는 웹 서비스

https://k-trendiest-frontend.vercel.app/

![KtrendiestLogo](https://github.com/tfer2442/myAlgorithm/assets/39954601/2c7296be-8e3a-4aa7-8732-0c9a97600918)

- Organization github: https://github.com/K-trendiest
- Front-end github: https://github.com/K-trendiest/K-trendiest-frontend

### ⚙️개발 환경
- Java 17
- Spring Boot 3.1.4
- Gradle 3.0.17
- IntelliJ IDEA

### 🛠️기술 스택
<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><br/>
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"><img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"><br/>
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"><img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

### 📖주요 기능
1. 외부 API로 영상, 영화 데이터 수집
   - 영화: KMDb, KOBIS | 영상: YouTube Data
2. 크롤링을 통하여 음악 데이터 수집
   - https://www.melon.com/chart/index.htm
3. @Scheduled를 사용하여 일정 주기로 순위 업데이트


### 📁폴더 구조
    └─ktrendiest
        └─newton
            │  NewtonApplication.java
            │  
            ├─constant
            │      DisplayConstant.java
            │      UrlConstant.java
            │      
            ├─controller
            │      MovieController.java
            │      MusicController.java
            │      YoutubeController.java
            │      
            ├─domain
            │      Movie.java
            │      Music.java
            │      Youtube.java
            │      
            └─service
                    MovieService.java
                    MusicService.java
                    YoutubeService.java

### 💡트러블 슈팅
1. Builder 패턴
    - 문제: 변수가 많은 Domain을 생성자로 생성 할 때, 가독성이 저하 됨
    - 해결: Builder 패턴을 공부하고, @Builder를 사용하여 해결
2. API 주기
    - 문제: 요청을 받을때 마다 외부 API를 호출 하면, 비용 문제 발생
    - 해결: @Scheduled를 사용하여 cron 표현식으로 일정 주기마다 외부 API를 호출하고, 변수에 데이터를 담아 놓음. -> 프론트엔드가 요청하면 변수의 값을 반환
3. API key 공개
    - 문제: API key는 외부에 공개하면 안되므로 Git에 올라가면 안됨
    - 해결: properties 파일을 만들고, @Value를 통하여 API key를 가져오도록 함
4. AWS 배포시 데이터 업데이트 시간 불일치
    - 문제: AWS 배포 시 원하는 시간에 값이 업데이트 되지 않음 
    - 해결: ```TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));``` 를 통하여 프로그램의 시간을 한국 기준으로 변경
5. AWS VPC 비용 발생
    - 문제: AWS 프리티어를 통하여 배포를 진행하였는데, VPC 부분에 매일 비용이 발생
    - 해결: 외부 API를 주기적으로 호출하는 부분이 문제라고 생각하여, 업데이트 주기를 늘림. 그 결과, VPC 요금 발생하지 않음

### ✍후기
- 외부 API를 호출하여, 데이터 처리하는 것이 즐거웠습니다.
- 배포를 처음 해보았는데, 누군가 제 웹사이트를 사용할 수 있게 되었다는 점이 뿌듯했습니다.
- 코드를 수정하고, 배포하는 과정에서 반복적으로 일을 처리해야했고, 비효율적이라는 생각이 들었습니다. 
CI/CD를 공부하고 다른 프로젝트에 적용시켜보고 싶어졌습니다.
- DB없이 간단하게 프로젝트를 진행했는데, DB를 사용하여 이전의 랭킹들을 볼 수 있게 만드는 것도 재밌을 것 같습니다.
