package com.lrt.doctor.dto;

import com.lrt.doctor.entity.User;
import lombok.Data;

/*接收前端传入的登录参数*/
@Data
public class UserDTO extends User {

    private String token;

    private String deparName;

}
