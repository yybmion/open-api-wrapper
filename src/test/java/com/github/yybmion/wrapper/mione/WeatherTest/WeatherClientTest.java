package com.github.yybmion.wrapper.mione.WeatherTest;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.yybmion.wrapper.client.PublicDataClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.github.yybmion.wrapper.builder.RequestBuilder;

public class WeatherClientTest {

    private static final String SERVICE_KEY = "{YOUR-SERVICE-KEY}";

    @Test
    @DisplayName("날씨 API 실제 호출 테스트")
    void testRealApiCall() {
        // given
        LocalDateTime now = LocalDateTime.now();
        // 현재 시간을 기상청 API 포맷에 맞게 처리
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 기상청 API는 정시 단위로만 제공되므로 시간을 조정
        String baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));

        RequestBuilder builder = new RequestBuilder(
                "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst")
                .addParameter("pageNo", "1")
                .addParameter("numOfRows", "1000")
                .addParameter("dataType", "JSON")
                .addParameter("base_date", baseDate)
                .addParameter("base_time", baseTime)
                .addParameter("nx", "55")
                .addParameter("ny", "127");

        // when
        PublicDataClient client = new PublicDataClient.Builder()
                .serviceKey(SERVICE_KEY)
                .build();

        TestWeatherResponse response = client.request(
                builder.build(),
                TestWeatherResponse.class
        );

        // 응답 데이터 로깅
        System.out.println("Response Code: " + response.getResultCode());
        System.out.println("Response Message: " + response.getResultMessage());

        // then (수정된 검증)
        if ("00".equals(response.getResultCode())) {
            // 성공 케이스
            assertAll(
                    () -> assertNotNull(response),
                    () -> assertNotNull(response.getData()),
                    () -> assertFalse(response.getData().isEmpty())
            );
            response.getData().forEach(System.out::println);
        } else {
            // 실패 케이스의 원인 파악
            System.out.println("API call failed with code: " + response.getResultCode());
            System.out.println("Error message: " + response.getResultMessage());
            fail("API call failed: " + response.getResultMessage());
        }
    }
}
