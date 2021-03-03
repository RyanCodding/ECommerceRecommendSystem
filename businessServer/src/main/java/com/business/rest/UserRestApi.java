package com.business.rest;

import com.business.enums.HttpStatusEnum;
import com.business.model.domain.User;
import com.business.model.request.LoginUserRequest;
import com.business.model.request.RegisterUserRequest;
import com.business.service.UserService;
import com.business.utils.Result;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/rest/users")
@RestController
public class UserRestApi {

    @Resource
    private UserService userService;

    @PostMapping(value = "/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user  =userService.loginUser(new LoginUserRequest(username,password));
        return Result.success(user);
    }

    @PostMapping(value = "/register")
    public Result addUser(@RequestParam("username") String username,@RequestParam("password") String password) {
        if(userService.checkUserExist(username)){

            return Result.error(HttpStatusEnum.USER_TAKEN.getCode(),HttpStatusEnum.USER_TAKEN.getMsg());
        }
        return Result.success(userService.registerUser(new RegisterUserRequest(username,password)));
    }
}
