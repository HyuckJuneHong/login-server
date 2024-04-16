## 📌 Login Server

- 회원가입 및 로그인 기능 구현 저장소입니다.

### 개발 환경

- Language : Java 17
- Framework : Springboot 3.2.2
- ORM : JPA
- Build Tool : Gradle
- Dev Tool : IntelliJ
- Test : JUnit 5
- DB : H2

<br/>

## 🌈 실행 방법 및 구현 설명

### Database : H2

- URL : http://localhost:8080/h2-console
- Driver Class : `org.h2.Driver`
- JDBC URL : `jdbc:h2:mem:db`
- User Name : `sa`
- Password :

### Springdoc OpenApi (Swagger)

- Springdoc OpenApi(Swagger) URL : http://localhost:8080/swagger-ui/index.html
- Springdoc OpenApi(Swagger)로 APIs Spec을 문서화 했습니다.
- Swagger 기능 중 하나인, 전역인증을 도입하여 API 테스트를 원활하게 했습니다.

### 인증 및 인가

1. 인증 및 인가 기능은 수월한 URL 분리와 테스트 환경, 권한 제어 등을 위해 Spring Security 환경에서 JWT를 이용해 구현했습니다.
2. 보안을 위해 인가 실패 시, 403 에러 대신에 404 에러를 반환하도록 구현했습니다.
3. 인증/인가 필터 처리
    - 요청을 보낼 때, SecurityConfig에 설정된 주소는 OncePerRequestFilter를 커스터마이징한 인증/인가 필터를 거쳐 처리가 되도록 구현했습니다.
    - 필터에서는 accessToken과 refreshToken을 검증하여 유효하다면, SecurityContext에서 관리되도록 구현했습니다.
4. @AuthenticationPrincipal
    - 해당 어노테이션을 커스텀하여 SecurityContext에 저장된 사용자는 요청과 동시에 필요한 정보를 함께 줄 수 있도록 구현했습니다.

### 테스트 코드

1. 테스트 코드는 JUnit5를 활용해 총 37개를 작성했고 테스트 커버리지는 Class 95%, Method 93%, Line 93%입니다.
3. 컨트롤러는 통합 테스트를 진행했고, 그 외는 단위 테스트를 진행했습니다.

### 회원가입 기능 설명

1. 개인 정보 암호화
    - 비밀번호 : Spring Security PasswordEncoder 인터페이스 구현체인 Bcrypt 암호화 방식을 사용했습니다.
    - 주민등록번호 : 중복 검사를 위해 AES-128 암/복호화 방식을 활용했습니다.

### 로그인 기능 설명

1. 로그인을 성공적으로 하면 엑세스 토큰 및 리프레쉬 토큰을 발급 응답해줍니다.
2. 초반에 발급된 토큰은 Authorization Header에서 관리됩니다.
3. 엑세스 토큰
    - 사용자 아이디와, 사용자 이름, 사용자 권한을 클레임 정보로 갖게 했습니다.
    - 보안(토큰 탈취)을 위해 만료기간은 짧게 가져갔습니다. (5분)
4. 리프레쉬 토큰
    - 엑세스 토큰 재발급을 위해 만들었기 때문에, 사용자 아이디만 클레임 정보로 갖게 했습니다.
    - 재발급 용도로만 사용되기 때문에, 만료기간은 길게 가져갔습니다. (7일)
    - 보안을 위해 엑세스 토큰 재발급 시, 리프레쉬 토큰도 함께 재발급 하도록 구현했습니다.
    - 리프레쉬 토큰은 현재 데이터베이스에서 관리되고 토큰 재발급할 때마다 업데이트됩니다.
        - 클론 후 바로 별도 설정없이 바로 실행해볼 수 있도록 Redis로 관리하지 않습니다.

<br/>

## 👩‍💻 요구 상세

- [x] Java17 , Spring Boot 3.x , JPA , H2 , Gradle를 활용합니다.
    - [x] H2 Embedded DB를 사용하되, 메모리 모드로 실행할 수 있게 설정했습니다.
- [x] API 구현
    - [x] 회원가입
    - [x] 로그인
- [x] 모든 요청/응답 `application/json` 타입으로 구현했습니다.
- [x] 각 기능 및 제약사항에 대한 테스트를 작성했습니다.
- [x] `swagger`를 활용해 API 확인/실행이 가능하도록 구현했습니다.
- [x] 민감 정보는 암호화하여 저장했습니다.
    - Ex : 주민등록번호, 비밀번호
