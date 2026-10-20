package com.bhahi.hr.dto;

/**
 * Simple DTO shared between the "hrmod" and "hrmodule" modules via the
 * "common" artifact. Used to demonstrate that both modules can import and use
 * classes declared in the common module.
 *
 * <p>Replace/extend this with your real shared DTOs.</p>
 */
public class CommonResponse {

    private boolean success;
    private String message;
    private Object data;

    /** No-arg constructor so callers can create an "empty" object easily. */
    public CommonResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
