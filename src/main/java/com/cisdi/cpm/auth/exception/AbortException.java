package com.cisdi.cpm.auth.exception;

public class AbortException extends BaseException{
    private static final long serialVersionUID = 1L;

    public AbortException(){}

    public AbortException(String msg) {
        super(msg);
    }

    public AbortException(Throwable cause) {
        super(cause);
    }

    public AbortException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
