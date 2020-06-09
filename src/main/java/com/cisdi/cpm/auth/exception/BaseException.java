package com.cisdi.cpm.auth.exception;

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BaseException(){}

    public BaseException(String msg){
        super(msg);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
