package com.calinx.pay.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WXPayHelper {
    private static final Logger logger = LoggerFactory.getLogger(WXPayHelper.class);

    @Autowired
    private WeiXPayConfig weiXPayConfig;

    /**
     * 微信支付 APP
     * @param body  商品描述
     * @param orderId   订单id
     * @param totalMoney    订单金额
     * @param additionalParam   额外参数
     * @return
     * @throws Exception
     */
    public Map<String,String> payOrder(String body, String orderId, BigDecimal totalMoney, String additionalParam) throws Exception {
        BigDecimal totalFree = totalMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        WXPay wxPay = new WXPay(weiXPayConfig);
        Map<String, String> data = new HashMap<String, String>();
        //应用id
        data.put("appid",weiXPayConfig.getAppID());
        //商户号
        data.put("mch_id",weiXPayConfig.getMchID());
        //随机字符串
        data.put("nonce_str",WXPayUtil.generateNonceStr());
        //商品描述
        data.put("body",body);
        //商户订单号
        data.put("out_trade_no",orderId);
        //货币类型
        data.put("fee_type","CNY");
        //总金额
        data.put("total_fee",totalFree.toString());
        //终端ip
        data.put("spbill_create_ip","127.0.0.1");
        //通知回调地址
        data.put("notify_url","http://test.carlinx.cn/order/");
        //交易类型
        data.put("trade_type","APP");
        data.put("sign",WXPayUtil.generateSignature(data, weiXPayConfig.getKey(), WXPayConstants.SignType.MD5));
        Map<String, String> untifiedResponse = wxPay.unifiedOrder(data);
        if(WXPayConstants.SUCCESS.equals(untifiedResponse.get("return_code")) && WXPayConstants.SUCCESS.equals(untifiedResponse.get("result_code"))){
            //调用成功  将调用结果返还给客户端
            return untifiedResponse;
        }
        return null;
    }


    /**
     * 微信支付  NATIVE
     * @param body
     * @param orderId
     * @param totalMoney
     * @param additionalParam
     * @return
     */
    public String payOrderNative(String body,String orderId,BigDecimal totalMoney, String additionalParam) throws Exception {
        Map<String,String> result = null;
        BigDecimal totalFree = totalMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        WXPay wxPay = new WXPay(weiXPayConfig);
        Map<String, String> data = new HashMap<String, String>();
        //公众账号ID
        data.put("appid",weiXPayConfig.getAppID());
        //商户号
        data.put("mch_id",weiXPayConfig.getMchID());
        //随机字符串
        data.put("nonce_str",WXPayUtil.generateNonceStr());
        //签名类型
        data.put("sign_type","MD5");
        //商品描述
        data.put("body",body);
        //额外参数
        data.put("attach",additionalParam);
        //订单号
        data.put("out_trade_no",orderId);
        //币种
        data.put("fee_type","CNY");
        //支付金额
        data.put("total_fee",totalFree.toString());
        //终端ip
        data.put("spbill_create_ip","127.0.0.1");
        //回调地址
        data.put("notify_url","www.baidu.com");
        //支付类型
        data.put("trade_type","NATIVE ");
        //请求数据签名
        data.put("sign",WXPayUtil.generateSignature(data, weiXPayConfig.getKey(), WXPayConstants.SignType.MD5));
        Map<String, String> untifiedResponse = wxPay.unifiedOrder(data);
        if(WXPayConstants.SUCCESS.equals(untifiedResponse.get("return_code")) && WXPayConstants.SUCCESS.equals(untifiedResponse.get("result_code"))){
            //调用成功 返回code_url   用于生成二维码
            return untifiedResponse.get("code_url");
        }
        return null;
    }





    /**
     * 支付回调通知
     * @param params    回调参数
     * @return  订单id
     */
    public Map<String,String> payCallBack(Map<String,String> params){
        WXPay wxpay = new WXPay(weiXPayConfig);
        try {
            if (wxpay.isPayResultNotifySignatureValid(params)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("param",params.get("attach"));
                map.put("orderId",params.get("out_trade_no"));
                return map;
            }
            else {
                throw new RuntimeException("微信支付回调验签失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("微信回支付调验证失败");
        }
    }




    /**
     * 微信退款
     * @param orderId   订单id
     * @param totalMoney    订单金额
     * @param refundMoney   退款金额
     * @param refundReason  退款原因
     * @return
     * @throws Exception
     */
    public Boolean refundTranslation(String orderId,BigDecimal totalMoney,BigDecimal refundMoney,String refundReason) throws Exception {
        //订单金额转化为分
        BigDecimal totalFee = totalMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        //退款金额转化为分
        BigDecimal refundFee = refundMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        WXPay wxPay = new WXPay(weiXPayConfig);
        //准备请求数据
        Map<String,String> data = new HashMap<String,String>();
        data.put("out_trade_no","379353941172948992");
        //商户退款单号 商户系统内部退款号  同一退款单号多次请求只能退一笔
        data.put("out_refund_no",orderId);
        //订单总金额
        data.put("total_fee",totalFee.toString());
        //退款金额
        data.put("refund_fee",refundFee.toString());
        //退款币种
        data.put("refund_fee_type","CNY");
        //退款原因
        data.put("refund_desc",refundReason);
        //随机字符串
        data.put("nonce_str",WXPayUtil.generateNonceStr());
        //回调地址
        data.put("notify_url",weiXPayConfig.getRefundUrl());
        //请求数据签名
        data.put("sign",WXPayUtil.generateSignature(data, weiXPayConfig.getKey(), WXPayConstants.SignType.MD5));
        Map<String, String> refundResponse = wxPay.refund(data);
        //调用成功
        if(WXPayConstants.SUCCESS.equals(refundResponse.get("return_code"))){
            return true;
        }
        throw new RuntimeException("调用微信退款接口失败"+refundResponse.get("return_msg"));
    }


    /**
     * 微信退款回调
     * @param params    回调参数
     * @return
     */
    public Map<String,String> refundCallBack(Map<String,String> params){
        WXPay wxpay = new WXPay(weiXPayConfig);
        try {
            if (wxpay.isPayResultNotifySignatureValid(params)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("orderId",params.get("out_trade_no"));
                return map;
            }
            else {
                throw new RuntimeException("微信退款回调验签失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("微信退款回调验证失败");
        }
    }


}
