package com.augrain.easy.canvas.special;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.special.RegularPolygonElement;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

/**
 * 正N边形
 *
 * @author biaoy
 * @since 2025/03/29
 */
public class RegularPolygonElementTest {

    @Test
    public void testBasicRegularPolygon() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        RegularPolygonElement element = new RegularPolygonElement(100, 8);
        element.setColor(Color.red);
        element.setPosition(RelativePosition.of(Positions.CENTER));

        canvas.addElement(element);

        canvas.asFile("png", "regular_polygon.png");
    }
}
