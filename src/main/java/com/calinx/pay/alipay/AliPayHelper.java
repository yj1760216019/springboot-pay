package com.calinx.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayHelper {
    private static final Logger logger = LoggerFactory.getLogger(AliPayHelper.class);
    @Autowired
    private AlipayConfig alipayConfig;

    //支付宝支付
    public String payOrder(String body, String subject, String orderId, Integer timeoutExpressSecond, BigDecimal totalMoney, String additionalParam) throws AlipayApiException, UnsupportedEncodingException {
        //实例化客户端
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.alipayUrl,
                alipayConfig.appId,
                alipayConfig.privateKey,
                alipayConfig.format,
                alipayConfig.charset,
                alipayConfig.alipayPublicKey,
                alipayConfig.signType);

        AlipayTradeAppPayRequest payRequest = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel payModel = new AlipayTradeAppPayModel();
        payModel.setBody(body);
        payModel.setSubject(subject);
        payModel.setOutTradeNo(orderId);
        payModel.setTimeoutExpress(timeoutExpressSecond+"m");
        payModel.setTotalAmount(totalMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        payModel.setProductCode("QUICK_MSECURITY_PAY");
        payModel.setPassbackParams(URLEncoder.encode(additionalParam,"utf-8"));
        payRequest.setBizModel(payModel);
        payRequest.setNotifyUrl(alipayConfig.callBackUrl);
        AlipayTradeAppPayResponse payResponse = alipayClient.sdkExecute(payRequest);
        logger.info("支付宝支付同步结果"+payResponse.getBody());
        return payResponse.getBody();
    }

    //支付回调   返回订单id
    public Map<String,String> payCallBack(Map<String,String> params){
        //去掉网关请求参数
        params.remove("traceId");
        String tradeStatus=params.get("trade_status");
        Boolean success=false;
        if (tradeStatus.equals("TRADE_FINISHED") || tradeStatus.equals("TRADE_SUCCESS")) {
            try {
                success=  AlipaySignature.rsaCheckV1(params, alipayConfig.alipayPublicKey, alipayConfig.charset,alipayConfig.signType);
            } catch (AlipayApiException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        else
        {
            throw new RuntimeException("支付宝回调失败");
        }
        if(!success){
            throw new RuntimeException("支付宝回调失败  验证失败");
        }
        HashMap<String, String> map = new HashMap<>();
        //额外参数
        map.put("param",params.get("passback_params"));
        //订单id
        map.put("orderId",params.get("out_trade_no"));
        return map;
    }
}
