package com.augrain.easy.canvas.utils;

import com.augrain.easy.canvas.enums.ZoomMode;
import com.augrain.easy.canvas.exception.CanvasException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * 图片工具类
 *
 * @author biaoy
 * @since 2025/03/09
 */
public class ImageUtils {

    private ImageUtils() {

    }

    /**
     * 通过http请求加载图片
     *
     * @param httpUrl 图片 http url
     * @return BufferedImage
     */
    public static BufferedImage loadUrl(String httpUrl) {
        try {
            return ImageIO.read(new URL(httpUrl));
        } catch (Exception e) {
            throw new CanvasException(e);
        }
    }

    /**
     * 通过本地文件加载图片
     *
     * @param file 本地图片
     * @return BufferedImage
     */
    public static BufferedImage loadFile(File file) {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            throw new CanvasException(e);
        }
    }

    public static BufferedImage rotate(BufferedImage image, int angle) {
        int[] bounds = RotateUtils.newBounds(image.getWidth(), image.getHeight(), angle);
        int newWidth = bounds[0];
        int newHeight = bounds[1];

        // 创建旋转后的图片
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 计算旋转中心点
        int x = (newWidth - image.getWidth()) / 2;
        int y = (newHeight - image.getHeight()) / 2;

        // 进行旋转
        double radians = Math.toRadians(angle);
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(radians, image.getWidth() / 2.0, image.getHeight() / 2.0);
        g2d.setTransform(transform);

        // 绘制旋转后的图片
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return rotatedImage;
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        g2.setComposite(AlphaComposite.SrcIn);

        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();
        return output;
    }

    public static BufferedImage makeCircleCorner(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        if (w != h) {
            throw new CanvasException("请使用宽高相等的图片");
        }
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, w, h);
        g2.setClip(shape);

        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();
        return output;
    }

    /**
     * 缩放
     */
    public static BufferedImage scale(BufferedImage image, Integer outWidth, Integer outHeight, ZoomMode zoomMode) {
        int width = 0;
        int height = 0;
        switch (zoomMode) {
            case WIDTH:
                width = outWidth;
                height = image.getHeight() * width / image.getWidth();
                break;
            case HEIGHT:
                height = outHeight;
                width = image.getWidth() * height / image.getHeight();
                break;
            case WIDTH_HEIGHT:
                height = outHeight;
                width = outWidth;
                break;
        }
        Graphics2D graphics = null;
        try {
            Image scaledInstance = image.getScaledInstance(outWidth, outHeight, Image.SCALE_SMOOTH);
            BufferedImage scaleImg = new BufferedImage(width, height, image.getType());
            graphics = scaleImg.createGraphics();
            graphics.drawImage(scaledInstance, 0, 0, null);
            return scaleImg;
        } finally {
            if (null != graphics) {
                graphics.dispose();
            }
        }
    }

    /**
     * 按照指定比例裁剪图片
     *
     * @param image 输入的源文件BufferedImage
     * @param ratio 裁剪比例，格式为 "width:height"，例如 "1:1", "3:4", "4:3"
     */
    public static BufferedImage crop(BufferedImage image, String ratio) {
        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        // 解析比例
        String[] ratioParts = ratio.split(":");
        if (ratioParts.length != 2) {
            throw new CanvasException("比例格式不正确，应为 'width:height'，例如 '1:1'");
        }
        double targetRatio = Double.parseDouble(ratioParts[0]) / Double.parseDouble(ratioParts[1]);

        // 计算裁剪区域的宽度和高度
        int cropWidth;
        int cropHeight;
        if (srcWidth / (double) srcHeight > targetRatio) {
            // 图片宽度过大，按照高度裁剪
            cropHeight = srcHeight;
            cropWidth = (int) (cropHeight * targetRatio);
        } else {
            // 图片高度过大，按照宽度裁剪
            cropWidth = srcWidth;
            cropHeight = (int) (cropWidth / targetRatio);
        }

        // 计算裁剪区域的起始坐标（居中裁剪）
        int x = (srcWidth - cropWidth) / 2;
        int y = (srcHeight - cropHeight) / 2;

        // 裁剪图片
        return image.getSubimage(x, y, cropWidth, cropHeight);
    }
}
