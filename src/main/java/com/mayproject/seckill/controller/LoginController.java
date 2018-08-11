package com.mayproject.seckill.controller;

import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.result.Result;
import com.mayproject.seckill.service.MiaoshaUserService;
import com.mayproject.seckill.util.MethodLog;
import com.mayproject.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse httpServletResponse, @Valid  LoginVo loginVo){
        log.info(loginVo.toString());
        //登录（拦截器会进行校验）
        String token = userService.login(httpServletResponse, loginVo);
        return Result.success(token);
    }

}
