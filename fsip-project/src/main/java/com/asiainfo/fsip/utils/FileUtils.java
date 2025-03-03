package com.asiainfo.fsip.utils;

import com.asiainfo.mcp.tmc.common.util.MD5;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

@Slf4j
public class FileUtils {

    public static String buildFileName(HttpServletRequest request, String fileName) throws Exception {
        //获取浏览器类型
        String agent = request.getHeader("User-Agent").toLowerCase();
        if (agent != null && (agent.indexOf("msie") != -1 || (agent.indexOf("rv") != -1 && agent.indexOf("firefox") == -1))) {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String((fileName).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }

        return fileName;
    }

    /**
     * 写文件
     *
     * @param outputStream
     * @param inputStream
     * @throws IOException
     */
    public static void writeFile(OutputStream outputStream, InputStream inputStream) throws
            IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            log.error("could not writeFile", e);
            throw e;
        }
    }

    /**
     * 生成图片
     *
     * @param content  链接
     * @param charSet  编码
     * @param qrWidth  宽度
     * @param qrHeight 高度
     * @return oss地址
     */
    public static BufferedImage transformPhoto(String content, String charSet, int qrWidth, int qrHeight) {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, charSet);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrWidth, qrHeight,
                    hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    public static String getFileType(InputStream is) throws Exception {
        byte[] b = new byte[4];
        is.read(b, 0, b.length);
        String value = bytesToHexString(b);
        if (StringUtils.startsWith(value, "FFD8FF")) {
            value = value.substring(0, 6);
        }
        return value;
    }

    /**
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     * <p>
     * 方法描述：将要读取文件头信息的文件的byte数组转换成string类型表示
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static String buildFileName(String name){
        if(StringUtils.isEmpty(name)){
            return name;
        }
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex > -1){
            String fileName = name.substring(0,lastIndex);
            StringBuilder sb = new StringBuilder(fileName).append("-").append(System.currentTimeMillis()).append(name.substring(lastIndex));;
            return sb.toString();
        }

        return name;
    }

    public static String getFileExtension(String name){
        if(StringUtils.isEmpty(name)){
            return name;
        }
        int lastIndex = name.lastIndexOf(".");
        if (lastIndex > -1){
            return name.substring(lastIndex);
        }

        return name;
    }

    public static String buildMd5Name(InputStream is, String name) throws Exception{
        byte[] bytes = IOUtils.toByteArray(is);
        String md5Name = MD5.md5(bytes);
        StringBuilder builder = new StringBuilder(md5Name).append(getFileExtension(name));
        return builder.toString();
    }
}
