# 👤 User Service (Da-it-da)

`user-service`는 **Da-it-da** 마이크로서비스 아키텍처(MSA) 프로젝트의 핵심 서비스로, 사용자의 인증(Authentication), 인가(Authorization), 그리고 프로필 및 권한 관리를 담당합니다. **DDD(Domain-Driven Design) 4계층 아키텍처**를 채택하여 비즈니스 로직의 순수성을 유지하고 기술적 확장에 유연하게 대응하도록 설계되었습니다.

---

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.5.13, Spring Cloud (Eureka, Config, OpenFeign)
- **Language**: Java 17
- **Database**: PostgreSQL (Main), Redis (Token/Cache)
- **ORM/Query**: JPA (Hibernate), **QueryDSL 5.1.0**
- **Authentication**: **Keycloak** (Identity Provider)
- **Observability**: **Zipkin**, **Micrometer Tracing** (Distributed Tracing)
- **Infrastructure**: Docker, Docker Compose
- **Build Tool**: Gradle

---

## 🏗 Architecture (DDD 4-Layer)

서비스의 복잡도를 관리하고 계층 간 결합도를 낮추기 위해 4계층 아키텍처를 준수합니다.

1.  **Presentation Layer**: REST Controller를 통해 클라이언트 요청을 수신하고 응답을 반환합니다. (`dto`, `controller`)
2.  **Application Layer**: 비즈니스 유스케이스를 구현하며, 도메인 객체 간의 흐름을 제어합니다. (`service`, `command`, `result`, `port`)
3.  **Domain Layer**: 핵심 비즈니스 로직과 엔티티를 포함하며, 외부 계층에 의존하지 않는 순수성을 유지합니다. (`entity`, `repository`, `enums`, `vo`)
4.  **Infrastructure Layer**: DB persistence, 외부 API 통신(Feign), 인증 서버 연동(Keycloak) 등 기술적 실구현을 담당합니다. (`persistence`, `external`)

---

## ✨ Key Features

### 1. 인증 및 보안 (Auth & Security)
- **Keycloak 연동**: 외부 인증 서버인 Keycloak을 사용하여 표준화된 OAuth2/OIDC 인증을 제공합니다.
- **Redis 기반 토큰 관리**:
    - **Refresh Token**: Redis에 저장하여 세션 유지 및 토큰 탈취 시 즉각적인 무효화를 지원합니다.
    - **Blacklist**: 로그아웃된 Access Token을 Redis에 등록하여 무상태(Stateless) 토큰의 보안 취약점을 보완합니다.
- **AOP 기반 권한 체크**: `@HasRole` 어노테이션과 Aspect를 통해 선언적이고 세밀한 접근 제어(RBAC)를 구현하였습니다.

### 2. 사용자 관리 (User Management)
- **회원가입**: 외부 서비스(`hub`, `company`) 존재 여부를 Feign을 통해 검증 후, Keycloak과 로컬 DB에 데이터를 동기화합니다.
- **내 정보 조회 (`/me`)**: Gateway에서 전달된 헤더 정보를 활용하여 현재 로그인한 사용자의 정보를 즉시 반환합니다.
- **QueryDSL 기반 목록 조회**: 복잡한 필터링 조건(이메일, 이름, 권한, 상태 등)과 페이징 처리를 최적화된 동적 쿼리로 지원합니다.
- **Soft Delete**: 데이터를 물리적으로 삭제하지 않고 상태(`status`)를 변경하여 데이터 정합성과 히스토리를 보존합니다.

### 3. 인프라 통합 (Infrastructure Integration)
- **JPA Auditing**: `X-User-Id` 헤더를 인식하여 데이터의 생성자/수정자를 자동으로 기록합니다.
- **Feign Client**: 타 서비스와의 통신 시 예외 처리를 강화하여 분산 시스템의 안정성을 확보하였습니다.
- **분산 트레킹 (Zipkin)**: Micrometer Tracing을 연동하여 Gateway부터 User Service까지의 전체 요청 흐름을 시각화하고 성능 병목 지점을 추적합니다.

---

## 🚀 API Endpoints

### Auth External API (`/api/v1/auth`)
| Method | Path | Description | Access |
| :--- | :--- | :--- | :--- |
| POST | `/login` | 사용자 로그인 및 토큰 발급 | Public |
| POST | `/logout` | 로그아웃 및 토큰 블랙리스트 등록 | User |
| POST | `/reissue` | Refresh Token을 통한 토큰 재발급 | User |

### User External API (`/api/v1/users`)
| Method | Path | Description | Access |
| :--- | :--- | :--- | :--- |
| POST | `/signup` | 회원가입 신청 | Public |
| GET | `/me` | 본인 정보 상세 조회 | User |
| GET | `/` | 사용자 목록 조회 (Paging, Filter) | ADMIN, HUB_ADMIN |
| GET | `/{userId}` | 특정 사용자 상세 조회 | ADMIN, HUB_ADMIN |
| POST | `/{userId}/registration` | 가입 승인 및 거절 | ADMIN, HUB_ADMIN |
| PATCH | `/{userId}` | 사용자 정보 수정 | ADMIN |
| PATCH | `/{userId}/role` | 사용자 권한 변경 | ADMIN |
| DELETE | `/{userId}` | 사용자 삭제 (Soft Delete) | ADMIN |

---

## 🔧 Installation & Build

1. **Prerequisites**
   - Docker & Docker Compose
   - JDK 17
   - Gradle

2. **Build**
   ```bash
   ./gradlew clean build -x test
   ```

3. **Run with Docker** (Root 디렉토리의 docker-compose 활용)
   ```bash
   docker-compose up -d user-service
   ```

---

## 💡 Technical Considerations

- **분산 트랜잭션 대응**: Keycloak 계정 생성과 로컬 DB 저장 사이의 데이터 불일치를 방지하기 위해 **보상 트랜잭션(Catch-Delete)** 로직을 구현하였습니다.
- **Custom Repository 패턴**: QueryDSL 로직을 `UserCustomRepository`로 분리하여 Repository 계층의 가독성과 유지보수성을 극대화하였습니다.
- **관측 가능성(Observability) 확보**: MSA 환경에서 장애 발생 시 원인 파악을 용이하게 하기 위해 Zipkin을 통한 전구간 트레킹(End-to-End Tracing)을 적용하였습니다.
- **보안 최적화**: 토큰의 남은 TTL을 계산하여 Redis Blacklist에 등록함으로써 메모리 자원을 효율적으로 관리합니다.
