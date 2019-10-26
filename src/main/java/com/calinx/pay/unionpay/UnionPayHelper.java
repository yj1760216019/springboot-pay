package com.calinx.pay.unionpay;

import com.calinx.pay.unionpay.sdk.AcpService;
import com.calinx.pay.unionpay.sdk.SDKConfig;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class UnionPayHelper {


    private static final Logger logger = LoggerFactory.getLogger(UnionPayHelper.class);

    /**
     *
     *
     * @param txnAmt   交易金额，单位为分
     * @param orderId   订单号
     */


    /**
     * 加载银联相关配置
     */
    static{
        SDKConfig.getConfig().loadPropertiesFromSrc();
    }


    /**
     * 银联支付
     * @param body  订单描述
     * @param totalMoney  支付金额
     * @param orderId  订单号
     * @return
     */
    public Object payOrder(String body,BigDecimal totalMoney,String orderId,String additionalParam) throws UnsupportedEncodingException {
        //转换金额为分
        BigDecimal totalFree = totalMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        Map<String, String> contentData = new HashMap<String, String>();
        //银联全渠道系统参数
        contentData.put("version", UnionPayConfig.version);        //版本号，全渠道默认值
        contentData.put("encoding", UnionPayConfig.encoding);      //字符集编码格式
        contentData.put("signMethod", SDKConfig.getConfig().getSignMethod());       //签名方式 只支持01：RSA方式证书加密
        contentData.put("txnType", "01");       //交易类型 01代表消费
        contentData.put("txnSubType", "01");    //消费子类型 01：自主消费
        contentData.put("bizType", "000201");       //业务类型 b2c网关支付 手机wap支付
        contentData.put("channelType", "08");       //渠道类型  07-pc  08-手机
        //商户接入相关参数
        contentData.put("merId",UnionPayConfig.merId);      //商户号
        contentData.put("accessType", "0");     //接入类型（0：商户 1：收单机构 2：平台商户）
        contentData.put("orderId", orderId);        //订单号 8-40位字母或数字 不能含有特殊字符
        contentData.put("txnTime", UnionPayConfig.getCurrentTime());       //订单发送时间 格式：yyyyMMddHHmmss
        contentData.put("txnAmt", totalFree.toString());      //交易金额 单位为分 不能有小数
        contentData.put("currencyCode", "156");     //交易币种  国内固定 人民币 156
        contentData.put("orderDesc",body);      //订单商品描述
        contentData.put("accType", "01");       //账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
        contentData.put("backUrl", UnionPayConfig.backUrl);   //后台通知地址 后台通知地址如果带有？拼接 验签时需要去掉
        contentData.put("reqReserved", Base64.toBase64String(additionalParam.getBytes(UnionPayConfig.encoding)));     //额外参数 回调原样返回
        //对请求参数进行签名
        Map<String, String> requestData = AcpService.sign(contentData, UnionPayConfig.encoding);
        //发送post请求  接收同步应答报文
        Map<String, String> responseData = AcpService.post(requestData,UnionPayConfig.requestAppUrl, UnionPayConfig.encoding);
        String tn = null;
        if(!responseData.isEmpty()){
            if(AcpService.validate(responseData, UnionPayConfig.encoding)){
                //验证签名成功
                String responseCode = responseData.get("respCode");
                if("00".equals(responseCode)){
                    tn = responseData.get("tn");
                }
            }
        }
        //获取到tn返回给客户端调取银联支付
        return tn;
    }




    /**
     * 银联支付回调
     * @param params
     * @return
     */
    public Map<String,String> payCallBack(Map<String,String> params){
       try {
           //验签
           if(AcpService.validate(params,UnionPayConfig.encoding)){
               //验签成功
                if("00".equals(params.get("respCode")) || "A6".equals(params.get("respCode"))){
                    Map result = new HashMap();
                    result.put("param",new String(Base64.decode(params.get("reqReserved")),UnionPayConfig.encoding));
                    //消费交易流水号
                    result.put("serialNumber",params.get("queryId"));
                    result.put("orderId",params.get("orderId"));
                    return result;
                }else{
                    throw new RuntimeException("银联支付回调结果状态异常");
                }
           }else{
               throw new RuntimeException("银联支付验签失败");
           }
       }catch (Exception e){
          throw new RuntimeException("银联支付验证失败"+e.getMessage());
       }
    }


    /**
     * 银联退款
     * @param orderId
     * @param refundMoney
     * @param serialNumber
     * @param additionalParam
     * @return
     * @throws UnsupportedEncodingException
     */
    public Boolean refundTranslation(String orderId,BigDecimal refundMoney,String serialNumber,String additionalParam) throws UnsupportedEncodingException {
        //元转换为分
        BigDecimal refunDFree = refundMoney.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        Map<String,String> contentData = new HashMap<String,String>();
        //银联全渠道参数
        contentData.put("version", UnionPayConfig.version);               //版本号
        contentData.put("encoding", UnionPayConfig.encoding);             //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
        contentData.put("txnType", "04");                           //交易类型 04-退货
        contentData.put("txnSubType", "00");                        //交易子类型  默认00
        contentData.put("bizType", "000201");                       //业务类型
        contentData.put("channelType", "08");                       //渠道类型，07-PC，08-手机

        /***商户接入参数***/
        contentData.put("merId", UnionPayConfig.merId);                //商户号码 改成自己申请的商户号
        contentData.put("accessType", "0");                         //接入类型，商户接入固定填0，不需修改
        contentData.put("orderId", orderId);          //商户订单号，8-40位数字字母
        contentData.put("txnTime", UnionPayConfig.getCurrentTime());      //订单发送时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
        contentData.put("currencyCode", "156");                     //交易币种（境内商户一般是156 人民币）
        contentData.put("txnAmt", refunDFree.toString());           //退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
        contentData.put("backUrl", UnionPayConfig.refundBackUrl);               //后台通知地址，后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 退货交易 商户通知,其他说明同消费交易的后台通知
        contentData.put("reqReserved",Base64.toBase64String(additionalParam.getBytes(UnionPayConfig.encoding)));
        //要调通交易以下字段必须修改
        contentData.put("origQryId", serialNumber);      //原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取
        //验签
        Map<String, String> requestData  = AcpService.sign(contentData,UnionPayConfig.encoding);		//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        //发送post请求    获取同步响应报文
        Map<String, String> responseData = AcpService.post(requestData, UnionPayConfig.backRequestUrl,UnionPayConfig.encoding);//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        //处理响应结果
        if(!responseData.isEmpty()){
            if(AcpService.validate(responseData, UnionPayConfig.encoding)){
                String respCode = responseData.get("respCode") ;
                if(("00").equals(respCode)){
                    return true;
                }else{
                    //后续需发起交易状态查询交易确定交易状态
                    throw new RuntimeException("状态异常 请发起交易状态查询确定交易状态");
                }
            }else{
               throw new RuntimeException("验证签名失败");
            }
        }else{
            throw new RuntimeException("未获取到返回报文或返回http状态码非200");
        }
    }


    /**
     * 退款回调
     * @param params
     * @return
     */
    public Map<String,String>  refundCallBack(Map<String,String> params){
        try {
            //验签
            if(AcpService.validate(params,UnionPayConfig.encoding)){
                //验签成功
                if("00".equals(params.get("respCode"))){
                    Map<String,String> result = new HashMap<String,String>();
                    //订单号
                    String orderId = params.get("orderId");
                    result.put("orderId",orderId);
                    return result;
                }else{
                    throw new RuntimeException("银联退款回调结果状态异常");
                }
            }else{
                throw new RuntimeException("银联退款验签失败");
            }
        }catch (Exception e){
            throw new RuntimeException("银联退款验证失败"+e.getMessage());
        }
    }








}
