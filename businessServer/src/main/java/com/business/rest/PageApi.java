package com.business.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 页面控制器
 * date: 2021/3/2 16:40
 *
 * @author hongjun
 */
@Controller
@RequestMapping("/page")
public class PageApi {

    @RequestMapping("/login")
    public String loginPage(){

        return "login";
    }

    @RequestMapping("/regist")
    public String registPage(){

        return "register";
    }

    @RequestMapping("/home")
    public String homePage(){

        return "home";
    }

    @RequestMapping("/detail")
    public String detailPage(){

        return "mdetail";
    }
}
