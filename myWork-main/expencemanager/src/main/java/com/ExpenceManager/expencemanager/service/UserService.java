package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.UserDto;

public interface UserService {
    public void defaultUser();

    String changeProfile(UserDto userDto, String username);

    String getEmail(String name);
}
