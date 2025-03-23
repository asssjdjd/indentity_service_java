package com.phamthanhlong.identity_service.controller;

import com.phamthanhlong.identity_service.dto.request.UserCreationRequest;
import com.phamthanhlong.identity_service.dto.request.UserUpdateRequest;
import com.phamthanhlong.identity_service.dto.response.ApiResponse;
import com.phamthanhlong.identity_service.dto.response.UserResponse;
import com.phamthanhlong.identity_service.entity.User;
import com.phamthanhlong.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResponse(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    List<User> showusers() {
        return userService.showUsers();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> showUser(@PathVariable("userId") String userId) {

        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResponse(userService.showUser(userId));
        return apiResponse;
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId,@RequestBody UserUpdateRequest request){
        return userService.updateUser(userId,request);
    }
    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable("userId") String userId) {
         userService.deleteUser(userId);
    }


}
