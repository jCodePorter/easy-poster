package com.augrain.easy.poster.element.special;

import com.augrain.easy.poster.element.AbstractDimensionElement;
import com.augrain.easy.poster.geometry.CoordinatePoint;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.model.CanvasContext;
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
    public CoordinatePoint doRender(CanvasContext context, Dimension dimension, int canvasWidth, int canvasHeight) {
        BitMatrix bitMatrix = QrCodeUtil.setBitMatrix(content, width, height);
        BufferedImage bufferedImage = QrCodeUtil.toBufferedImage(bitMatrix);

        CoordinatePoint point = dimension.getPoint();
        context.getGraphics().drawImage(bufferedImage, point.getX(), point.getY(), dimension.getWidth(), dimension.getHeight(), null);
        return point;
    }
}
