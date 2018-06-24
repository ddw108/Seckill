package com.mayproject.seckill.result;

public class CodeMsg {

    private int code;
    private String msg;

    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务器异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg REQUESE_ILLEGAL = new CodeMsg(500102, "请求非法");
    public static CodeMsg ACCESS_LIMIT = new CodeMsg(500103, "访问太频繁");

    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "用户不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");


    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500241, "订单不存在");

    public static CodeMsg MIAOSHA_OVER = new CodeMsg(500500, "商品秒杀完毕");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复秒杀");
    public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "秒杀失败");


    private CodeMsg(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
