package com.mayproject.seckill.service;

import com.mayproject.seckill.dao.MiaoshaUserDao;
import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.exception.GrobalException;
import com.mayproject.seckill.redis.MiaoshaUserKey;
import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.util.MD5Util;
import com.mayproject.seckill.util.MethodLog;
import com.mayproject.seckill.util.UUIDUtil;
import com.mayproject.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(Long id){
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
        if(user != null){
            return user;
        }
        //否则取数据库
        user = miaoshaUserDao.getById(id);
        if(user != null){
            redisService.set(MiaoshaUserKey.getById, ""+id, user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String passwordNew){
       MiaoshaUser user = getById(id);
       if(user == null){
           throw new GrobalException(CodeMsg.MOBILE_NOT_EXIST);
       }
       //更新数据库
       MiaoshaUser userToBeUpdate = new MiaoshaUser();
       userToBeUpdate.setId(id);
       userToBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew, user.getSalt()));
       miaoshaUserDao.update(userToBeUpdate);
       //处理缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(userToBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
       return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse httpServletResponse, String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        //从redis根据token取得MiaoshaUser这个对象
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长cookie的有效期
        if(user!=null){
            addCookie(user, token, httpServletResponse);
        }
        return user;
    }

    @MethodLog(methodName = "登录动作")
    public String login(HttpServletResponse httpServletResponse, LoginVo loginVo){
        if(loginVo == null){
            throw new GrobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if(miaoshaUser == null){
            throw new GrobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String slatDB = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, slatDB);
        if(!(dbPass.equals(calcPass))){
            throw new GrobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成唯一表示码token，标识用户
        String token = UUIDUtil.uuid();
        //给http响应加上cookie，输出到客户端
        addCookie(miaoshaUser, token, httpServletResponse);
        return token;
    }

    private void addCookie(MiaoshaUser miaoshaUser, String token, HttpServletResponse httpServletResponse){
        //生成cookie（此时已经有了服务端的http响应）
        //向redis缓存中添加token
        //前缀，key，value
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        //生成cookie
        //两个参数为key和value
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        //将cookie添加到http响应中，并反馈给客户端
        httpServletResponse.addCookie(cookie);
    }
}
