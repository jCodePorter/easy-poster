package com.augrain.easy.canvas.basic.line;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import org.junit.Test;

import java.awt.*;

/**
 * 线段测试
 *
 * @author biaoy
 * @since 2025/03/21
 */
public class LineBasicTest {

    @Test
    public void testBasicLine() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        canvas.addLineElement(CoordinatePoint.of(0, 250), CoordinatePoint.of(500, 250))
                .setColor(Color.BLUE);

        canvas.asFile("png", "line_basic.png");
    }

}
