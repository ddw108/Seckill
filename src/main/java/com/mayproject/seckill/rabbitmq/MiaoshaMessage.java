package com.mayproject.seckill.rabbitmq;

import com.mayproject.seckill.domain.MiaoshaUser;

public class MiaoshaMessage {

    private MiaoshaUser miaoshaUser;

    private long goodsId;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
