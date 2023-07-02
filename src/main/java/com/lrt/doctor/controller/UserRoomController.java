package com.lrt.doctor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrt.doctor.common.R;
import com.lrt.doctor.entity.Room;
import com.lrt.doctor.entity.User;
import com.lrt.doctor.entity.UserRoom;
import com.lrt.doctor.service.RoomService;
import com.lrt.doctor.service.UserRoomService;
import com.lrt.doctor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/call")
public class UserRoomController {

    @Autowired
    UserService userService;

    @Autowired
    UserRoomService userRoomService;

    @Autowired
    RoomService roomService;


    /**
     * 根据医生id分页查询对应出诊记录（按开始时间升序排列）,按照日期,诊室名查找
     * @param id
     * @return
     */
    @GetMapping("/page")
    public R<Page> docPage(Long id, int page, int pageSize, LocalDate date, String roomName) {
        Page<UserRoom> userRoomPage = new Page<>(page, pageSize);
        // 根据诊室名模糊查询诊室号
        List<Long> roomIds = getRoomIdsLikeRoomName(roomName);
//        select * from user_room where user_id = '1675163308572811266' and Date(date_begin) = '2023-07-02' and room_id in (1)
        QueryWrapper<UserRoom> userRoomQueryWrapper = new QueryWrapper<>();
        userRoomQueryWrapper.eq("user_id", id)
                .eq("Date(date_begin)", date)
                .in("room_id", roomIds)
                .orderByAsc("date_begin");
        userRoomService.page(userRoomPage, userRoomQueryWrapper);
        return R.success(userRoomPage);
    }


    /**
     * 管理员分页查询所有出诊信息，按日期，医生名，诊室名过滤
     * @param page
     * @param pageSize
     * @param date
     * @param docName
     * @param roomName
     * @return
     */
    @GetMapping("/pageAll")
    public R<Page> allPage(int page, int pageSize, LocalDate date, String docName, String roomName) {
        Page<UserRoom> userRoomPage = new Page<>(page, pageSize);
        // 根据医生名模糊查询医生id
        List<Long> userIds = getUserIdsLikeUserName(docName);
        // 根据诊室名模糊查询诊室号
        List<Long> roomIds = getRoomIdsLikeRoomName(roomName);
        // 查询
        QueryWrapper<UserRoom> userRoomQueryWrapper = new QueryWrapper<>();
        userRoomQueryWrapper.eq("Date(date_begin)", date)
                .in("user_id", userIds)
                .in("room_id", roomIds)
                .orderByAsc("date_begin");
        userRoomService.page(userRoomPage);
        return R.success(userRoomPage);
    }

    /**
     * 新增出诊记录(前端提交userRoom)
     * @param userRoom
     * @return
     */
    @PutMapping
    public R<String> save(@RequestBody UserRoom userRoom) {
        userRoomService.save(userRoom);
        return R.success("新增出诊记录成功");
    }

    /**
     * 修改出诊记录(前端提交userRoom)
     * @param userRoom
     * @return
     */
    @PostMapping
    public R<String> update(@RequestBody UserRoom userRoom) {
        userRoomService.updateById(userRoom);
        return R.success("修改出诊记录成功");
    }

    /**
     * 根据id删除出诊记录
     * @param userRoom
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestBody UserRoom userRoom) {
        userRoomService.removeById(userRoom.getId());
        return R.success("删除出诊记录成功");
    }

    /**
     * 根据诊室名模糊查询诊室号
     * @param roomName
     * @return
     */
    private List<Long> getRoomIdsLikeRoomName(String roomName) {
        LambdaQueryWrapper<Room> roomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomLambdaQueryWrapper.like( Room::getName, roomName);
        List<Room> roomList = roomService.list(roomLambdaQueryWrapper);
        List<Long> roomIds = roomList.stream().map(Room::getId).collect(Collectors.toList());
        return roomIds;
    }

    /**
     * 根据医生名模糊查询医生id
     * @param docName
     * @return
     */
    private List<Long> getUserIdsLikeUserName(String docName) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getName, docName)
                .eq(User::getAuth, 1);
        List<User> users = userService.list(userLambdaQueryWrapper);
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        return userIds;
    }

}
