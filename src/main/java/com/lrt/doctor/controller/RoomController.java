package com.lrt.doctor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrt.doctor.common.R;
import com.lrt.doctor.dto.RoomDTO;
import com.lrt.doctor.entity.Depar;
import com.lrt.doctor.entity.Room;
import com.lrt.doctor.entity.UserRoom;
import com.lrt.doctor.service.DeparService;
import com.lrt.doctor.service.RoomService;
import com.lrt.doctor.service.UserRoomService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    DeparService deparService;

    @Autowired
    UserRoomService userRoomService;

    /**
     * 新增诊室
     * @param roomDTO
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody RoomDTO roomDTO) {
        Room room = new Room();
        BeanUtils.copyProperties(roomDTO, room);
        LambdaQueryWrapper<Room> roomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLambdaQueryWrapper.eq(Room::getName, room.getName());
        if (roomService.getOne(roomLambdaQueryWrapper) != null) {
            return R.error("诊室名称已存在");
        }
        // 根据科室名查找科室id
        LambdaQueryWrapper<Depar> deparLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deparLambdaQueryWrapper.eq(Depar::getName, roomDTO.getDeparName());
        Depar depar = deparService.getOne(deparLambdaQueryWrapper);
        // 如果科室存在则绑定
        if (depar != null) {
            room.setDeparId(depar.getId());
        }
        roomService.save(room);
        return R.success("新增诊室成功");
    }

    /**
     * 分页查询所有诊室信息，以诊室名做过滤条件,返回roomDTO
     * @param page
     * @param pageSize
     * @param roomName
     * @return
     */
    @GetMapping
    public R<Page> page(int page, int pageSize, String roomName) {
        // 构造Room分页构造器
        Page<Room> roomPage = new Page<>(page, pageSize);
        // 构造RoomDTO分页构造器
        Page<RoomDTO> roomDTOPage = new Page<>();
        // 从数据库中查询出Room数据
        LambdaQueryWrapper<Room> roomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLambdaQueryWrapper.like(Room::getName, roomName);
        roomService.page(roomPage, roomLambdaQueryWrapper);
        // 将roomPage中数据拷贝到roomDTO中,忽略records属性
        BeanUtils.copyProperties(roomPage, roomDTOPage, "records");
        // 获取查询出的room数据
        List<Room> roomRecords = roomPage.getRecords();
        // 将room数据转换为roomDTO数据，多了一个deparName字段
        List<RoomDTO> roomDTORecords = new ArrayList<>();
        for (Room roomRecord : roomRecords) {
            RoomDTO roomDTO = new RoomDTO();
            BeanUtils.copyProperties(roomRecord, roomDTO);
            String deparName = deparService.getById(roomDTO.getDeparId()).getName();
            roomDTO.setDeparName(deparName);
            roomDTORecords.add(roomDTO);
        }
        roomDTOPage.setRecords(roomDTORecords);
        return R.success(roomDTOPage);
    }

    /**
     * 修改诊室信息
     * @param roomDTO
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody RoomDTO roomDTO) {
        Room room = new Room();
        BeanUtils.copyProperties(roomDTO, room);
        // 根据科室名查找科室id
        LambdaQueryWrapper<Depar> deparLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deparLambdaQueryWrapper.eq(Depar::getName, roomDTO.getDeparName());
        Depar depar = deparService.getOne(deparLambdaQueryWrapper);
        // 如果科室存在则绑定
        if (depar != null) {
            room.setDeparId(depar.getId());
        }
        roomService.updateById(room);
        return R.success("修改诊室信息成功");

    }

    /**
     * 根据id删除诊室
     * @param id
     * @return
     */
    @DeleteMapping("#{id}")
    public R<String> deleteById(@PathVariable Long id) {
        // 查看当前诊室是否有出诊记录
        LambdaQueryWrapper<UserRoom> userRoomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoomLambdaQueryWrapper.eq(UserRoom::getRoomId, id);
        if (userRoomService.getOne(userRoomLambdaQueryWrapper) != null) {
            return R.error("当前诊室还有出诊记录，无法删除");
        }
        // 删除诊室
        roomService.removeById(id);
        return R.success("删除诊室成功");
    }

}
