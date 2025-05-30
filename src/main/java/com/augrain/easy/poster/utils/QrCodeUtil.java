package com.augrain.easy.poster.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Hashtable;

public class QrCodeUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int margin = 0;
    private static final int LogoPart = 4;

    /**
     * 设置生成二维码矩阵信息
     * <p>
     * 二维码纠错级别指的是在识别二维码时，对于损坏或模糊的二维码的容错能力。
     * 一般来说，二维码有四个纠错级别：
     * L (低)：可以纠正7%左右的错误。
     * M (中)：可以纠正15%左右的错误。
     * Q (高)：可以纠正25%左右的错误。
     * H (高)：可以纠正30%左右的错误。
     * 总结：一般来说，使用较高的纠错级别会导致生成的二维码更大，但是它的容错能力也会更强。
     *
     * @param content 二维码图片内容
     * @param width   二维码图片宽度
     * @param height  二维码图片高度
     */
    public static BitMatrix setBitMatrix(String content, int width, int height) {
        BitMatrix bitMatrix = null;
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        hints.put(EncodeHintType.MARGIN, margin);
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            return bitMatrix;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    /**
     * 给二维码图片中绘制logo信息 非必须
     *
     * @param image    二维码图片
     * @param logoPath logo图片路径
     */
    private static BufferedImage addLogo(BufferedImage image, String logoPath) throws IOException {
        Graphics2D g = image.createGraphics();
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // 计算logo图片大小,可适应长方形图片,根据较短边生成正方形
        int width = image.getWidth() < image.getHeight() ? image.getWidth() / LogoPart : image.getHeight() / LogoPart;
        int height = width;

        // 计算logo图片放置位置
        int x = (image.getWidth() - width) / 2;
        int y = (image.getHeight() - height) / 2;

        // 在二维码图片上绘制中间的logo
        g.drawImage(logoImage, x, y, width, height, null);
        // 绘制logo边框,可选
        g.setStroke(new BasicStroke(2)); // 画笔粗细
        g.setColor(Color.WHITE); // 边框颜色
        g.drawRect(x, y, width, height); // 矩形边框
        logoImage.flush();
        g.dispose();
        return image;
    }

    private static void writeToFile(BitMatrix matrix, String format, OutputStream outStream, String logoPath) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!StringUtils.isEmpty(logoPath)) {
            image = addLogo(image, logoPath);
        }
        ImageIO.write(image, format, outStream);
    }

    public static void main(String[] args) throws WriterException {
        String content = "www.augrain.cnwww.augrain.cnwww.augrain.cn";
        String format = "jpg";
        int width = 400;
        int height = 400;
        BitMatrix bitMatrix = setBitMatrix(content, width, height);
        OutputStream outStream;

        String path = "d:/code" + new Date().getTime() + ".png";
        try {
            outStream = Files.newOutputStream(new File(path).toPath());
            writeToFile(bitMatrix, format, outStream, null);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}