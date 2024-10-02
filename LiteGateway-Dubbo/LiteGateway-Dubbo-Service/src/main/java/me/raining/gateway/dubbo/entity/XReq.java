package me.raining.gateway.dubbo.entity;

import java.io.Serializable;

/**
 * @author raining
 * @version 1.0.0
 * @description
 */
public class XReq implements Serializable {

    private static final long serialVersionUID = 3031463352108975608L;

    private String uid;
    private String name;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
