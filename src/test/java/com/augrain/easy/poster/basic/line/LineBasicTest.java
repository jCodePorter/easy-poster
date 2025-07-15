package com.augrain.easy.poster.basic.line;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.model.LineStyle;
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
        EasyPoster poster = new EasyPoster(500, 500);

        poster.addLineElement(Point.of(0, 200), Point.of(500, 200))
                .setLineStyle(LineStyle.DASH)
                .setColor(Color.BLUE);

        poster.addLineElement(Point.of(0, 250), Point.of(500, 250))
                .setLineStyle(LineStyle.DOT)
                .setColor(Color.RED);

        poster.addLineElement(Point.of(0, 300), Point.of(500, 300))
                .setLineStyle(LineStyle.DASH_DOT)
                .setColor(Color.ORANGE);

        poster.asFile("png", "out_line_basic.png");
    }

}
