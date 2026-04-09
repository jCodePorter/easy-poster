package com.bytefuture.easy.poster.element.special;

import com.bytefuture.easy.poster.element.AbstractDimensionElement;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.utils.QrCodeUtil;
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
        context.getGraphics().drawImage(bufferedImage, point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight(), null);
        return point;
    }
}
