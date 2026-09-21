package com.bhahi.hr.exception;

public class CustomException extends RuntimeException {
    private final String header;
    private final String message;
    private final int statusCode;

    public String getHeader() {
        return header;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public CustomException(String header, String message, int statusCode){
        this.header=header;
        this.message=message;
        this.statusCode=statusCode;
    };
    public CustomException(){
        header="Something went wrong.";
        message="Unknown error, please try again.";
        statusCode=500;
    }
}
