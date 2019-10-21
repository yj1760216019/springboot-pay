package com.calinx.pay.alipay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class AlipayConfig {
    /**
     * 正式环境请求地址
     */
    @Value("${alipay.alipayUrl}")
    public  String alipayUrl;// = "https://openapi.alipay.com/gateway.do";
    /**
     * 支付宝分配给开发者的应用ID
     *
     */
    @Value("${alipay.appId}")
    public  String appId ;//= "APP_ID";

    /**
     * 支付接口名称
     */
    @Value("${alipay.payMethod}")
    public  String payMethod ;//= "alipay.trade.app.pay";
    /**
     * 提现接口名称
     */
    @Value("${alipay.transferMethod}")
    public  String transferMethod;// = "alipay.fund.trans.toaccount.transfer";
    /**
     * 仅支持JSON
     */
    @Value("${alipay.format}")
    public  String format;// = "JSON";
    /**
     * 请求使用的编码格式
     */
    @Value("${alipay.charset}")
    public  String charset; //= "utf-8";
    /**
     * 商户生成签名字符串所使用的签名算法类
     */
    @Value("${alipay.signTypeStr}")
    public  String signType ;//= "RSA2";
    /**
     * 支付宝公钥
     */
    @Value("${alipay.alipayPublicKey}")
    public  String alipayPublicKey ;//= "ALIPAY_PUBLIC_KEY";
    /**
     * 商户私钥
     */
    @Value("${alipay.privateKey}")
    public  String privateKey ;//= "PRIVATE_KEY";

    /**
     * 回调地址
     */
    @Value("${alipay.callBackUrl}")
    public  String callBackUrl ;//= "https://xxx.com/xxx/xx";

    public String getAlipayUrl() {
        return alipayUrl;
    }

    public void setAlipayUrl(String alipayUrl) {
        this.alipayUrl = alipayUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getTransferMethod() {
        return transferMethod;
    }

    public void setTransferMethod(String transferMethod) {
        this.transferMethod = transferMethod;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    @Override
    public String toString() {
        return "AlipayConfig{" +
                "alipayUrl='" + alipayUrl + '\'' +
                ", appId='" + appId + '\'' +
                ", payMethod='" + payMethod + '\'' +
                ", transferMethod='" + transferMethod + '\'' +
                ", format='" + format + '\'' +
                ", charset='" + charset + '\'' +
                ", signType='" + signType + '\'' +
                ", alipayPublicKey='" + alipayPublicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", callBackUrl='" + callBackUrl + '\'' +
                '}';
    }
}
