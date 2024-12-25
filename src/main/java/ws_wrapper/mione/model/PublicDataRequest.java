package ws_wrapper.mione.model;

import java.util.Map;

public interface PublicDataRequest {
    /**
     * API의 엔드포인트 URL을 반환
     */
    String getEndPoint();

    /**
     * API 호출에 필요한 파라미터 맵을 반환
     */
    Map<String,String> getParameter();
}
