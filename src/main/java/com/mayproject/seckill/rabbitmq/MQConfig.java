package com.mayproject.seckill.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String MIAOSHAQUEUE = "miaosha_queue";

    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADER_QUEUE = "headers_queue";

    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADERS_EXCHANGE = "headersExchange";

    public static final String ROUNTING_KEY1 = "topic.key1";
    public static final String ROUNTING_KEY2 = "topic.#";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE , true);
    }

    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1 , true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2 , true);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUNTING_KEY1);
    }

    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUNTING_KEY2);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    @Bean
    public Queue headersQueue(){
        return new Queue(HEADER_QUEUE , true);
    }

    @Bean
    public Binding headersBinding2(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }

    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
}
