# Public Data Portal API Wrapper

[공공데이터 포털 API](https://www.data.go.kr/index.do)를 쉽게 사용할 수 있는 자바 라이브러리입니다.

[![](https://jitpack.io/v/yybmion/open-api-wrapper.svg)](https://jitpack.io/#yybmion/open-api-wrapper)

## 특징
- 간단하고 사용하기 쉬운 API 래퍼
- 자동 재시도 메커니즘 (지수 백오프 적용)
- 응답 캐싱 지원
- 비동기 요청 지원

## 시작하기

### 1. JitPack 저장소 추가
```groovy
// settings.gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. 의존성 추가
```groovy
// build.gradle
dependencies {
    implementation 'com.github.yybmion:open-api-wrapper:1.0.8'
}
```

## 사용 방법

### 기본 사용
```java
// 클라이언트 생성
PublicDataClient client = new PublicDataClient.Builder()
    .serviceKey("your-service-key")
    .build();

// 요청 생성
RequestBuilder builder = new RequestBuilder("http://apis.data.go.kr/your-api-endpoint")
    .addParameter("pageNo", "1")
    .addParameter("numOfRows", "10")
    .addParameter("dataType", "JSON");

// API 호출
YourResponse response = client.request(builder.build(), YourResponse.class);
```

### 고급 설정
```java
PublicDataClient client = new PublicDataClient.Builder()
    .serviceKey("your-service-key")
    .connectTimeout(15)          // 연결 타임아웃 (초)
    .readTimeout(15)            // 읽기 타임아웃 (초)
    .maxRetries(3)             // 최대 재시도 횟수
    .cacheExpiration(10)       // 캐시 만료 시간 (분)
    .cacheSize(1000)          // 캐시 최대 크기
    .build();
```

### 비동기 요청
```java
CompletableFuture<YourResponse> future = client.requestAsync(builder.build(), YourResponse.class);
future.thenAccept(response -> {
    // 응답 처리
});
```

## 시스템 요구사항
- Java 8 이상
- 공공데이터 포털 API 서비스 인증키

## 의존성
- OkHttp3
- Jackson
- Google Guava
- SLF4J

## 라이센스
이 프로젝트는 MIT 라이센스 하에 있습니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 기여
버그 리포트, 기능 제안, Pull Request 모두 환영합니다. 
