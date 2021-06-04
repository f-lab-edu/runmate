package com.runmate.configure;

import com.runmate.configure.jwt.JwtAuthenticationFilter;
import com.runmate.configure.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    JwtProvider jwtProvider;
//    @Bean
//    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistrationBean(){
//        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean
//                =new FilterRegistrationBean<>();
//
//        registrationBean.setFilter(new JwtAuthenticationFilter(jwtProvider));
//        registrationBean.addUrlPatterns("*");
//
//        return registrationBean;
//    }
}
