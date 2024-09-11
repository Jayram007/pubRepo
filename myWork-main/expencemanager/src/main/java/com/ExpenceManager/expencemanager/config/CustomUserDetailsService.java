package com.ExpenceManager.expencemanager.config;

import com.ExpenceManager.expencemanager.entity.User;
import com.ExpenceManager.expencemanager.repo.UserRepo;
import com.ExpenceManager.expencemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userRepo.count()==0)
            userService.defaultUser();

        User user=userRepo.findByFirstName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }
}
