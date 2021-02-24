package com.business.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * Description:
 * date: 2021/2/23 19:09
 *
 * @author hongjun
 */
public class LoadApplicationUtil {

    public static String getYmlNew(String key){
        Resource resource = new ClassPathResource("application.yml");
        Properties properties = null;
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties =  yamlFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return String.valueOf(properties.get(key)) ;
    }

}
