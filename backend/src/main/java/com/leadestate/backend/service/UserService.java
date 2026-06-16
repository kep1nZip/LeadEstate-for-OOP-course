package com.leadestate.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadestate.backend.entity.User;
import com.leadestate.backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // CREATE USER
    public User createUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email sudah terdaftar");
        }

        user.setRoleId(3);
        return userRepository.save(user);
    }

    // UPDATE USER
    public User updateUser(Integer id, User user) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // VALIDASI EMAIL
        if (!existingUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(user.getEmail())) {

            throw new RuntimeException("Email sudah digunakan");
        }

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRoleId(user.getRoleId());

        return userRepository.save(existingUser);
    }

    
    public String deleteUser(Integer id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

    String name = user.getName();

    userRepository.delete(user);

    return "User " + name + " dengan id " + id + " berhasil dihapus";
}
public User updateProfile(Integer id, User request) {

    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

    // update hanya field profile
    user.setName(request.getName());
    user.setEmail(request.getEmail());

    // password
    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
        user.setPassword(request.getPassword());
    }

    return userRepository.save(user);
}
public User updateUserRole(Integer id, Integer roleId) {

    User user = userRepository.findById(id)
        .orElseThrow(() ->
            new RuntimeException(
                "User tidak ditemukan"
            ));

    if (roleId == 1) {
        user.setRoleId(1);
    } else {
        user.setRoleId(3);
    }

    return userRepository.save(user);
}
}