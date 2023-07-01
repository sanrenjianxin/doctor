package com.lrt.doctor.config;


import com.lrt.doctor.common.JacksonObjectMapper;
import com.lrt.doctor.config.interceptor.AdminInterceptor;
import com.lrt.doctor.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor())
//                .addPathPatterns("/**")  // 拦截所有请求, 通过判断 token 是否合法来决定是否登录
//                .excludePathPatterns("/user/login", "/file/**" , "/people/export", "/people/import")// 放行 login 请求
//                .order(1); // 首先执行
//        registry.addInterceptor(adminInterceptor())
//                .addPathPatterns("/people/**","/user/AdminLoginConfirm") // 拦截所有管理的请求
//                .excludePathPatterns("/people/findAll") // 放行获取评分列表请求
//                .order(2); // 第二位执行
//    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }

    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用Jackson将Java对象转为Json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);
    }

}
