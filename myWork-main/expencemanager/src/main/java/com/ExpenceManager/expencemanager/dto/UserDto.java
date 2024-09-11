package com.ExpenceManager.expencemanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {


    private long userId=1L;

    private String password;

    private String confirmPassword;

    private String userName;

    private String email;


}
