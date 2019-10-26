package com.calinx.pay.wxpay;

import com.calinx.pay.utils.HttpUtil;
import com.calinx.pay.utils.PaymentUtil;
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
     * 微信支付
     * @param body  商品描述
     * @param orderId   订单id
     * @param totalMoney    订单金额
     * @param additionalParam   额外参数
     * @return
     * @throws Exception
     */
    public Map<String,String> payOrder(String body, String orderId, BigDecimal totalMoney, String additionalParam) throws Exception {
        Map<String,String> result = null;
        BigDecimal totalFree = totalMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        WXPay wxPay = new WXPay(weiXPayConfig);
        Map<String, String> data = new HashMap<String, String>();
        data.put("body",body);
        data.put("out_trade_no",orderId);
        data.put("total_fee",totalFree.toString());
        data.put("fee_type","CNY");
        data.put("attach",additionalParam);
        data.put("notify_url",weiXPayConfig.getNotifyUrl());
        data.put("trade_type","APP");
        Map<String, String> response = wxPay.unifiedOrder(data);
        if(response.get("result_code").equals("SUCCESS")){
            result=new LinkedHashMap<String, String>();
            result.put("appid",weiXPayConfig.getAppID());
            result.put("partnerid",weiXPayConfig.getMchID());
            result.put("prepayid",response.get("prepay_id"));
            result.put("package","Sign=WXPay");
            result.put("noncestr", WXPayUtil.generateNonceStr());
            result.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
            String sign= WXPayUtil.generateSignature(result, weiXPayConfig.getKey(), WXPayConstants.SignType.MD5);
            result.put("sign",sign );
        }
        return  result;
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
