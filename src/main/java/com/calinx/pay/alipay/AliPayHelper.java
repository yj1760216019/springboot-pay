package com.calinx.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
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



    /**
     * 支付宝支付
     * @param body  商品描述
     * @param subject   商品描述
     * @param orderId   订单id
     * @param timeoutExpressSecond  超时时间
     * @param totalMoney    订单金额
     * @param additionalParam   额外参数
     * @return
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    public String payOrder(String body, String subject, String orderId, Integer timeoutExpressSecond, BigDecimal totalMoney, String additionalParam) {
        try {
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
            //订单描述信息
            payModel.setBody(body);
            //订单标题
            payModel.setSubject(subject);
            //商户订单号
            payModel.setOutTradeNo(orderId);
            //订单支付过期时间  过期后不能再支付
            payModel.setTimeoutExpress(timeoutExpressSecond+"m");
            //订单总金额   单位:元
            payModel.setTotalAmount(totalMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            //销售产品码   固定值
            payModel.setProductCode("QUICK_MSECURITY_PAY");
            //公用回传参数
            payModel.setPassbackParams(URLEncoder.encode(additionalParam,"utf-8"));
            payRequest.setBizModel(payModel);
            payRequest.setNotifyUrl(alipayConfig.callBackUrl);
            AlipayTradeAppPayResponse payResponse = alipayClient.sdkExecute(payRequest);
            logger.info("支付宝支付同步结果"+payResponse.getBody());
            if(payResponse.isSuccess()){
                return payResponse.getBody();
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException("创建支付环境失败:"+e.getMessage());
        }

    }


    /**
     * 二维码支付   该方式如果需要传额外参数  可通过body传递
     * @param body
     * @param subject
     * @param orderId
     * @param timeoutExpressSecond
     * @param totalMoney
     * @param storeId
     * @return
     * @throws AlipayApiException
     */
    public String payOrderNative(String body,String subject,String orderId,Integer timeoutExpressSecond,BigDecimal totalMoney,String storeId) {
        try {
            DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.alipayUrl,
                    alipayConfig.appId,
                    alipayConfig.privateKey,
                    alipayConfig.format,
                    alipayConfig.charset,
                    alipayConfig.alipayPublicKey,
                    alipayConfig.signType);
            AlipayTradePrecreateRequest  payRequest = new AlipayTradePrecreateRequest();
            AlipayTradePrecreateModel payModel = new AlipayTradePrecreateModel();
            //商户订单号
            payModel.setOutTradeNo(orderId);
            //订单金额
            payModel.setTotalAmount(totalMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            //订单标题
            payModel.setSubject(subject);
            //订单描述信息
            payModel.setBody(body);
            //商户门店编号
            payModel.setStoreId(storeId);
            //二维码支付过期时间
            payModel.setQrCodeTimeoutExpress(timeoutExpressSecond+"m");
            AlipayTradePrecreateResponse payResponse = alipayClient.execute(payRequest);
            if(payResponse.isSuccess()){
                return payResponse.getQrCode();
            }
            return null;
        }catch (Exception e){
            throw new RuntimeException("创建支付环境失败:"+e.getMessage());
        }
    }








    /**
     * 支付宝支付回调
     * @param params    回调参数
     * @return
     */
    public Map<String,String> payCallBack(Map<String,String> params){
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
            throw new RuntimeException("支付宝支付回调失败");
        }
        if(!success){
            throw new RuntimeException("支付宝支付回调失败  验证失败");
        }
        HashMap<String, String> map = new HashMap<>();
        //额外参数
        map.put("param",params.get("passback_params"));
        //订单id
        map.put("orderId",params.get("out_trade_no"));
        map.put("serialNumber","trade_no");
        return map;
    }


    /**
     * 支付宝退款
     * @param orderId
     * @param serialNumber
     * @param refundMoney
     * @return
     * @throws AlipayApiException
     */
    public Boolean refundTranslation(String orderId,String serialNumber,BigDecimal refundMoney,String refundReason) throws AlipayApiException {
        DefaultAlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.alipayUrl,
                alipayConfig.appId,
                alipayConfig.privateKey,
                alipayConfig.format,
                alipayConfig.charset,
                alipayConfig.alipayPublicKey,
                alipayConfig.signType
        );
        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo("HY20191018104206");
        refundModel.setTradeNo("2019101822001475480551755435");
        refundModel.setRefundReason(refundReason);
        refundModel.setRefundAmount(refundMoney.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        refundRequest.setBizModel(refundModel);
        AlipayTradeRefundResponse refundResponse = alipayClient.execute(refundRequest);
        if(refundResponse.isSuccess() || "10000".equals(refundResponse.getCode())){
            return true;
        }
        return false;
    }




    /**
     * 支付宝支付回调
     * @param params    回调参数
     * @return
     */
    public Map<String,String> refundCallBack(Map<String,String> params){
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
            throw new RuntimeException("支付宝退款回调失败");
        }
        if(!success){
            throw new RuntimeException("支付宝退款回调失败  验证失败");
        }
        HashMap<String, String> map = new HashMap<>();
        //额外参数
        map.put("param",params.get("passback_params"));
        //订单id
        map.put("orderId",params.get("out_trade_no"));
        return map;
    }







}
