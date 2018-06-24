package com.mayproject.seckill.controller;

import com.mayproject.seckill.access.AccessLimit;
import com.mayproject.seckill.domain.MiaoshaOrder;
import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.domain.OrderInfo;
import com.mayproject.seckill.rabbitmq.MQSender;
import com.mayproject.seckill.rabbitmq.MiaoshaMessage;
import com.mayproject.seckill.redis.GoodsKey;
import com.mayproject.seckill.redis.MiaoshaKey;
import com.mayproject.seckill.redis.MiaoshaUserKey;
import com.mayproject.seckill.redis.RedisService;
import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.result.Result;
import com.mayproject.seckill.service.GoodsService;
import com.mayproject.seckill.service.MiaoshaService;
import com.mayproject.seckill.service.OrderService;
import com.mayproject.seckill.util.MD5Util;
import com.mayproject.seckill.util.UUIDUtil;
import com.mayproject.seckill.vo.GoodsVo;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 在系统初始化的时候会回调这个函数
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if(goodsVos == null){
            return;
        }
        for(GoodsVo goodsVo : goodsVos){
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    //GET是从服务端获取数据，无论调用多少次，都不会对服务端数据产生影响
    //POST是往服务端提交数据
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser miaoshaUser,
                            @RequestParam("goodsId")long goodsId, @PathVariable("path") String path){
        model.addAttribute("user",miaoshaUser);
        if(miaoshaUser == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //验证path是否正确
        boolean check = miaoshaService.checkPath(path, miaoshaUser, goodsId);
        if(!check){
            return Result.error(CodeMsg.REQUESE_ILLEGAL);
        }
        //内存标记goodsId，来减少redis的访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断库存(预减库存)
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀到
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if(miaoshaOrder != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setMiaoshaUser(miaoshaUser);
        mqSender.sendMiaoshaMessage(message);
        return Result.success(0);//排队中

        /*老版本的增加了redis缓存的秒杀界面
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已经秒杀到
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(), goodsId);
        if(miaoshaOrder != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存
        //下订单
        //写入秒杀订单（必须是原子操作）
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goodsVo);
        return Result.success(orderInfo);
        */
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        long result  = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser miaoshaUser,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam("verifyCode")int verifyCode) {
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //验证码进行验证
        boolean check = miaoshaService.checkVerifyCode(miaoshaUser, goodsId, verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUESE_ILLEGAL);
        }
        String str = miaoshaService.createMiaoshaPath(miaoshaUser, goodsId);
        return Result.success(str);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser miaoshaUser,
                                               @RequestParam("goodsId")long goodsId) {
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        BufferedImage image = miaoshaService.createVerifyCode(miaoshaUser, goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG",out);
            out.flush();;
            out.close();
            return null;
        }catch (Exception e){
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
