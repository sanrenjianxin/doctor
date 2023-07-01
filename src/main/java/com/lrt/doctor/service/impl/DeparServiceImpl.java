package com.lrt.doctor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrt.doctor.entity.Depar;
import com.lrt.doctor.mapper.DeparMapper;
import com.lrt.doctor.service.DeparService;
import org.springframework.stereotype.Service;

@Service
public class DeparServiceImpl extends ServiceImpl<DeparMapper, Depar> implements DeparService {
}
