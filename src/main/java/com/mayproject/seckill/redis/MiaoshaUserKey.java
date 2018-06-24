package com.mayproject.seckill.redis;

public class MiaoshaUserKey extends BasePresix {

    public static final int TOKEN_EXPIRE = 3600*24*2;

    public MiaoshaUserKey(int expireSeconds, String Prefix) {
        super(expireSeconds, Prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");

    public static MiaoshaUserKey withExpire(int expire){
        return new MiaoshaUserKey(expire, "ac");
    }
}
