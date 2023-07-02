package com.lrt.doctor.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrt.doctor.common.R;
import com.lrt.doctor.dto.UserDTO;
import com.lrt.doctor.entity.Depar;
import com.lrt.doctor.entity.User;
import com.lrt.doctor.entity.UserRoom;
import com.lrt.doctor.service.DeparService;
import com.lrt.doctor.service.UserRoomService;
import com.lrt.doctor.service.UserService;
import com.lrt.doctor.utils.JWTUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private DeparService deparService;

    @Autowired
    private UserRoomService userRoomService;

    /**
     * 登录
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public R<UserDTO> login(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        // 使用hutool工具类
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return R.error("用户名或密码为空");
        }
        else {
            // 将传入的密码进行md5加密
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            // 将传入的用户名查找数据库
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username);
            User user = userService.getOne(queryWrapper);
            // 如果用户名不存在，报错
            if (user == null) {
                return R.error("登录失败");
            }
            // 如果密码不符，报错
            if (!password.equals(user.getPassword())) {
                return R.error("登录失败");
            }
            // 浅拷贝User对象中的数据到userDTO中,忽略userDTO中没有的数据
            BeanUtil.copyProperties(user, userDTO, true);
            // 以用户id 和用户的角色 为载荷, password为密钥生成token
            String token = JWTUtils.getToken(user.getId().toString(), user.getAuth() == 0 ? "admin" : "doctor", user.getPassword());
            userDTO.setToken(token);

            return R.success(userDTO);
        }
    }

    /**
     * 验证医生是否登录接口
     * @return
     */
    @GetMapping("/loginConfirm")
    public R<String> loginConfirm() {
        return R.success("医生已登录");
    }

    /**
     * 验证管理员是否登录接口
     */
    @GetMapping("/AdminLoginConfirm")
    public R<String> adminLogin() {
        return R.success("管理员已登录");
    }

    /**
     * 根据id获取用户信息接口
     * @param id
     * @return
     */
    @GetMapping("#{id}")
    public R<UserDTO> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        UserDTO userDTO = new UserDTO();
        if (user == null) {
            return R.error("找不到用户");
        }
        // 浅拷贝user到userDTO
        BeanUtil.copyProperties(user, userDTO, true);
        // 获取医生所在科室的名字
        Depar depar = deparService.getById(user.getDeparId());
        userDTO.setDeparName(depar.getName());

        return R.success(userDTO);
    }

    /**
     * 医生照片上传接口
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        // 获取文件的信息
        String originalFilename = multipartFile.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        // 先存储到磁盘
        File uploadParentFile = new File(fileUploadPath);
        // 判断配置的文件目录是否存在，若不存在创建一个新的文件目录
        if (!uploadParentFile.exists()) {
            uploadParentFile.mkdir();
        }
        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUuid = uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath + fileUuid);
        // 把获取到的文件存储到磁盘目录
        multipartFile.transferTo(uploadFile);
        // 返回文件名以便前端回显
        return R.success(fileUuid);
    }

    /**
     * 修改个人信息
     * @param userDTO
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody UserDTO userDTO) {
        // 根据科室的名字查找科室id,绑定到userDTO
        String deparName = userDTO.getDeparName();
        LambdaQueryWrapper<Depar> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Depar::getName, deparName);
        Depar depar = deparService.getOne(queryWrapper);
        userDTO.setDeparId(depar.getId());
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        // 保存到数据库
        userService.updateById(user);
        return R.success("个人信息修改成功");
    }

    /**
     * 新增医生
     * @param userDTO
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody UserDTO userDTO) {
        // 判断传入的用户名和密码是否为空
        if (StrUtil.isBlank(userDTO.getUsername()) || StrUtil.isBlank(userDTO.getPassword())) {
            return R.error("用户名或密码为空");
        }
        // 判断用户名是否已经存在
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, userDTO.getUsername());
        if (userService.getOne(userLambdaQueryWrapper) != null) {
            return R.error("用户名已存在");
        }
        // 将密码进行md5加密
        userDTO.setPassword(DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes()));
        // 根据科室的名字查找科室id,绑定到userDTO
        LambdaQueryWrapper<Depar> deparLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deparLambdaQueryWrapper.eq(Depar::getName, userDTO.getDeparName());
        Depar depar = deparService.getOne(deparLambdaQueryWrapper);
        userDTO.setDeparId(depar.getId());
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        // 设置权限为医生
        user.setAuth(1);
        // 保存到数据库
        userService.save(user);
        return R.success("新增医生成功");
    }

    /**
     * 医生信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器
        Page page1 = new Page(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 按姓名过滤，降序排列
        queryWrapper.like(!StrUtil.isBlank(name), User::getName, name);
        queryWrapper.orderByDesc(User::getUpdateTime);
        // 执行查询
        userService.page(page1, queryWrapper);
        return R.success(page1);
    }

    /**
     * 根据id删除医生信息
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public R<String> delete(@PathVariable Long id) {
        // 根据id查找医生所有出诊记录
        LambdaQueryWrapper<UserRoom> userRoomLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoomLambdaQueryWrapper.eq(UserRoom::getUserId, id);
        // 如果还有出诊记录则报告
        if (userRoomService.getOne(userRoomLambdaQueryWrapper) != null) {
            return R.error("当前医生还有出诊，无法删除");
        }
        // 根据id查找用户信息，如果是管理员用户则不能删除
        User user = userService.getById(id);
        if (user.getAuth() == 0) {
            return R.error("无法删除管理员用户");
        }
        // 删除医生
        userService.removeById(id);
        return R.success("删除医生成功");
    }

}
