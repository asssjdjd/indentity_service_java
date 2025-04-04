package com.phamthanhlong.identity_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.phamthanhlong.identity_service.dto.request.PermissionRequest;
import com.phamthanhlong.identity_service.dto.response.PermissionResponse;
import com.phamthanhlong.identity_service.entity.Permission;
import com.phamthanhlong.identity_service.mapper.PermissionMapper;
import com.phamthanhlong.identity_service.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PermissonService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}
