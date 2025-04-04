package com.phamthanhlong.identity_service.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phamthanhlong.identity_service.dto.request.UserCreationRequest;
import com.phamthanhlong.identity_service.dto.request.UserUpdateRequest;
import com.phamthanhlong.identity_service.dto.response.UserResponse;
import com.phamthanhlong.identity_service.entity.User;
import com.phamthanhlong.identity_service.enums.Role;
import com.phamthanhlong.identity_service.exception.ErorrCode;
import com.phamthanhlong.identity_service.exception.UserException;
import com.phamthanhlong.identity_service.mapper.UserMapper;
import com.phamthanhlong.identity_service.repository.RoleRepository;
import com.phamthanhlong.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) throw new UserException(ErorrCode.USER_EXSITED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());

        //        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new UserException(ErorrCode.USER_NOTFOUND));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasAuthority('APPROVE_POST') or hasRole('ADMIN')")
    public List<UserResponse> showUsers() {
        log.info("in method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse showUser(String id) {
        log.info("in method get user by id");
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new UserException(ErorrCode.USER_NOTFOUND)));
    }

    //    them roles
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User userUpdate = userRepository.findById(userId).orElseThrow(() -> new UserException(ErorrCode.USER_NOTFOUND));
        // tim truc tiep qua repo boi vi neu dung ham kia se tra ve UserResponse

        userMapper.updateUser(userUpdate, request);
        userUpdate.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        userUpdate.setRoles(new HashSet<>(roles));

        //       map request ve user;
        return userMapper.toUserResponse(userRepository.save(userUpdate));
        //        luu vao database la user nhg tra ve lai la UserResponse

    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
