package com.calinx.pay.Controller;

import com.alipay.api.AlipayApiException;
import com.calinx.pay.alipay.AliPayHelper;
import com.calinx.pay.base.JsonResult;
import com.calinx.pay.unionpay.UnionPayHelper;
import com.calinx.pay.wxpay.WXPayHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/pay")
@Api("支付相关")
public class PayController {

    @Autowired
    private AliPayHelper aliPayHelper;
    @Autowired
    private WXPayHelper wxPayHelper;
    @Autowired
    private UnionPayHelper unionPayHelper;

    @PostMapping("/alipay")
    @ApiOperation("支付宝支付")
    public JsonResult aliPay(@ApiParam("订单描述")@RequestParam(value = "body")String body,
                             @ApiParam("订单id")@RequestParam(value = "orderId")String orderId,
                             @ApiParam("金额")@RequestParam(value = "totalMoney")BigDecimal totalMoney,
                             @ApiParam("额外参数") @RequestParam(value = "param")String param) throws UnsupportedEncodingException, AlipayApiException {
        return JsonResult.success(aliPayHelper.payOrder(body,body,orderId,2000,totalMoney,param));
    }



    @PostMapping("/wxpay")
    @ApiOperation("微信支付")
    public JsonResult wxpay(@ApiParam("订单描述")@RequestParam(value = "body")String body,
                            @ApiParam("订单id")@RequestParam(value = "orderId")String orderId,
                            @ApiParam("金额")@RequestParam(value = "totalMoney")BigDecimal totalMoney,
                            @ApiParam("额外参数") @RequestParam(value = "param")String param) throws Exception {
        return JsonResult.success(wxPayHelper.payOrder(body,orderId,totalMoney,param));
    }



    @PostMapping("/unionpay")
    @ApiOperation("银联支付")
    public JsonResult unionpay(@ApiParam("订单描述")@RequestParam(value = "body")String body,
                               @ApiParam("订单id")@RequestParam(value = "orderId")String orderId,
                               @ApiParam("金额")@RequestParam(value = "totalMoney")BigDecimal totalMoney,
                               @ApiParam("额外参数") @RequestParam(value = "param")String param) throws UnsupportedEncodingException {
        return JsonResult.success(unionPayHelper.payOrder(body,totalMoney,orderId,param));
    }

    @PostMapping("/unionpay/refund")
    @ApiOperation("银联退款")
    public JsonResult unionpayRefund(@ApiParam("流水号")@RequestParam(value = "queryId")String queryId,
                                     @ApiParam("订单id")@RequestParam(value = "orderId")String orderId,
                                     @ApiParam("金额")@RequestParam(value = "totalMoney")BigDecimal totalMoney,
                                     @ApiParam("额外参数") @RequestParam(value = "param")String param) throws UnsupportedEncodingException {
        return JsonResult.success(unionPayHelper.refundTranslation(orderId,totalMoney,queryId,param));
    }


    @PostMapping("/wxpay/refund")
    @ApiOperation("微信退款")
    public JsonResult wxpayRefund(@ApiParam("流水号")@RequestParam(value = "queryId")String queryId,
                                  @ApiParam("订单id")@RequestParam(value = "orderId")BigDecimal orderId,
                                  @ApiParam("金额")@RequestParam(value = "totalMoney")BigDecimal totalMoney,
                                  @ApiParam("额外参数") @RequestParam(value = "param")String param) throws Exception {
        return JsonResult.success(wxPayHelper.refundTranslation(queryId, orderId, totalMoney, param));
    }


    @PostMapping("/alipay/refund")
    @ApiOperation("支付宝退款")
    public JsonResult alipayRefund(@ApiParam("流水号")@RequestParam(value = "queryId")String queryId,
                                   @ApiParam("订单id")@RequestParam(value = "orderId")String orderId,
                                   @ApiParam("金额")@RequestParam(value = "totalMoney")BigDecimal totalMoney,
                                   @ApiParam("额外参数") @RequestParam(value = "param")String param) throws AlipayApiException {
        return JsonResult.success(aliPayHelper.refundTranslation(queryId, orderId, totalMoney, param));
    }







}
