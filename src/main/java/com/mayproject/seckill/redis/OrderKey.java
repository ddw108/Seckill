package com.mayproject.seckill.redis;

public class OrderKey extends BasePresix {
    public OrderKey(String Prefix) {
        super(Prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}
