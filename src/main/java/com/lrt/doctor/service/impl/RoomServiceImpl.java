package com.lrt.doctor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrt.doctor.entity.Room;
import com.lrt.doctor.mapper.RoomMapper;
import com.lrt.doctor.service.RoomService;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
}
