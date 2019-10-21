package com.calinx.pay.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
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

    //微信支付
    public Map<String,String> payOrder(String body, String orderId, BigDecimal totalMoney, String additionalParam) throws Exception {
        WXPay wxPay = null;
        Map<String,String> result = null;
        BigDecimal totalFree = totalMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        wxPay = new WXPay(weiXPayConfig);
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


    //微信支付回调   返回订单id
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
                throw new RuntimeException("微信回调失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("微信回调失败");
        }
    }
}
