package com.bytefuture.easy.poster.ui.special;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.special.RegularPolygonElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.RelativePosition;
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
        EasyPoster poster = new EasyPoster(500, 500);

        RegularPolygonElement element = new RegularPolygonElement(100, 8);
        element.setColor(Color.red);
        element.setPosition(RelativePosition.of(Direction.CENTER));

        poster.addElement(element);

        poster.asFile("png", "out_regular_polygon.png");
    }
}
