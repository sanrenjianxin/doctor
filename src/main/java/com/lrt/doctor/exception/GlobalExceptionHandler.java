package com.lrt.doctor.exception;

import com.lrt.doctor.common.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     *
     * @param se 业务异常
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public R handle(ServiceException se){
        return R.error(se.getMessage());
    }

}
