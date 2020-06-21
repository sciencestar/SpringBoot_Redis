package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName Application
 * @Description: TODO
 * @Author lxc
 * @Date 2020/2/24
 * @Version V1.0
 **/

@SpringBootApplication
public class SpringRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRedisApplication.class);
        System.out.println("应用启动.............");
    }
}
