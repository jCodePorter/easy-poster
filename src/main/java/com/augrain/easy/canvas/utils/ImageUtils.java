package com.augrain.easy.canvas.utils;

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage rotate(BufferedImage src, int angle) {
        int[] bounds = RotateUtils.newBounds(src.getWidth(), src.getHeight(), angle);
        int newWidth = bounds[0];
        int newHeight = bounds[1];

        // 创建新的图像对象
        BufferedImage outImg = new BufferedImage(newWidth, newHeight, src.getType());

        // 在新的图像上绘制旋转后的图像
        Graphics2D g2d = outImg.createGraphics();
        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(angle), newWidth / 2, newHeight / 2);
        g2d.setTransform(transform);
        g2d.drawImage(src, (newWidth - src.getWidth()) / 2, (newHeight - src.getHeight()) / 2, null);
        g2d.dispose();
        return outImg;
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
            throw new RuntimeException("请使用宽高相等的图片");
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
}
