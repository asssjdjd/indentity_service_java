package com.phamthanhlong.identity_service.service;

import com.phamthanhlong.identity_service.dto.request.UserCreationRequest;
import com.phamthanhlong.identity_service.dto.request.UserUpdateRequest;
import com.phamthanhlong.identity_service.dto.response.UserResponse;
import com.phamthanhlong.identity_service.entity.User;
import com.phamthanhlong.identity_service.exception.ErorrCode;
import com.phamthanhlong.identity_service.exception.UserException;
import com.phamthanhlong.identity_service.mapper.UserMapper;
import com.phamthanhlong.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UserService {

   UserRepository userRepository;
   UserMapper userMapper;

    public User createUser(UserCreationRequest request) {

        if(userRepository.existsByUsername(request.getUsername()))
            throw new UserException(ErorrCode.USER_EXSITED);

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public List<User> showUsers(){
        return userRepository.findAll();
    }

    public UserResponse showUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErorrCode.USER_NOTFOUND)));
    }

    public UserResponse updateUser(String userId,UserUpdateRequest request){
        User userUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErorrCode.USER_NOTFOUND));
        // tim truc tiep qua repo boi vi neu dung ham kia se tra ve UserResponse

       userMapper.updateUser(userUpdate,request);
//       map request ve user

        return userMapper.toUserResponse(userRepository.save(userUpdate));
//        luu vao database la user nhg tra ve lai la UserResponse

    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
