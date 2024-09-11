package com.ExpenceManager.expencemanager.controller;

import com.ExpenceManager.expencemanager.dto.SearchDto;
import com.ExpenceManager.expencemanager.dto.UserDto;
import com.ExpenceManager.expencemanager.service.EmailService;
import com.ExpenceManager.expencemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @GetMapping("/loggingSuccess")
    public String loginSuccessful(Model model, Principal principal){

         emailService.sendLoginEmail(principal.getName());
        if(principal.getName().equals("user")) {
            model.addAttribute("username", principal.getName());
            model.addAttribute("email", userService.getEmail(principal.getName()));
            return "UserProfile";
        }
        return "redirect:/";
    }

    @PostMapping("/changeProfile")
    public String changeProfile(@ModelAttribute("userDto")UserDto userDto, Model model, Principal principal){

        if(userDto.getPassword().equals(userDto.getConfirmPassword())) {
            emailService.sendChangeProfileEmail(userService.changeProfile(userDto, principal.getName()), userDto);
            return "redirect:/logout";
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("email",userService.getEmail(principal.getName()) );
        return "UserProfile";
    }
    @GetMapping("/updateProfile")
    public String updateProfile(Model model, Principal principal){

        model.addAttribute("username", principal.getName());
        model.addAttribute("email",userService.getEmail(principal.getName()) );
        return "UserProfile";
    }

}
