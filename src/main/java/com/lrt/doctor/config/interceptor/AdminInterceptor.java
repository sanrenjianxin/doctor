package com.lrt.doctor.config.interceptor;

import com.lrt.doctor.common.Constants;
import com.lrt.doctor.exception.ServiceException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求域中获取role
        String role = (String) request.getAttribute("role");
        // 如果不是管理员角色
        if (!role.equals("admin")) {
            throw new ServiceException(Constants.CODE_401, "您没有管理员权限");
        }
        return true;
    }
}
