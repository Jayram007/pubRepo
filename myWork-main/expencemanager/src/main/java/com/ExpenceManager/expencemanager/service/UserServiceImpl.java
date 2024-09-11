package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.UserDto;
import com.ExpenceManager.expencemanager.entity.User;
import com.ExpenceManager.expencemanager.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Override
    public void defaultUser() {
        User user=new User();
        user.setEmail("jayramkatkar7@gmail.com");
        user.setFirstName("user");
        user.setLastName("user");
        String encodedPassword = passwordEncoder.encode("1234");
        user.setPassword(encodedPassword);
        userRepo.save(user);
        System.out.println("User saved............");

    }

    @Override
    public String changeProfile(UserDto userDto, String userName) {

        User user=userRepo.findByFirstName(userName);
        user.setFirstName(userDto.getUserName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        userRepo.save(user);
        return userDto.getUserName();
    }

    @Override
    public String getEmail(String name) {
        return userRepo.findByFirstName(name).getEmail();
    }
}
