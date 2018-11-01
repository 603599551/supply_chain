package com.exception;

public class PcException extends BaseException {

    private String code;
    private String msg;

    public PcException(String code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
