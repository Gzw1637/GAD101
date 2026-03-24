package org.gzw.backend.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.gzw.backend.common.Result;

@ControllerAdvice("org.gzw.backend.controller")
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.error();
    }
//    自定义
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result handleException(CustomException e){
        e.printStackTrace();
        return Result.error(e.getCode(),e.getMes());
    }
}
