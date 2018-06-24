package com.mayproject.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mayproject.seckill.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     *Direct模式 交换机
     **/
    public void send(Object message){
        String msg = RedisService.beanToString(message);
        log.info("Send message:" + message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    /**
     *Topic模式 交换机
     **/
    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        log.info("Send message:" + message);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUNTING_KEY1, msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUNTING_KEY2, msg + "2");
    }

    /**
     *Fanout模式 交换机
     **/
    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        log.info("Send message:" + message);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "",msg + "1");
    }

    /**
     *Header模式 交换机
     **/
    public void sendHeader(Object message){
        String msg = RedisService.beanToString(message);
        log.info("Send message:" + message);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1", "value1");
        properties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(), properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "" ,obj);
    }

    public void sendMiaoshaMessage(MiaoshaMessage message){
        String msg = RedisService.beanToString(message);
        log.info("Send message:" + message);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHAQUEUE, msg);
    }
}
