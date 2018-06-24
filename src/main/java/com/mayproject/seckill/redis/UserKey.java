package com.mayproject.seckill.redis;

public class UserKey extends BasePresix {

    private UserKey(String Prefix) {
        super(Prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
