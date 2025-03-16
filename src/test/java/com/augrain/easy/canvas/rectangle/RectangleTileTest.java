package com.augrain.easy.canvas.rectangle;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.advance.TileElement;
import com.augrain.easy.canvas.element.basic.RectangleElement;
import com.augrain.easy.canvas.geometry.Margin;
import org.junit.Test;

import java.awt.*;

/**
 * 矩形基本测试
 *
 * @author biaoy
 * @since 2025/03/16
 */
public class RectangleTileTest {

    @Test
    public void testBasicRectangle() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        TileElement tileElement = new TileElement(new RectangleElement(100, 100)
                .setColor(Color.PINK)
                .setRoundCorner(30))
                .setPadding(20, 20)
                .setLayout(4, 4, Margin.of(10));

        canvas.addElement(tileElement);
        canvas.asFile("png", "rectangle_tile.png");
    }
}
