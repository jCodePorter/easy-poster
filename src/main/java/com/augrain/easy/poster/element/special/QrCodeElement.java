package com.augrain.easy.poster.element.special;

import com.augrain.easy.poster.element.AbstractDimensionElement;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.PosterContext;
import com.augrain.easy.poster.utils.QrCodeUtil;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;

/**
 * 二维码
 *
 * @author biaoy
 * @since 2025/05/07
 */
public class QrCodeElement extends AbstractDimensionElement<QrCodeElement> {

    private final String content;

    public QrCodeElement(String content, int with, int height) {
        this.width = with;
        this.height = height;
        this.content = content;
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        BitMatrix bitMatrix = QrCodeUtil.setBitMatrix(content, width, height);
        BufferedImage bufferedImage = QrCodeUtil.toBufferedImage(bitMatrix);

        Point point = dimension.getPoint();

        int renderX = point.getX() + dimension.getXOffset();
        int renderY = point.getY() + dimension.getYOffset();
        context.getGraphics().drawImage(bufferedImage, renderX, renderY, dimension.getWidth(), dimension.getHeight(), null);
        return point;
    }
}
