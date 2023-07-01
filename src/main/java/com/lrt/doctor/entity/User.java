package com.lrt.doctor.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class User {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private Integer auth;// 0管理员1医生

    private String name;

    private String phone;

    private String idNumber; // 身份证号码

    private String sex;

    private Date birth;

    private String intro;

    private Long deparId;

    private Integer fees;

    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
