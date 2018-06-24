package com.mayproject.seckill.service;

import com.mayproject.seckill.dao.OrderDao;
import com.mayproject.seckill.domain.MiaoshaOrder;
import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.domain.OrderInfo;
import com.mayproject.seckill.redis.OrderKey;
import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        //return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
    }

    public OrderInfo getOrderById(long orderId){
        return orderDao.getOrderById(orderId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goodsVo){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(miaoshaUser.getId());

        orderDao.insert(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(miaoshaUser.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+miaoshaUser.getId()+"_"+goodsVo.getId(), miaoshaOrder);
        return orderInfo;
    }


}
