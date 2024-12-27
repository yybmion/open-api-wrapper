package ws_wrapper.mione.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws_wrapper.mione.exception.PublicDataException;
import ws_wrapper.mione.model.PublicDataRequest;

public class PublicDataClient {
    private final String serviceKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, Object> cache;
    private static final Logger logger = LoggerFactory.getLogger(PublicDataClient.class);

    private PublicDataClient(Builder builder) {
        this.serviceKey = builder.serviceKey;
        this.objectMapper = new ObjectMapper();

        // OkHttpClient 설정
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(builder.connectTimeout, TimeUnit.SECONDS)
                .readTimeout(builder.readTimeout, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor(builder.maxRetries))
                .build();

        // 캐시 설정
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(builder.cacheExpiration, TimeUnit.MINUTES)
                .maximumSize(builder.cacheSize)
                .build();
    }

    /**
     * 동기식 API 요청 수행
     */
    public <T> T request(PublicDataRequest request, Class<T> responseType) {
        String cacheKey = generateCacheKey(request);

        // 캐시된 응답이 있는지 확인
        Object cachedResponse = cache.getIfPresent(cacheKey);
        if (cachedResponse != null) {
            logger.debug("Cache hit for key: {}", cacheKey);
            return responseType.cast(cachedResponse);
        }

        String url = buildUrl(request);
        logger.debug("Making request to: {}", url);

        try {
            Response response = executeRequest(url);
            if (!response.isSuccessful()) {
                String errorMessage = response.message();
                String errorCode;

                switch (response.code()) {
                    case 400:
                        errorCode = PublicDataException.INVALID_REQUEST_PARAMETER_ERROR;
                        break;
                    case 404:
                        errorCode = PublicDataException.NODATA_ERROR;
                        break;
                    case 500:
                        errorCode = PublicDataException.APPLICATION_ERROR;
                        break;
                    case 503:
                        errorCode = PublicDataException.SERVICE_TIMEOUT;
                        break;
                    default:
                        errorCode = PublicDataException.HTTP_ERROR;
                }
                throw new PublicDataException(errorMessage, errorCode);
            }

            T result = parseResponse(response, responseType);
            cache.put(cacheKey, result);
            return result;
        } catch (IOException e) {
            throw new PublicDataException("Network error: " + e.getMessage(),
                    PublicDataException.HTTP_ERROR);
        } catch (Exception e) {
            if (e instanceof PublicDataException) {
                throw e;
            }
            throw new PublicDataException("Unexpected error: " + e.getMessage(),
                    PublicDataException.APPLICATION_ERROR);
        }
    }

    /**
     * 비동기식 API 요청 수행
     */
    public <T> CompletableFuture<T> requestAsync(PublicDataRequest request, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> request(request, responseType));
    }

    // URL 생성
    private String buildUrl(PublicDataRequest request) {
        StringBuilder urlBuilder = new StringBuilder(request.getEndpoint());
        urlBuilder.append("?serviceKey=").append(URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));

        for (Map.Entry<String, String> entry : request.getParameters().entrySet()) {
            urlBuilder.append("&")
                    .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return urlBuilder.toString();
    }

    // HTTP 요청 실행
    private Response executeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", "application/json")
                .build();

        return httpClient.newCall(request).execute();
    }

    // 응답 파싱
    private <T> T parseResponse(Response response, Class<T> responseType) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body == null) {
                throw new PublicDataException(
                        "Empty response body from server",
                        PublicDataException.HTTP_ERROR  // HTTP_ERROR로 처리
                );
            }

            String responseBody = body.string();
            logger.debug("Response body: {}", responseBody);

            try {
                return objectMapper.readValue(responseBody, responseType);
            } catch (Exception e) {
                // JSON 파싱 실패는 APPLICATION_ERROR로 처리
                throw new PublicDataException(
                        "Failed to parse response: " + e.getMessage(),
                        PublicDataException.APPLICATION_ERROR
                );
            }
        } catch (IOException e) {
            // IO 관련 에러는 DB_ERROR 또는 HTTP_ERROR로 처리
            throw new PublicDataException(
                    "Failed to read response: " + e.getMessage(),
                    PublicDataException.HTTP_ERROR
            );
        }
    }

    // 캐시 키 생성
    private String generateCacheKey(PublicDataRequest request) {
        return request.getEndpoint() +
                request.getParameters().toString() +
                serviceKey;
    }

    // Builder 클래스
    public static class Builder {
        private String serviceKey;
        private int connectTimeout = 10;
        private int readTimeout = 10;
        private int maxRetries = 3;
        private int cacheExpiration = 10;
        private int cacheSize = 1000;

        public Builder serviceKey(String serviceKey) {
            this.serviceKey = serviceKey;
            return this;
        }

        // 연결 타임아웃 설정
        public Builder connectTimeout(int seconds) {
            this.connectTimeout = seconds;
            return this;
        }

        // 읽기 타임아웃 설정
        public Builder readTimeout(int seconds) {
            this.readTimeout = seconds;
            return this;
        }

        // 최대 재시도 횟수 설정
        public Builder maxRetries(int retries) {
            this.maxRetries = retries;
            return this;
        }

        // 캐시 만료 시간 설정
        public Builder cacheExpiration(int minutes) {
            this.cacheExpiration = minutes;
            return this;
        }

        // 캐시 최대 크기 설정
        public Builder cacheSize(int size) {
            this.cacheSize = size;
            return this;
        }

        public PublicDataClient build() {
            if (serviceKey == null || serviceKey.isEmpty()) {
                throw new IllegalArgumentException("Service key is required");
            }
            return new PublicDataClient(this);
        }
    }
}
