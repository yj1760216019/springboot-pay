package com.calinx.pay.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.*;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);


    static {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(1000).setSocketTimeout(4000).setExpectContinueEnabled(true).build();
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
        pool.setMaxTotal(300);
        pool.setDefaultMaxPerRoute(50);
        HttpRequestRetryHandler retryHandler = (IOException exception, int executionCount, HttpContext context) -> {
            if (executionCount > 1) {
                return false;
            }
            if (exception instanceof NoHttpResponseException) {
                logger.info("[NoHttpResponseException has retry request:" + context.toString() + "][executionCount:" + executionCount + "]");
                return true;
            } else if (exception instanceof SocketException) {
                logger.info("[SocketException has retry request:" + context.toString() + "][executionCount:" + executionCount + "]");
                return true;
            }
            return false;
        };
        httpClient = HttpClients.custom().setConnectionManager(pool).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler).build();
    }




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



    private HttpUtil() {
    }

    private final static String UTF8 = StandardCharsets.UTF_8.displayName();

    private static CloseableHttpClient httpClient;



    /**
     * @param certPath
     * @param password
     * @return
     * @throws Exception
     */
    public static CloseableHttpClient sslHttpsClient(String certPath, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = new FileInputStream(new File(certPath))) {
            keyStore.load(inputStream, password.toCharArray());
        }
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
    }


    /**
     * 设置请求头信息
     *
     * @param headers
     * @param request
     * @return
     */
    private static void setHeaders(Map<String, String> headers, HttpRequest request) {
        if (null != headers && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    /**
     * 发送post请求请求体为xml
     *
     * @param url
     * @param xml
     * @param headers
     * @return
     */
    public static String sendPostXml(String url, String xml, Map<String,String> headers) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            setHeaders(headers, httpPost);
            StringEntity entity = new StringEntity(xml, StandardCharsets.UTF_8);
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseData = response.getEntity();
            result = EntityUtils.toString(responseData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送json请求
     *
     * @param url
     * @param json
     * @return
     */
    public static String sendPostJson(String url, String json, Map<String, String> headers) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            setHeaders(headers, httpPost);
            StringEntity stringEntity = new StringEntity(json, StandardCharsets.UTF_8);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseData = response.getEntity();
            result = EntityUtils.toString(responseData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param params
     * @param header
     * @return
     */
    public static String sendGet(String url, Map<String, String> params, Map<String, String> header) {
        String result = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            if (params != null && params.size() > 0) {
                List<NameValuePair> pairs = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
                builder.setParameters(pairs);
            }
            HttpGet httpGet = new HttpGet(builder.build());
            setHeaders(header, httpGet);
            HttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 发送get请求
     *
     * @param url
     * @param xml
     * @param headers
     * @return
     */
    public static String sendSslXmlPost(String url, String xml, Map<String, String> headers, CloseableHttpClient httpClient) {
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            setHeaders(headers, httpPost);
            StringEntity entity = new StringEntity(xml, StandardCharsets.UTF_8);
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseData = response.getEntity();
            result = EntityUtils.toString(responseData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }






}
