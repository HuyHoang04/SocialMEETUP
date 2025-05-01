package com.socialmedia.demo.responses;


public class ApiResponse <T> {
    private String code;
    private T result;
    
    public T getResult() {
        return result;
    }
    
    public void setResult(T result) {
        this.result = result;
    }

    public void setCode(String string) {
        this.code = string;
    }
}
