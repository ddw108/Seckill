package com.mayproject.seckill.controller;

import com.mayproject.seckill.domain.MiaoshaOrder;
import com.mayproject.seckill.domain.MiaoshaUser;
import com.mayproject.seckill.domain.OrderInfo;
import com.mayproject.seckill.result.CodeMsg;
import com.mayproject.seckill.result.Result;
import com.mayproject.seckill.service.GoodsService;
import com.mayproject.seckill.service.MiaoshaService;
import com.mayproject.seckill.service.OrderService;
import com.mayproject.seckill.vo.GoodsVo;
import com.mayproject.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderContrller {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> miaosha(Model model, MiaoshaUser miaoshaUser,
                                         @RequestParam("orderId") long orderId){
        if(miaoshaUser == null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrderInfo(orderInfo);
        return Result.success(vo);
    }
}
