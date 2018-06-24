package com.mayproject.seckill.redis;

public class GoodsKey extends BasePresix{

    private GoodsKey(int expireSeconds, String Prefix) {
        super(expireSeconds, Prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");

    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");

    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0, "gs");
}
