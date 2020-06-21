package com.controller;

import com.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName RedisProducerController
 * @Description: TODO redis 服务提供者
 * @Author lxc
 * @Date 2020/6/21 12:38
 * @Version V1.0
 **/
@RestController
@RequestMapping("producer")
public class RedisProducerController {
    @Autowired RedisUtil redisUtil;

    /** 公共配置 */
    private final static String SUCCESS = "success";
    private final static String MESSAGE = "testmq";
    private static final List<String> list;

    static {
        list = Arrays.asList(new String[]{"猿医生", "CD", "yys"});
    }

    /**
     * 消息发送API
     * @return
     */
    @RequestMapping("/sendMessage")
    public String sendMessage() {
        for (String message : list) {
            redisUtil.lpush(MESSAGE, message);
        }
        return SUCCESS;
    }
}
