package com.calinx.pay.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUtil {

    public static Map post(JSONObject param, String url) {
        Object map = new HashMap();

        try {
            URL postUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)postUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            Throwable var6 = null;

            try {
                outputStream.write(param.toString().getBytes("utf-8"));
            } catch (Throwable var31) {
                var6 = var31;
                throw var31;
            } finally {
                if (outputStream != null) {
                    if (var6 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable var30) {
                            var6.addSuppressed(var30);
                        }
                    } else {
                        outputStream.close();
                    }
                }

            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            var6 = null;

            try {
                StringBuffer buffer = new StringBuffer();

                while(true) {
                    String lines;
                    if ((lines = reader.readLine()) == null) {
                        map = JSONUtil.toBean(buffer.toString(),Map.class);
                        break;
                    }

                    lines = new String(lines.getBytes(), "utf-8");
                    buffer.append(lines);
                }
            } catch (Throwable var32) {
                var6 = var32;
                throw var32;
            } finally {
                if (reader != null) {
                    if (var6 != null) {
                        try {
                            reader.close();
                        } catch (Throwable var29) {
                            var6.addSuppressed(var29);
                        }
                    } else {
                        reader.close();
                    }
                }

            }

            connection.disconnect();
        } catch (Exception var35) {
            var35.printStackTrace();
        }

        return (Map)map;
    }

    public static void flushXML(HttpServletResponse response, String xml) {
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/xml");

        try {
            response.getWriter().print(xml);
            response.flushBuffer();
        } catch (Exception var3) {
        }

    }

    public static void flushText(HttpServletResponse response, String text) {
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("text/plain");

        try {
            response.getWriter().print(text);
            response.flushBuffer();
        } catch (Exception var3) {
        }

    }

    public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        } else {
            byte[] buffer = new byte[contentLength];

            int readlen;
            for(int i = 0; i < contentLength; i += readlen) {
                readlen = request.getInputStream().read(buffer, i, contentLength - i);
                if (readlen == -1) {
                    break;
                }
            }

            return buffer;
        }
    }

    public static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }

        return new String(buffer, charEncoding);
    }

    public static Map<String, String> getRequestPostParams(HttpServletRequest request) {
        Map<String, String> params = new LinkedHashMap();
        Map requestParams = request.getParameterMap();
        Iterator iter = requestParams.keySet().iterator();

        while(iter.hasNext()) {
            String name = (String)iter.next();
            String[] values = (String[])((String[])requestParams.get(name));
            String valueStr = "";

            for(int i = 0; i < values.length; ++i) {
                valueStr = i == values.length - 1 ? valueStr + values[i] : valueStr + values[i] + ",";
            }

            params.put(name, valueStr);
        }

        return params;
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }


}
