package com.mayproject.seckill.exception;

import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.result.Result;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GrobalExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandle(HttpServletRequest request, Exception e){
        e.printStackTrace();
        if(e instanceof GrobalException){
            GrobalException ex = (GrobalException)e;
            return Result.error(ex.getCm());
        }else if(e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}