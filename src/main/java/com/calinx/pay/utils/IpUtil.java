package com.calinx.pay.utils;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author yj
 * @Create 2019/11/20 19:30
 */

public class IpUtil {

    public static String getIp(HttpServletRequest request) {
        if (request == null)
            return "";
        String ip = request.getHeader("X-Requested-For");
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
