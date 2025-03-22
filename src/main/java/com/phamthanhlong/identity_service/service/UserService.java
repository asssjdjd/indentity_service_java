package com.phamthanhlong.identity_service.service;

import com.phamthanhlong.identity_service.dto.request.UserCreationRequest;
import com.phamthanhlong.identity_service.dto.request.UserUpdateRequest;
import com.phamthanhlong.identity_service.entity.User;
import com.phamthanhlong.identity_service.exception.ErorrCode;
import com.phamthanhlong.identity_service.exception.UserException;
import com.phamthanhlong.identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request) {
        User user = new User();

        if(userRepository.existsByUsername(request.getUsername()))
            throw new UserException(ErorrCode.USER_EXSITED);


        user.setUsername(request.getUsername());
        user.setDob(request.getDob());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPassword(request.getPassword());

        return userRepository.save(user);
    }

    public List<User> showUsers(){
        return userRepository.findAll();
    }

    public User showUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErorrCode.USER_NOTFOUND));
    }

    public User updateUser(String userId,UserUpdateRequest request){
        User userUpdate = showUser(userId);

        userUpdate.setPassword(request.getPassword());
        userUpdate.setDob(request.getDob());
        userUpdate.setFirstname(request.getFirstname());
        userUpdate.setLastname(request.getLastname());

        return userRepository.save(userUpdate);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
