package com.mayproject.seckill.redis;

public abstract class BasePresix implements KeyPrefix {

    private int expireSeconds;
    private String Prefix;

    public BasePresix(String Prefix) {
        this(0, Prefix);
    }

    public BasePresix(int expireSeconds, String Prefix){
        this.expireSeconds = expireSeconds;
        this.Prefix = Prefix;
    }
    public int expireSeconds() {
        return expireSeconds;
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + Prefix;
    }
}
