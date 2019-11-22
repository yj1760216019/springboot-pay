package com.calinx.pay;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alipay.api.AlipayApiException;
import com.calinx.pay.alipay.AliPayHelper;
import com.calinx.pay.utils.QRCodeUtil;
import com.calinx.pay.wxpay.WXPayHelper;
import org.apache.tomcat.jni.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PayApplicationTests {

    @Autowired
    private WXPayHelper wxPayHelper;
    @Autowired
    private AliPayHelper aliPayHelper;



    @Test
    public void contextLoads() {
        Random random = new Random();
        for(int i = 0;i<10;i++){

            int nextInt = random.nextInt(2);
            System.out.println(nextInt);
        }
        System.out.println("################################");
        for(int i = 0;i<10;i++){
            System.out.println(RandomUtil.randomInt(0,2));
        }
    }


    @Test
    public void test2() throws Exception {
        Map<String, String> payOrder = wxPayHelper.payOrder("可口可乐", "79663673213323", new BigDecimal(0.01), "aa");
        System.out.println(payOrder);
    }



    @Test
    public void testAlipay() throws AlipayApiException {
        String operator = aliPayHelper.payOrderNative("可口可乐", "可口可乐听装", "48748934", 15, new BigDecimal(0.01), "carlinx");
        System.out.println(operator);
    }


    @Test
    public void qrCode() throws IOException {
       QRCodeUtil.produceQrCodeTolocal("png","weixin：//wxpay/bizpayurl?sign=XXXXX&appid=XXXXX&mch_id=XXXXX&product_id=XXXXXX&time_stamp=XXXXXX&nonce_str=XXXXX","D://二维码//wx.png");
    }





}
