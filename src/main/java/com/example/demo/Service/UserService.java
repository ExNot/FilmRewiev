package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    public Optional<User> getUserByUserName(String userName){
        return userRepository.findByUsername(userName);
    }

    /*public boolean authenticateUser(String username, String password){
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()){
            User user = userOptional.get();
            return user.getPassword().equals(password);
        } else{
            return false;
        }
    }*/

}
