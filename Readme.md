# 📰 Outsourcing Project

## 📌 프로젝트 소개

- Redis와 캐싱의 성능을 확인하기 위한 온라인 경매 시스템 백엔드 API 개발 미니 프로젝트 입니다.
- 기능은 최소한만 구현하고, RDB만 사용했을 때와 Redis를 도입했을 때의 성능 차이를 실험하여 Redis의 성능을 체험합니다.
- 다양한 유저가 동시에 입찰을 하게되어 동시성 문제가 발생될 때 해결하는 방법을 찾았습니다.

## 🗓 개발 기간 🕰

- 2025.05.16 금 - 2025.05.26 월

## 🎯 프로젝트 목적

- 이 프로젝트의 목적은 **캐싱 성능과 Redis의 활용 및 동시성 문제 해결**을 다양한 방법으로 시도해보는 것입니다.
- 이 과정을 수행하기 위해 API과 기능은 필요한 기능만 빠르게 구현하였습니다.
- 경매장 시스템이므로 악의적인 로그인 요청에 즉각 대응하기 위해서 JWT가 아니라 세션으로 로그인을 구현하였습니다.
- 캐싱(Redis) 데이터와 RDB 데이터 정합성을 맞추기 위해 스케줄러, 벌크연산, Redis Pub/Sub, 스프링 시큐리티 생명주기 콜백처리 등의 기술을 사용하였습니다.
- 테스트코드를 통해 동시성 문제가 잘 해결되었는지 확인하였습니다

---

## 🛠 **사용 기술 스택**

## 기술 스택

### 백엔드

| 분야         | 기술                                      |
|------------|-----------------------------------------|
| **프레임워크**  | Spring Boot                             |
| **보안**     | Spring Security                         |
| **데이터베이스** | MySQL, Redis                            |
| **데이터 처리** | Spring Data JPA, QueryDSL, JdbcTemplate |
| **인증 관리**  | Session                                 |

---

## 개발 단계 🚀

### 1️⃣ 기능별 Mapping

| **User/Auth**     | **Product**          | **Dips(찜)** | **Bid<br/>(입찰)** | **WonItem(낙찰된 아이템)** | **Order**  | **Popular-keywords** |
|-------------------|----------------------|-------------|------------------|----------------------|------------|----------------------|
| 회원가입<br/>(이메일 인증) | 상품 등록                | 찜 등록        | 수동 입찰            | 낙찰된 아이템 생성 (자동)      | 주문 생성(수동)  | 인기 검색어 조회            |
| 로그인<br/>(OAuth2)  | 상품 수정                | 찜 취소        | 자동 낙찰            | 낙찰된 아이템 생성 (생성)      | 주문 생성(자동)  |
| 로그아웃              | 상품 삭제                | 내 찜 목록 조회   |                  |                      | 내 주문 전체 조회 |                      |
|                   | 상품 전체 조회<br/>(검색)    |             |                  |                      |            |                      |
|                   | 상품 단건 조회             |             |                  |                      |            |                      |
|                   | 조회수 기능(Redis, 벌크 연산) |             |                  |                      |            |                      |

## 🗂️ 계층 구조 (MVC + Service + Repository)

```
src
└── main
    └── java
        └── com.example.auction
            ├── common
            │   ├── annotation
            │   ├── constant
            │   ├── dto
            │   ├── entity
            │   ├── enums
            │   ├── exception
            │   ├── handler
            │   ├── listener
            │   ├── response
            │   ├── scheduler
            │   ├── service
            │   └── util
            │
            ├── config
            │   └── security
            │
            └── domain
                ├── auctionbid
                │   ├── controller
                │   ├── dto
                │   ├── entity
                │   ├── exception
                │   ├── handler
                │   ├── repository
                │   └── service
                │
                ├── dips
                │   ├── controller
                │   ├── dto
                │   ├── entity
                │   ├── exception
                │   ├── handler
                │   ├── repository
                │   └── service
                │
                ├── image
                │   ├── entity
                │   ├── exception
                │   ├── repository
                │   └── service
                │
                ├── order
                │   ├── controller
                │   ├── dto
                │   ├── entity
                │   ├── exception
                │   ├── handler
                │   ├── repository
                │   └── service
                │
                ├── product
                │   ├── controller
                │   ├── dto
                │   ├── entity
                │   ├── exception
                │   ├── repository
                │   └── service
                │
                ├── searchLog
                │   ├── dto
                │   ├── entity
                │   ├── repository
                │   └── service
                │
                ├── topKeyword
                │   ├── controller
                │   ├── dto                
                │   ├── entity
                │   ├── repository
                │   └── service
                │
                ├── user
                │   ├── auth
                │   │   ├── controller
                │   │   ├── dto
                │   │   ├── exception
                │   │   ├── handler
                │   │   ├── security
                │   │   └── service
                │   ├── userInfo
                │   ├── entity
                │   ├── exception
                │   └── repository
                │
                └── wonitem
                    ├── controller
                    ├── dto
                    │   ├── request
                    │   └── response
                    ├── entity
                    ├── exception
                    └── service
```

---

## 추가 정보 ℹ️

- ERD 다이어그램: https://www.erdcloud.com/d/peLkqAj3saMP5eNSQ

### 🔍 검색 API에 Cache(Caffeine)를 적용한 이유

#### 🛠️ 스케줄러 방식의 한계

- 서버 확장 시 스케줄러 중복 실행으로 성능 저하 및 데이터 중복 발생 가능
- 삭제 후 삽입 중 에러 발생 시 인기 검색어 공백 문제 발생

#### 💾 불필요한 DB 부하

- 검색어 저장과 집계를 위해 테이블 2개, 로직 2배로 복잡도 증가
- 검색 시마다 DB 접근으로 과도한 I/O 발생

#### ⚡️ 성능 개선과 구조 단순화

- Caffeine은 JVM 메모리 기반으로 빠르고 가벼움
- TTL 설정으로 스케줄러 없이도 자동 만료 처리 가능
