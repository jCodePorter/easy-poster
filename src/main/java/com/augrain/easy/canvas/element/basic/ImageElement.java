package com.augrain.easy.canvas.element.basic;

import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.enums.ZoomMode;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.utils.ImageUtils;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片元素
 *
 * @author biaoy
 * @since 2025/02/20
 */
@Getter
public class ImageElement extends AbstractElement implements IElement {

    /**
     * 图片缩放方式
     */
    private final ZoomMode zoomMode;

    /**
     * 设置的待输出的图片宽度
     */
    private int width;

    /**
     * 设置的待输出的图片高度
     */
    private int height;

    /**
     * 输入的图片对象
     */
    private final BufferedImage image;

    public ImageElement(BufferedImage image) {
        this.zoomMode = ZoomMode.ORIGIN;
        this.image = image;
    }

    /**
     * @param httpUrl 图片url
     */
    public ImageElement(String httpUrl) {
        this.zoomMode = ZoomMode.ORIGIN;
        this.image = ImageUtils.loadUrl(httpUrl);
    }

    /**
     * @param image 图片对象
     */
    public ImageElement(BufferedImage image, int width, int height, ZoomMode zoom) {
        this.image = image;
        this.width = width;
        this.height = height;
        this.zoomMode = zoom;
    }

    @Override
    public Dimension calDimension(Graphics2D g, int canvasWidth, int canvasHeight) {
        ZoomMode zoomMode = this.getZoomMode();
        BufferedImage image = this.getImage();
        int width = 0;
        int height = 0;
        switch (zoomMode) {
            case ORIGIN:
                width = image.getWidth();
                height = image.getHeight();
                break;
            case WIDTH:
                width = this.getWidth();
                height = image.getHeight() * width / image.getWidth();
                break;
            case HEIGHT:
                height = this.getHeight();
                width = image.getWidth() * height / image.getHeight();
                break;
            case WIDTH_HEIGHT:
                height = this.getHeight();
                width = this.getWidth();
                break;
        }
        CoordinatePoint point = CoordinatePoint.ORIGIN_COORDINATE;
        if (position != null) {
            point = position.calculate(canvasWidth, canvasHeight, width, height);
        }
        return Dimension.builder()
                .width(width)
                .height(height)
                .point(point)
                .build();
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        CoordinatePoint point;
        if (isPositionUpdated()) {
            point = getPosition().calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());
        } else {
            point = dimension.getPoint();
        }
        BufferedImage image = this.getImage();
        g.drawImage(image, point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight(), null);
        return point;
    }
}
