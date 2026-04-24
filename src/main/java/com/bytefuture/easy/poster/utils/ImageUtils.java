package com.bytefuture.easy.poster.utils;

import com.bytefuture.easy.poster.exception.PosterException;
import com.bytefuture.easy.poster.model.Scale;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;

/**
 * 图片工具类
 *
 * @author biaoy
 * @since 2025/03/09
 */
public class ImageUtils {
    private static final int CONNECT_TIMEOUT_MILLIS = 3000;
    private static final int READ_TIMEOUT_MILLIS = 5000;
    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024;

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
            URL url = validateRemoteImageUrl(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");

            int contentLength = connection.getContentLength();
            if (contentLength > MAX_IMAGE_BYTES) {
                throw new PosterException("remote image is too large");
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new PosterException("remote image request failed: " + responseCode);
            }

            try (InputStream inputStream = new BoundedInputStream(connection.getInputStream(), MAX_IMAGE_BYTES)) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new PosterException("unsupported remote image content");
                }
                return image;
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            if (e instanceof PosterException) {
                throw (PosterException) e;
            }
            throw new PosterException(e);
        }
    }

    private static URL validateRemoteImageUrl(String httpUrl) throws Exception {
        if (httpUrl == null || httpUrl.trim().isEmpty()) {
            throw new PosterException("remote image url can not be empty");
        }

        URI uri = new URI(httpUrl.trim());
        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
            throw new PosterException("only http/https remote image urls are allowed");
        }
        if (uri.getHost() == null || uri.getHost().trim().isEmpty()) {
            throw new PosterException("remote image host can not be empty");
        }

        InetAddress address = InetAddress.getByName(uri.getHost());
        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isSiteLocalAddress()
                || address.isLinkLocalAddress()) {
            throw new PosterException("local or private remote image hosts are not allowed");
        }

        return uri.toURL();
    }

    private static final class BoundedInputStream extends FilterInputStream {
        private final int maxBytes;
        private int bytesRead;

        private BoundedInputStream(InputStream inputStream, int maxBytes) {
            super(inputStream);
            this.maxBytes = maxBytes;
        }

        @Override
        public int read() throws IOException {
            int value = super.read();
            if (value != -1) {
                incrementCount(1);
            }
            return value;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            int read = super.read(buffer, offset, length);
            if (read > 0) {
                incrementCount(read);
            }
            return read;
        }

        private void incrementCount(int delta) {
            bytesRead += delta;
            if (bytesRead > maxBytes) {
                throw new PosterException("remote image is too large");
            }
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
            throw new PosterException(e);
        }
    }

    /**
     * 圆角
     *
     * @param image 输入的源文件BufferedImage
     * @param angle 旋转角度
     */
    public static BufferedImage rotate(BufferedImage image, int angle) {
        int[] bounds = RotateUtils.newBounds(image.getWidth(), image.getHeight(), angle);
        int newWidth = bounds[0];
        int newHeight = bounds[1];

        // 创建旋转后的图片
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotatedImage.createGraphics();

        // 启用抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 计算旋转中心点
        int x = (newWidth - image.getWidth()) / 2;
        int y = (newHeight - image.getHeight()) / 2;

        // 进行旋转
        double radians = Math.toRadians(angle);
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(radians, image.getWidth() / 2.0, image.getHeight() / 2.0);
        g.setTransform(transform);

        // 绘制旋转后的图片
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return rotatedImage;
    }

    /**
     * 圆角
     *
     * @param image        输入的源文件BufferedImage
     * @param cornerRadius 圆角宽度
     */
    public static BufferedImage roundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        g.setComposite(AlphaComposite.SrcIn);

        g.drawImage(image, 0, 0, w, h, null);
        g.dispose();
        return output;
    }

    /**
     * 缩放
     *
     * @param image 输入的源文件BufferedImage
     * @param scale 缩放参数
     */
    public static BufferedImage scale(BufferedImage image, Scale scale) {
        int width = 0;
        int height = 0;
        switch (scale.getZoomMode()) {
            case WIDTH:
                width = scale.getWidth();
                height = image.getHeight() * width / image.getWidth();
                break;
            case HEIGHT:
                height = scale.getHeight();
                width = image.getWidth() * height / image.getHeight();
                break;
            case WIDTH_HEIGHT:
                width = scale.getWidth();
                height = scale.getHeight();
                break;
            case RATIO:
                height = (int) (scale.getRatio() * image.getHeight());
                width = (int) (scale.getRatio() * image.getWidth());
                break;
        }
        Image scaledInstance = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaleImg = new BufferedImage(width, height, image.getType());
        Graphics2D g = scaleImg.createGraphics();
        g.drawImage(scaledInstance, 0, 0, null);
        g.dispose();
        return scaleImg;
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
            throw new PosterException("比例格式不正确，应为 'width:height'，例如 '1:1'");
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
