package com.lrt.doctor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrt.doctor.entity.User;
import com.lrt.doctor.mapper.UserMapper;
import com.lrt.doctor.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
