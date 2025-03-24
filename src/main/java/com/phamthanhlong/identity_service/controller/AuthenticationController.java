package com.phamthanhlong.identity_service.controller;

import com.nimbusds.jose.JOSEException;
import com.phamthanhlong.identity_service.dto.request.AuthenticationRequest;
import com.phamthanhlong.identity_service.dto.request.IntrospeactRequest;
import com.phamthanhlong.identity_service.dto.response.ApiResponse;
import com.phamthanhlong.identity_service.dto.response.AuthenticationResponse;
import com.phamthanhlong.identity_service.dto.response.IntrospectResponse;
import com.phamthanhlong.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .response(result)
                .build();

    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospeactRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .response(result)
                .build();

    }
}
