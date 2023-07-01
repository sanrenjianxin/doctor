package com.lrt.doctor.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrt.doctor.common.R;
import com.lrt.doctor.dto.UserDTO;
import com.lrt.doctor.entity.Depar;
import com.lrt.doctor.entity.User;
import com.lrt.doctor.service.DeparService;
import com.lrt.doctor.service.UserService;
import com.lrt.doctor.utils.JWTUtils;
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
     * 获取用户信息接口
     * @param id
     * @return
     */
    @GetMapping
    public R<UserDTO> getUserInfo(@RequestParam Long id) {
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



}
