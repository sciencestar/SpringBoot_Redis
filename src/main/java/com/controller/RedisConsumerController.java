package com.controller;

import com.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisConsumerController
 * @Description: TODO redis 消费者
 * @Author lxc
 * @Date 2020/6/21 12:42
 * @Version V1.0
 **/
@RestController
@RequestMapping("/consumer")
public class RedisConsumerController {

    @Autowired RedisUtil redisUtil;

    /** 公共配置 */
    private final static String MESSAGE = "testmq";

    /**
     * 接收消息API
     * @return
     */
    @RequestMapping("/receiveMessage")
    public String sendMessage() {
        return (String) redisUtil.brpop(MESSAGE, 0, TimeUnit.SECONDS);
    }
}
