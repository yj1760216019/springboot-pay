package com.calinx.pay.alipay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class AlipayConfig {
    //请求地址
    //@Value("${alipay.alipayUrl}")
    public  String alipayUrl = "https://openapi.alipay.com/gateway.do";                       //"https://openapi.alipay.com/gateway.do";

    //支付宝分配给开发者的应用ID
    //@Value("${alipay.appId}")
    public  String appId = "*********";

    /**
     * 仅支持JSON
     */
    //@Value("${alipay.format}")
    public  String format = "JSON";                          //"JSON";
    /**
     * 请求使用的编码格式
     */
    //@Value("${alipay.charset}")
    public  String charset = "utf-8";                        //"utf-8";
    /**
     * 商户生成签名字符串所使用的签名算法类
     */
    //@Value("${alipay.signTypeStr}")
    public  String signType = "RSA2";                     //"RSA2";
    /**
     * 支付宝公钥
     */
    //@Value("${alipay.alipayPublicKey}")
    public  String alipayPublicKey = "*******1nOS9ize3mWMFAxeMXdLMQ6keciurj1YTdPL+En9HA/phAqlwP2R/YhchiUUBauB112GelZsIoHmJfkIkukAY/cvatNhwmKH4C9tA1dahzIDNu33nroUEbTZu0PXL7TZOStsgtFiAte3dzrBPJajdAlaG+K6ct1Gj5NC24V8zuUoaiWNdBD6PN3e6F50+Xig4K3GzU9tHU79J71zAWLrzTFtrOm6R5shfx8dJF5K8EXHM5KCIq2DdAXtxH1/+S9PUh9KlQOl6aq5pFmcbzlQhTt1lNCRTeq+FCKxCSq2rjgVLA0b/mGqWhPmYpy0Tq5EBkQw5P9xwIDAQAB";
    /**
     * 商户私钥
     */
    //@Value("${alipay.privateKey}")
    public  String privateKey = "******ASCBKYwggSiAgEAAoIBAQCO9dtbfK2jL+OigOXdHhUbwZiUH5acCHHGEQjFPrde2iOQf7rgQueSJq84PA6xhiUHijoabAHdemEauROmAqCvrgSQuXQ0JcAstLSxOINi6X2J01mdrl/dxYUTPKdRV+JZ7d6+CqxR6v2ok8qzvVlV5cM3c5UXRYqh9mQNFrm7BTVJI6/e202UbYw1jJO6Y6xYoRW1V/OvRfbSxmURee/VNvkl/Wty5oecyT8jFfpIxTPKgY6yc/X9L/xv5/T70k/p0k82LxrRBDO50SuISEIlA01x6hhaCl+tBVdquBXTrLMjHv/TDUllsESLqo83vFqnoRfMCyCI/qz1KxTqa+yfAgMBAAECggEAfCi+qEgyLaYr1ziHanDz+DU9JHgCv+T9mv20VkpPy4s8lAS68oZoxB/g0cAnKrrMci30pHnlbMsahrDyhZ2GnM1PnbnTWG7o5mrS+4+PXFpY81ihXjbRf7RKDRkCX70qEtxqNTbjzgl+3+frhgsPCXERCJnThHCbJGrvHgb8q5VSh0IcVrb3CfdZq/9LoPFI8t8UuPSD8y9y3NeUCqUwEAgl0mLxb0bMRQBvlIKv7NR1vVfKSc8FMYFVhxdcAI6zsUMoNz/7eymSAX/uNzUHNQwYsInN7He1vOA7Cth2UCTBBOk3IoxdwZI/V/0MtXtqhHo3z1YL6GPenMRb+LWYQQKBgQD+Sicmzgt5+U+AbOF4jXHPrALupHlq3A4dd4H/4O+CvXJ+R2SppgHQQhS0+6jLpIcdYP5Zvf6bA4729xZariyg/io8fnsnMewed3zXXRiFYzK3LQW7eDyML9ZymosfkkO4TtDTRmtat8tkJZLvdSXGDCp7j/FRQPti4xM1AfnU7wKBgQCP7AMmGoVfb2gwDmV3KVMTZIBlzLShPWunQDdNkRLkYmIkl56dJKR5IAQei/wd+auKfVl4SYo8+R0nOIQDNT+e0aWjQPjH+HxQQU3hX5HwLvWF9SVH0EhZ/EwaK0Y08RON5YWBJvEbw7U2/xWPnGlC/hXmRNYDyRRuGCkpFKtDUQKBgBQdSXuIUOAVGHTQKAWhKp+Cu7ikyfXp8jPigCX9ZPSyh5v1icDhoMMYdwrZ3UA+g3GPeo8euEUKwwRGBz6lCGKzuCOQ3az8BfFjAj0G4Cms7xYOOaXanO952Ty3fMnCiBjMIziTGWrTnwMM9dJ7McG4tKORf0UuHrRzwdEvltOBAoGAReHljYTnKfXgcyUqY3fvXMU6CzQgqQI32Zg2wjUlJTwYR4hPfiIisqW3GlYCb1Oh1bcT0HJ06zJS045/m0cpwyYs9V5A7mhbhGSROlO7exJyKT9lgL9Dwl3dXf+RMXe1dPrznkiYXlDjFvc5MuhRvDO/0nWuLVxfzS1yHv5oQmECgYAKAyO49Rjkh8eQUyMe5T/4SGl8VO47i9t7Euf8SiBUUBNBjcgRfS7fbM6Si5fmKYqUIDQka/ZH9YekxwIdu5yXyC/bkPH7QnXj4FnDc+j4P1OLQo7i24dCtBIbGEoupQVRlHDZ9eWAh3ddQGdlp15KeJVku/H9Yj0JPHt6AhUYFw==";

    /**
     * 回调地址
     */
    //@Value("${alipay.callBackUrl}")
    public  String callBackUrl ;

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


}
