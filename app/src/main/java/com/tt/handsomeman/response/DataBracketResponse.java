package com.tt.handsomeman.response;

public class DataBracketResponse<T> extends StandardResponse {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
