package com.bookstore.services;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.IRoleRepository;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServices {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public void save(User user){
        userRepository.save(user);
        Long userId = userRepository.getUserIdByUserName(user.getUsername());
        Long roleId = roleRepository.getRoleIdByName("USER");
        if(roleId != 0 && userId != 0){
            userRepository.addRoleToUser(userId, roleId);
        }
    }

    public void assignRoleToUser(String username, String roleName) {
        Long userId = userRepository.getUserIdByUserName(username);
        Long roleId = roleRepository.getRoleIdByName(roleName);
        if(roleId != 0 && userId != 0){
            userRepository.addRoleToUser(userId, roleId);
        }
    }

    public List<Role> getRolesByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getRoles();
        }
        return List.of();
    }
}
