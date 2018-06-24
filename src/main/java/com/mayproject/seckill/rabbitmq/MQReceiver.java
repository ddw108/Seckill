package com.mayproject.seckill.rabbitmq;

import com.mayproject.seckill.domain.MiaoshaOrder;
import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.result.Result;
import com.mayproject.seckill.service.GoodsService;
import com.mayproject.seckill.service.MiaoshaService;
import com.mayproject.seckill.service.OrderService;
import com.mayproject.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:" + message  );
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        log.info("topic1 message:" + message  );
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){
        log.info("topic2 message:" + message  );
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiveTopic2(byte[] message){
        log.info("header message:" +new String(message)  );
    }

    @RabbitListener(queues = MQConfig.MIAOSHAQUEUE)
    public void miaoshaReceive(String message){
        log.info("receive message:" + message);
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message, MiaoshaMessage.class);
        long goodsId = miaoshaMessage.getGoodsId();
        MiaoshaUser miaoshaUser = miaoshaMessage.getMiaoshaUser();

        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return;
        }
        //判断是否已经秒杀到
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if(miaoshaOrder != null){
            return;
        }
        //减库存
        //下订单
        //写入秒杀订单（必须是原子操作）
        miaoshaService.miaosha(miaoshaUser, goodsVo);
    }
}
