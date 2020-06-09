package com.cisdi.cpm.auth.exception;

public class SysException extends BaseException {
    private static final long serialVersionUID = 1L;

    public SysException(){}

    public SysException(String msg) {
        super(msg);
    }

    public SysException(Throwable cause){
        super(cause);
    }

    public SysException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
