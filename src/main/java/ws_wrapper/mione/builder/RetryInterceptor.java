package ws_wrapper.mione.builder;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws_wrapper.mione.exception.PublicDataException;

public class RetryInterceptor implements Interceptor {
    private final int maxRetries;
    private static final Logger logger = LoggerFactory.getLogger(RetryInterceptor.class);

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int tryCount = 0;
        IOException lastException = null;

        while (tryCount < maxRetries) {
            try {
                Response response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                }

                // 응답이 실패인 경우 body를 닫아주어야 리소스 누수 방지
                if (response.body() != null) {
                    response.close();
                }
            } catch (IOException e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", tryCount + 1, e.getMessage());
            }

            tryCount++;
            if (tryCount < maxRetries) {
                long delayMillis = calculateDelay(tryCount);
                logger.info("Retrying in {} ms", delayMillis);
                sleep(delayMillis);
            }
        }

        throw new PublicDataException("Failed after " + maxRetries + " retries", lastException);
    }

    // 지수 백오프로 재시도 대기 시간 계산
    private long calculateDelay(int retryCount) {
        return (long) (Math.pow(2, retryCount - 1) * 1000);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
