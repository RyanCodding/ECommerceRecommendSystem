package com.business.rest;

import com.business.model.domain.User;
import com.business.model.request.LoginUserRequest;
import com.business.model.request.RegisterUserRequest;
import com.business.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/rest/users")
@RestController
public class UserRestApi {

    @Resource
    private UserService userService;

    @GetMapping(value = "/login", produces = "application/json")
    public Model login(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        User user  =userService.loginUser(new LoginUserRequest(username,password));
        model.addAttribute("success",user != null);
        model.addAttribute("user",user);
        return model;
    }

    @GetMapping(value = "/register", produces = "application/json")
    public Model addUser(@RequestParam("username") String username,@RequestParam("password") String password,Model model) {
        if(userService.checkUserExist(username)){
            model.addAttribute("success",false);
            model.addAttribute("message"," 用户名已经被注册！");
            return model;
        }
        model.addAttribute("success", userService.registerUser(new RegisterUserRequest(username,password)));
        return model;
    }
}
