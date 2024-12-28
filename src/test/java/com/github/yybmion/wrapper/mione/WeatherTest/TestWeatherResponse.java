package com.github.yybmion.wrapper.mione.WeatherTest;

import java.util.List;
import com.github.yybmion.wrapper.model.PublicDataResponse;

public class TestWeatherResponse implements PublicDataResponse<List<TestWeatherData>> {
    private Response response;  // 최상위 response 객체 추가

    // PublicDataResponse 인터페이스 구현
    @Override
    public List<TestWeatherData> getData() {
        return response.body.items.item;  // items.item으로 접근 방식 변경
    }

    @Override
    public String getResultCode() {
        return response.header.resultCode;
    }

    @Override
    public String getResultMessage() {
        return response.header.resultMsg;
    }

    // 내부 클래스들로 JSON 구조 매핑
    public static class Response {
        private Header header;
        private Body body;

        // getter/setter
        public Header getHeader() { return header; }
        public void setHeader(Header header) { this.header = header; }
        public Body getBody() { return body; }
        public void setBody(Body body) { this.body = body; }
    }

    public static class Header {
        private String resultCode;
        private String resultMsg;

        // getter/setter
        public String getResultCode() { return resultCode; }
        public void setResultCode(String resultCode) { this.resultCode = resultCode; }
        public String getResultMsg() { return resultMsg; }
        public void setResultMsg(String resultMsg) { this.resultMsg = resultMsg; }
    }

    public static class Body {
        private Items items;          // Items 타입으로 변경
        private String dataType;
        private int pageNo;
        private int numOfRows;
        private int totalCount;

        // Items 타입의 getter/setter만 남기고 List 타입은 제거
        public Items getItems() { return items; }
        public void setItems(Items items) { this.items = items; }

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }

        public int getPageNo() { return pageNo; }
        public void setPageNo(int pageNo) { this.pageNo = pageNo; }

        public int getNumOfRows() { return numOfRows; }
        public void setNumOfRows(int numOfRows) { this.numOfRows = numOfRows; }

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    }

    public static class Items {
        private List<TestWeatherData> item;  // 실제 데이터 배열

        public List<TestWeatherData> getItem() { return item; }
        public void setItem(List<TestWeatherData> item) { this.item = item; }
    }

    // response의 getter/setter
    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }
}
