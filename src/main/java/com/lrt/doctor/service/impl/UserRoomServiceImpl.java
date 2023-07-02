package com.lrt.doctor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrt.doctor.entity.UserRoom;
import com.lrt.doctor.mapper.UserRoomMapper;
import com.lrt.doctor.service.UserRoomService;
import org.springframework.stereotype.Service;

@Service
public class UserRoomServiceImpl extends ServiceImpl<UserRoomMapper, UserRoom> implements UserRoomService {
}
