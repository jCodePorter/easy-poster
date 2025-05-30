package com.augrain.easy.poster.element.basic;

import com.augrain.easy.poster.element.AbstractDimensionElement;
import com.augrain.easy.poster.element.IElement;
import com.augrain.easy.poster.geometry.CoordinatePoint;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.CanvasContext;
import com.augrain.easy.poster.model.Scale;
import com.augrain.easy.poster.utils.ImageUtils;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图片元素
 *
 * @author biaoy
 * @since 2025/02/20
 */
@Getter
public class ImageElement extends AbstractDimensionElement<ImageElement> implements IElement {
    /**
     * 输入的图片对象
     */
    private BufferedImage image;

    public ImageElement(BufferedImage image) {
        this.image = image;
        handleDimension();
    }

    /**
     * @param httpUrl 图片url
     */
    public ImageElement(String httpUrl) {
        this.image = ImageUtils.loadUrl(httpUrl);
        handleDimension();
    }

    public ImageElement(File file) {
        this.image = ImageUtils.loadFile(file);
        handleDimension();
    }

    private void handleDimension() {
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * 缩放
     *
     * @param scale 缩放参数
     */
    public ImageElement scale(Scale scale) {
        this.image = ImageUtils.scale(image, scale);
        handleDimension();
        return this;
    }

    /**
     * 旋转
     *
     * @param angel 角度
     */
    public ImageElement rotate(int angel) {
        this.image = ImageUtils.rotate(image, angel);
        handleDimension();
        return this;
    }

    /**
     * 裁剪
     *
     * @param ratio 裁剪比例，如"1:1", "4:3"
     */
    public ImageElement crop(String ratio) {
        this.image = ImageUtils.crop(image, ratio);
        handleDimension();
        return this;
    }

    /**
     * 圆角
     *
     * @param roundCorner 圆角宽度
     */
    public ImageElement roundCorner(final int roundCorner) {
        this.image = ImageUtils.roundedCorner(image, roundCorner);
        handleDimension();
        return this;
    }

    @Override
    public CoordinatePoint doRender(CanvasContext context, Dimension dimension, int canvasWidth, int canvasHeight) {
        CoordinatePoint point = dimension.getPoint();
        context.getGraphics().drawImage(this.getImage(), point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight(), null);
        return point;
    }
}
