package com.phamthanhlong.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErorrCode {
    RUNTIME_EXCEPTION(9999,"runtime exception",HttpStatus.INTERNAL_SERVER_ERROR), // loi 500
    USER_EXSITED(1002, "User exited",HttpStatus.BAD_REQUEST),
    VALIDATION(1003, "invalid type input",HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(1004,"not found user",HttpStatus.BAD_REQUEST),
    USERNOT_EXISTED(1005,"user not exit",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"you don't have permisssion",HttpStatus.FORBIDDEN)
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;


    ErorrCode(int code, String message,HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }



}
