package com.phamthanhlong.identity_service.exception;

public class UserException extends RuntimeException {

    public UserException(ErorrCode erorrCode) {
        super(erorrCode.getMessage()); // lay phan runtimeException va thay no bang errorCode
        this.erorrCode = erorrCode;
    }

    private ErorrCode erorrCode;

    public ErorrCode getErorrCode() {
        return erorrCode;
    }

    public void setErorrCode(ErorrCode erorrCode) {
        this.erorrCode = erorrCode;
    }
}
