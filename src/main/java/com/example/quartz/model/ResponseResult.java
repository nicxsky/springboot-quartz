package com.example.quartz.model;

public class ResponseResult<T> extends Response {
    private T data;

    public ResponseResult(T data) {
        this.data = data;
    }

    public ResponseResult(ExceptionMsg msg) {
        super(msg);
    }

    public ResponseResult(String rspCode, String rspMsg) {
        super(rspCode, rspMsg);
    }

    public ResponseResult(String rspCode, String rspMsg, T data) {
        super(rspCode, rspMsg);
        this.data = data;
    }

    public ResponseResult(ExceptionMsg msg, T data) {
        super(msg);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "data=" + data +
                "} " + super.toString();
    }
}
