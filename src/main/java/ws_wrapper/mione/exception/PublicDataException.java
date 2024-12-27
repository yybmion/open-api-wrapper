package ws_wrapper.mione.exception;

public class PublicDataException extends RuntimeException {
    // 공공데이터 포털 표준 에러 코드
    public static final String NORMAL_SERVICE = "00";                          // 정상
    public static final String APPLICATION_ERROR = "01";                       // 어플리케이션 에러
    public static final String DB_ERROR = "02";                               // 데이터베이스 에러
    public static final String NODATA_ERROR = "03";                           // 데이터없음 에러
    public static final String HTTP_ERROR = "04";                             // HTTP 에러
    public static final String SERVICE_TIMEOUT = "05";                        // 서비스 연결실패 에러
    public static final String INVALID_REQUEST_PARAMETER_ERROR = "10";        // 잘못된 요청 파라메터 에러

    private final String errorCode;

    public PublicDataException(String message, String errorCode) {
        super(getDetailedMessage(errorCode, message));
        this.errorCode = errorCode;
    }

    private static String getDetailedMessage(String errorCode, String message) {
        StringBuilder detailedMessage = new StringBuilder();

        switch(errorCode) {
            case NORMAL_SERVICE:
                detailedMessage.append("[정상] 정상 처리되었습니다.");
                break;

            case APPLICATION_ERROR:
                detailedMessage.append("[어플리케이션 에러] 어플리케이션 실행 중 오류가 발생했습니다. ")
                        .append("잠시 후 다시 시도해주세요.");
                break;

            case DB_ERROR:
                detailedMessage.append("[데이터베이스 에러] 데이터베이스 처리 중 오류가 발생했습니다. ")
                        .append("잠시 후 다시 시도해주세요.");
                break;

            case NODATA_ERROR:
                detailedMessage.append("[데이터없음 에러] 요청한 데이터가 없습니다. ")
                        .append("요청 파라미터를 확인해주세요.");
                break;

            case HTTP_ERROR:
                detailedMessage.append("[HTTP 에러] HTTP 통신 중 오류가 발생했습니다. ")
                        .append("네트워크 연결을 확인해주세요.");
                break;

            case SERVICE_TIMEOUT:
                detailedMessage.append("[서비스 연결실패 에러] 서비스 연결에 실패했습니다. ")
                        .append("잠시 후 다시 시도해주세요.");
                break;

            case INVALID_REQUEST_PARAMETER_ERROR:
                detailedMessage.append("[잘못된 요청 파라메터 에러] 요청 파라미터가 잘못되었습니다. ")
                        .append("API 문서를 참고하여 파라미터를 확인해주세요.");
                break;

            default:
                detailedMessage.append("[알 수 없는 에러] 알 수 없는 에러가 발생했습니다.");
        }

        detailedMessage.append("\n상세 메시지: ").append(message);
        return detailedMessage.toString();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
