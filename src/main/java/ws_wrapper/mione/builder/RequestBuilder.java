package ws_wrapper.mione.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ws_wrapper.mione.model.PublicDataRequest;

public class RequestBuilder {
    private final String endpoint;
    private final Map<String, String> parameters;

    public RequestBuilder(String endpoint, Map<String, String> parameters) {
        this.endpoint = endpoint;
        this.parameters = new HashMap<>();
    }

    /**
     * API 요청 파라미터를 추가
     *
     * @param key   파라미터 키
     * @param value 파라미터 값
     * @return 현재 빌더 인스턴스
     */
    public RequestBuilder addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    /**
     * 여러 파라미터를 한번에 추가
     *
     * @param params 파라미터 맵
     * @return 현재 빌더 인스턴스
     */
    public RequestBuilder addParameters(Map<String, String> params) {
        parameters.putAll(params);
        return this;
    }

    /**
     * PublicDataRequest 인스턴스 생성
     */
    public PublicDataRequest build() {
        return new PublicDataRequest() {
            @Override
            public String getEndpoint() {
                return endpoint;
            }

            @Override
            public Map<String, String> getParameters() {
                return Collections.unmodifiableMap(parameters);
            }
        };
    }
}
