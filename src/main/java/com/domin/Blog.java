package com.domin;

import java.io.Serializable;

/**
 * @ClassName Blog
 * @Description: TODO
 * @Author lxc
 * @Date 2020/2/24
 * @Version V1.0
 **/
public class Blog implements Serializable {

    private String blogId;
    private String blogName;

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }
}
