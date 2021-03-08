package com.business.utils;

/**
 * 随机数工具
 */
public class RandomUtil {

    private RandomUtil(){}

    /**
     * 生成六位随机整数
     * @return
     */
    public static int generateRandomInt6(){
        return (int)((Math.random()*9+1)*100000);
    }

}