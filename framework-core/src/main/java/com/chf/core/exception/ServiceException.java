package com.chf.core.exception;

import java.util.function.Supplier;

import com.chf.core.constants.ErrorCodeContants;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;

    public ServiceException(Exception e) {
        super(e);
        if (e instanceof ServiceException) {
            this.code = ((ServiceException) e).getCode();
        } else {
            this.code = ErrorCodeContants.OTHER_ERROR;
        }
    }

    public ServiceException(ServiceException e) {
        this(e.getCode(), e.getMessage());
    }

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public ServiceException(String code) {
        this(code, code);
    }

    public String getCode() {
        return code;
    }

    public static Supplier<ServiceException> exceptionSupplier(String code, String msg) {
        return () -> new ServiceException(code, msg);
    }

    public static Supplier<ServiceException> exceptionSupplier(String code) {
        return () -> new ServiceException(code);
    }

}
