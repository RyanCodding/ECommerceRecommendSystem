package com.business.rest;

import com.business.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Description: 页面控制器
 * date: 2021/3/2 16:40
 *
 * @author hongjun
 */
@Controller
public class PageApi {

    @Resource
    private ProductService productService;

    /**
     * 登录页面
     **/
    @RequestMapping("/login")
    public String loginPage(){
        return "login";
    }

    /**
     * 注册页面
     **/
    @RequestMapping("/regist")
    public String registPage(){
        return "register";
    }

    /**
     * 注册成功页面
     **/
    @RequestMapping("/registSuccess")
    public String registSuccessPage(){
        return "registSuccess";
    }


    /**
     * 主页
     **/
    @RequestMapping("/home")
    public String homePage(){
        return "index";
    }

    /**
     * 商品详情页面
     **/
    @GetMapping("/detail")
    public String detailPage(){
        return "product";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "login";
    }
}
