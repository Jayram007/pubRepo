package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.dto.UserDto;

public interface EmailService {
           void sendLoginEmail(String name);

    void sendChangeProfileEmail(String name, UserDto userDto);
}
