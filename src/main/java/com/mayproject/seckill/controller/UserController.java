package com.mayproject.seckill.controller;

import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser miaoshaUser){
        return Result.success(miaoshaUser);
    }
}
