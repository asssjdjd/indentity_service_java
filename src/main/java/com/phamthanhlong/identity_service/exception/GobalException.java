package com.phamthanhlong.identity_service.exception;

import com.phamthanhlong.identity_service.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GobalException {

//    xu ly lop runtimeexception vi du nhu la sai link ,....
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception){
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErorrCode.RUNTIME_EXCEPTION.getCode());
        apiResponse.setMessage(ErorrCode.RUNTIME_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = UserException.class)
    ResponseEntity<ApiResponse> handlingUserException(UserException exception) {
        ApiResponse apiResponse = new ApiResponse();
        ErorrCode erorrCode = exception.getErorrCode();
        apiResponse.setCode(erorrCode.getCode());
        apiResponse.setMessage(erorrCode.getMessage());

        return ResponseEntity
                .status(erorrCode.getStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ApiResponse apiResponse = new ApiResponse();
        ErorrCode erorrCode = ErorrCode.UNAUTHORIZED;

        apiResponse.setCode(erorrCode.getCode());
        apiResponse.setMessage(erorrCode.getMessage());

        return ResponseEntity
                .status(erorrCode.getStatusCode())
                .body(apiResponse);

    }



//    xu ly lien quan den cac lop nhu la trung ten sai validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException ex) {
        ApiResponse apiResponse = new ApiResponse();
        ErorrCode erorrCode = ErorrCode.VALIDATION;
        apiResponse.setCode(erorrCode.getCode());
        apiResponse.setMessage(ex.getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest()
                            .header("X-Error-Code", String.valueOf(erorrCode.getCode())) // Thêm header key-value
                            .header("X-Error-Message", erorrCode.getMessage()) // Thêm header chứa message
                            .body(apiResponse)
                            ;
    }



}
