package com.business.rest;

import com.business.model.domain.User;
import com.business.service.UserService;
import com.business.utils.RandomUtil;
import com.business.utils.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@RequestMapping("/rest/users")
@RestController
public class UserRestApi {

    @Resource
    private UserService userService;


    @PostMapping(value = "/register")
    public Result userRegist(@RequestBody User user) {
        if(userService.checkUserExist(user.getName())) {
            return Result.error(400,"用户名已注册");
        }
        user.setName(HtmlUtils.htmlEscape(user.getName()));

        //使用随机数做盐，采用md5加密，加密2次
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String  algorithmName = "md5";

        String encodePassword = new SimpleHash(algorithmName,user.getPassword(),salt,times).toString();

        //将盐与加密后的密码存入数据库
        user.setUserId(RandomUtil.generateRandomInt6());
        user.setSalt(salt);
        user.setPassword(encodePassword);

        userService.registerUser(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody User userRequest, HttpSession session){
        String name = HtmlUtils.htmlEscape(userRequest.getName());

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name,userRequest.getPassword());
        try{
            subject.login(token);
            User user = userService.findByUsername(userRequest.getName());
            session.setAttribute("user",user);
            return Result.success();
        }catch (AuthenticationException e){
            String message = "账号密码错误，请重新输入！";
            return Result.error(400,message);
        }

    }


    @GetMapping("isLogin")
    public Result isLogin( HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated())
            return Result.success();
        else
            return Result.error(400,"未登录");
    }

}
