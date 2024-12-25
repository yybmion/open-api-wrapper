package ws_wrapper.mione.model;

public interface PublicDataResponse<T> {
    /**
     * API 응답의 실제 데이터를 반환
     */
    T getData();

    /**
     * API 응답의 결과 코드를 반환
     */
    String getResultCode();

    /**
     * API 응답의 결과 메시지를 반환
     */
    String getResultMessage();
}
