package com.mayproject.seckill.access;

import com.alibaba.fastjson.JSON;
import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.redis.MiaoshaKey;
import com.mayproject.seckill.redis.MiaoshaUserKey;
import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.result.Result;
import com.mayproject.seckill.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MiaoshaUser user = getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }
            int seconds = accessLimit.seconds();
            boolean needLogin =  accessLimit.needLogin();
            int maxCount = accessLimit.maxCount();

            String key = request.getRequestURI();
            if(needLogin){
                if(user == null){
                    render(response, CodeMsg.SERVER_ERROR);
                    return false;
                }
                key += "_"+user.getId();
            }else{

            }
            MiaoshaUserKey accessKey = MiaoshaUserKey.withExpire(seconds);
            //查询访问次数
            Integer count = redisService.get(accessKey, key, Integer.class);
            if(count == null){
                redisService.set(accessKey, key, 1);
            }else if(count < maxCount){
                redisService.incr(accessKey, key);
            }else{
                render(response, CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(str.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){
        //通过参数获得token
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        //通过cookie获得token
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        //获取到客户端请求的token
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        //从redis中获取miaoshaUser
        return miaoshaUserService.getByToken(response, token);
    }

    //通过cookie获得token
    private String getCookieValue(HttpServletRequest httpServletRequest, String cookiName){
        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookiName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
