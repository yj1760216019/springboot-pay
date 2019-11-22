package com.calinx.pay.wxpay;


import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Configuration
public class WeiXPayConfig implements WXPayConfig {
    private byte[] certData;
    //@Value("${wxpay.appID}")
    private String appID = "********";
   //@Value("${wxpay.mchID}")
    private  String mchID = "********";
    //@Value("${wxpay.key}")
    private String key = "********";
    //@Value("${wxpay.httpConnectTimeoutMs}")
    private int httpConnectTimeoutMs = 30000;
    //@Value("${wxpay.httpReadTimeoutMs}")
    private int httpReadTimeoutMs = 30000;
    //@Value("${wxpay.certPath}")
    private String certPath = "D:\\certs\\wx\\apiclient_cert.p12";
    //@Value("${wxpay.notifyUrl}")
    private String notifyUrl = "********";
    //@Value("${wxpay.secret}")
    private String secret = "********";
    //退款回调
    private String refundUrl;


    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    public void setHttpConnectTimeoutMs(int httpConnectTimeoutMs) {
        this.httpConnectTimeoutMs = httpConnectTimeoutMs;
    }

    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
        File file = new File(certPath);
        InputStream certStream = null;
        try {
            certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
            certStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public byte[] getCertData() {
        return certData;
    }

    public void setCertData(byte[] certData) {
        this.certData = certData;
    }

    public String getRefundUrl() {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl) {
        this.refundUrl = refundUrl;
    }
}
