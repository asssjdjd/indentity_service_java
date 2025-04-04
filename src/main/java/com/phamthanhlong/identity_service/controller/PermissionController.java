package com.phamthanhlong.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.phamthanhlong.identity_service.dto.request.PermissionRequest;
import com.phamthanhlong.identity_service.dto.response.ApiResponse;
import com.phamthanhlong.identity_service.dto.response.PermissionResponse;
import com.phamthanhlong.identity_service.service.PermissonService;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
public class PermissionController {
    PermissonService permissonService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .response(permissonService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .response(permissonService.getAll())
                .build();
    }

    @DeleteMapping({"/{permission}"})
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissonService.deletePermission(permission);
        return ApiResponse.<Void>builder().build();
    }
}
