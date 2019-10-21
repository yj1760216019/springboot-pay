package com.calinx.pay.Controller;


import com.calinx.pay.alipay.AliPayHelper;
import com.calinx.pay.utils.HttpUtil;
import com.calinx.pay.utils.XmlUtil;
import com.calinx.pay.wxpay.WXPayHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/order")
public class PayCallBackController {
    @Autowired
    private AliPayHelper aliPayHelper;
    @Autowired
    private WXPayHelper wxPayHelper;

    @PostMapping("/alipayCallBack")
    public void aliPayCallBack(HttpServletRequest request, HttpServletResponse response){
        HttpUtil.flushText(response,"success");
        aliPayHelper.payCallBack(HttpUtil.getRequestPostParams(request));
    }



    @PostMapping("/wxpayCallBack")
    public void wxpayCallBack(HttpServletRequest request ,HttpServletResponse response) throws Exception {
        HttpUtil.flushXML(response,"<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
        wxPayHelper.payCallBack(XmlUtil.xmlToMap(HttpUtil.getRequestPostStr(request)));

    }



}
