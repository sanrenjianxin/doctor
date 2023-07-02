package com.lrt.doctor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrt.doctor.common.R;
import com.lrt.doctor.dto.RoomDTO;
import com.lrt.doctor.entity.Depar;
import com.lrt.doctor.entity.Room;
import com.lrt.doctor.entity.User;
import com.lrt.doctor.entity.UserRoom;
import com.lrt.doctor.service.DeparService;
import com.lrt.doctor.service.RoomService;
import com.lrt.doctor.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/depar")
public class DeparController {

    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    @Autowired
    DeparService deparService;

    /**
     * 新增科室
     * @param depar
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Depar depar) {
        // 查看科室名称是否已经存在
        LambdaQueryWrapper<Depar> deparLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deparLambdaQueryWrapper.eq(Depar::getName, depar.getName());
        if (deparService.getOne(deparLambdaQueryWrapper) != null) {
            return R.error("科室名称已存在");
        }
        deparService.save(depar);
        return R.success("新增科室成功");
    }

    /**
     * 分页查询所有科室信息，以科室名做过滤条件
     * @param page
     * @param pageSize
     * @param deparName
     * @return
     */
    @GetMapping
    public R<Page> page(int page, int pageSize, String deparName) {
        // 构造Depar分页构造器
        Page<Depar> deparPage = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Depar> deparLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deparLambdaQueryWrapper.like(Depar::getName, deparName);
        deparService.page(deparPage, deparLambdaQueryWrapper);
        return R.success(deparPage);
    }

    /**
     * 修改科室信息
     * @param depar
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Depar depar) {
        deparService.updateById(depar);
        return R.success("修改科室信息成功");
    }

    /**
     * 根据id删除科室
     * @param id
     * @return
     */
    @DeleteMapping("#{id}")
    public R<String> deleteById(@PathVariable Long id) {
        // 查看当前科室下是否有诊室
        LambdaQueryWrapper<Room> roomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLambdaQueryWrapper.eq(Room::getDeparId, id);
        if (roomService.getOne(roomLambdaQueryWrapper) != null) {
            return R.error("当前科室下还有诊室，无法删除");
        }
        // 查看当前科室下是否有医生
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getDeparId, id);
        if (userService.getOne(userLambdaQueryWrapper) != null) {
            return R.error("当前科室下还有医生，无法删除");
        }
        deparService.removeById(id);
        return R.success("删除科室成功");
    }

}
