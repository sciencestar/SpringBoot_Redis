package com.controller;

import com.annotation.DistributedLock;
import com.domin.Blog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.util.RedisUtil;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName BlogController
 * @Description: TODO
 * @Author lxc
 * @Date 2020/2/24
 * @Version V1.0
 **/
@RestController
@RequestMapping("/blogs")
public class BlogController {
    private static final Log log = LogFactory.getLog(BlogController.class);

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/{id}")
    public ModelAndView blog(@PathVariable("id") Long blogId) {
        ModelAndView view = new ModelAndView("blog-detail");
        //查看博客信息，先根据id从Redis中找
        Blog blog = (Blog) redisUtil.get("blog_" + blogId);
        return view;
    }

    @DistributedLock(prefix = "分布式锁")
    @RequestMapping(value = "setRedis/{key}", consumes = "application/json")
    public boolean setRedis(@PathVariable("key") String key, @RequestBody Blog blog) {
        redisUtil.begin();
        Boolean flag = redisUtil.set(key, blog, 30000);
        redisUtil.exec();
        return flag;
    }

    @DistributedLock(prefix = "分布式锁")
    @RequestMapping("/getRedis/{id}")
    public Object redisGet(@PathVariable("id") Long blogId) {
        Blog blog = null;
        if (redisUtil.hasKey(String.valueOf("blog"+blogId))) {
            blog = (Blog) redisUtil.get("blog" + blogId);
        }else {
            log.info("该数据不存在!");
        }
        return blog;
    }
}
