package com.augrain.easy.poster.special;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.special.RegularPolygonElement;
import com.augrain.easy.poster.geometry.PositionDirection;
import com.augrain.easy.poster.geometry.RelativePosition;
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
        EasyPoster canvas = new EasyPoster(500, 500);

        RegularPolygonElement element = new RegularPolygonElement(100, 8);
        element.setColor(Color.red);
        element.setPosition(RelativePosition.of(PositionDirection.CENTER));

        canvas.addElement(element);

        canvas.asFile("png", "regular_polygon.png");
    }
}
