package com.mayproject.seckill.controller;

import com.mayproject.seckill.domain.User;
import com.mayproject.seckill.rabbitmq.MQReceiver;
import com.mayproject.seckill.rabbitmq.MQSender;
import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.redis.UserKey;
import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.result.Result;
import com.mayproject.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @Autowired
    MQReceiver receiver;

    @RequestMapping("/")
    @ResponseBody
    String home(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        sender.send("hello ddw");
        return Result.success("hello spring boot");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> mqtopic(){
        sender.sendTopic("hello ddw");
        return Result.success("hello spring boot");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> mqfanout(){
        sender.sendFanout("hello ddw");
        return Result.success("hello spring boot");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> mqheader(){
        sender.sendHeader("hello ddw");
        return Result.success("hello spring boot");
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){

        return Result.success("hello spring boot");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "DengDingwen");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("wzz");
        redisService.set(UserKey.getById, ""+1, user);
        return Result.success(true);
    }
}
