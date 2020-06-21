package com.controller;

import com.domin.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.util.RedisUtil;

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

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/{id}")
    public ModelAndView blog(@PathVariable("id") Long blogId) {
        ModelAndView view = new ModelAndView("blog-detail");
        //查看博客信息，先根据id从Redis中找
        Blog blog = (Blog) redisUtil.get("blog_" + blogId);
        return view;
    }

    @RequestMapping(value = "setRedis/{key}",consumes = "application/json")
    public boolean setRedis(@PathVariable("key") String key,@RequestBody Blog blog){
        return redisUtil.set(key,blog,3000);
    }
    @RequestMapping("/getRedis/{id}")
    public Object redisGet(@PathVariable("id") Long blogId) {
        Blog blog = (Blog) redisUtil.get("blog"+blogId);
        return blog;
    }
}
