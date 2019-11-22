package com.calinx.pay.utils;

import cn.hutool.core.codec.Base64Encoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * @Author yj
 * @Create 2019/11/21 18:56
 */

public class QRCodeUtil {

    private static final int BLACK = 0XFF000000;
    private static final int WHITE = 0XFFFFFFFF;


    /**
     * 生成二维码基础图片
     * @param matrix
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        BufferedImage bi = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < matrix.getWidth(); i++) {
            for (int j = 0; j < matrix.getHeight(); j++) {
                //有值的是黑色，没有值是白色
                bi.setRGB(i, j, matrix.get(i, j) ? BLACK : WHITE);
            }
        }
        return bi;
    }


    /**
     * 读取本地文件生成二维码
     * @param imgPath
     * @return
     */
    public static Image localImageLogo(String imgPath){
        try {
            //读取logo图片流
            BufferedImage logoImage = ImageIO.read(new File(imgPath));
            //设置logo大小
            Image image = logoImage.getScaledInstance(70, 70, Image.SCALE_FAST);
            return image;
        }catch (Exception e){
            throw new RuntimeException("生成logo失败:"+e.getMessage());
        }
    }


    /**
     * 根据路径读取网络图片 生成logo
     * @param imgUrl
     * @return
     * @throws IOException
     */
    public static Image netImageLogo(String imgUrl) throws IOException {
        InputStream inputStream = null;
        try {
            // 创建URL
            URL url = new URL(imgUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inputStream = conn.getInputStream();
            BufferedImage logoImage = ImageIO.read(inputStream);
            Image image = logoImage.getScaledInstance(70, 70, Image.SCALE_FAST);
            return image;
        } catch (IOException e) {
            throw new RuntimeException("读取图片失败:"+e.getMessage());
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
    }



    /***
     *指定内容和图片生成二维码 转成Base64输出
     * @param format   输出类型(png、jpg)
     * @param content  内容
     * @return
     */
    public static String produceQrCodeWithLogoToBase64(String format,String content,String imgPath) throws IOException {
        String code = null;
        ByteArrayOutputStream outputStream = null;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            HashMap<EncodeHintType, Object> table = new HashMap<>();
            table.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitmatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400,400,table);
            BufferedImage bufferedImage = toBufferedImage(bitmatrix);
            //获取logo图片（此处选择本地图片还是 网络图片）
            Image image = localImageLogo(imgPath);
            //获取画笔
            Graphics graphics = bufferedImage.getGraphics();
            //将logo画在二维码上
            graphics.drawImage(image,165,165,null);
            //创建新的输出流
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,format,outputStream);
            //流中获取byte数组
            byte[] bytes = outputStream.toByteArray();
            code = new Base64Encoder().encode(bytes);
            return code;
        }catch (Exception e){
            throw new RuntimeException("生成二维码失败:"+e.getMessage());
        }finally {
            //释放资源
            if(outputStream != null){
                outputStream.close();
            }
        }
    }






    /***
     * 指定内容生成二维码  转成Base64输出
     * @param format   输出类型(png、jpg)
     * @param content  内容
     * @return
     */
    public static String prouceQrCodeToBase64(String format,String content) throws IOException {
        String code = null;
        ByteArrayOutputStream outputStream = null;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            HashMap<EncodeHintType, Object> table = new HashMap<>();
            table.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitmatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400,400,table);
            BufferedImage bufferedImage = toBufferedImage(bitmatrix);
            //创建新的输出流
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,format,outputStream);
            //流中获取byte数组
            byte[] bytes = outputStream.toByteArray();
            code = new Base64Encoder().encode(bytes);
            return code;
        }catch (Exception e){
            throw new RuntimeException("生成二维码失败:"+e.getMessage());
        }finally {
            //释放资源
            if(outputStream != null){
                outputStream.close();
            }
        }
    }


    /**
     * 指定内容和本地logo  生成二维码输出到本地
     * @param format      二维码格式
     * @param content     二维码内容
     * @param localImagePath     图片路径
     * @param localPath   输出路径
     * @throws IOException
     */
    public static void produceQrCodeWithLogoTolocal(String format,String content,String localImagePath,String localPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            MultiFormatWriter multiWriter=new MultiFormatWriter();
            HashMap<EncodeHintType, Object> table = new HashMap<>();
            table.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitmatrix = multiWriter.encode(content, BarcodeFormat.QR_CODE, 400,400,table);
            BufferedImage bufferedImage = toBufferedImage(bitmatrix);
            //获取logo图片（此处选择本地图片还是 网络图片）
            Image image = localImageLogo(localImagePath);
            //获取画笔
            Graphics graphics = bufferedImage.getGraphics();
            //将logo画在二维码上
            graphics.drawImage(image,165,165,null);
            //操作输出文件夹
            File file = new File(localImagePath);
            if(!file.getParentFile().exists()){
                //如果文件的父路径不存在 创建文件夹
                file.getParentFile().mkdirs();
            }
            fileOutputStream = new FileOutputStream(file);
            ImageIO.write(bufferedImage,format,fileOutputStream);
        }catch (Exception e){
            throw new RuntimeException("生成二维码失败:"+e.getMessage());
        }finally {
            //释放资源
            if(fileOutputStream != null){
                fileOutputStream.close();
            }
        }
    }


    /**
     * 指定内容生成二维码输出到本地
     * @param format    输出格式
     * @param content   二维码内容
     * @param localPath  输出路径
     * @throws IOException
     */
    public static void produceQrCodeTolocal(String format,String content,String localPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            MultiFormatWriter multiWriter=new MultiFormatWriter();
            HashMap<EncodeHintType, Object> table = new HashMap<>();
            table.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitmatrix = multiWriter.encode(content, BarcodeFormat.QR_CODE, 400,400,table);
            BufferedImage bufferedImage = toBufferedImage(bitmatrix);
            //操作输出文件夹
            File file = new File(localPath);
            if(!file.getParentFile().exists()){
                //如果文件的父路径不存在 创建文件夹
                file.getParentFile().mkdirs();
            }
            fileOutputStream = new FileOutputStream(file);
            ImageIO.write(bufferedImage,format,fileOutputStream);
        }catch (Exception e){
            throw new RuntimeException("生成二维码失败:"+e.getMessage());
        }finally {
            //释放资源
            if(fileOutputStream != null){
                fileOutputStream.close();
            }
        }
    }










}
