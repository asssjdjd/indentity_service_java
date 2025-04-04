package com.phamthanhlong.identity_service.configuraion;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phamthanhlong.identity_service.dto.response.ApiResponse;
import com.phamthanhlong.identity_service.exception.ErorrCode;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        ErorrCode errorCode = ErorrCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value()); // set tatus
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // set body tra ve dang Json rat quan trong

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper(); // chuyen ve string

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse)); // convert ve dang string

        response.flushBuffer(); // commit response
    }
}
