package com.mayproject.seckill.access;

import com.mayproject.seckill.domain.MiaoshaUser;

public class UserContext {

    //数据是每个线程单独存一份
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }

    public static MiaoshaUser getUser(){
        return userHolder.get();
    }
}
