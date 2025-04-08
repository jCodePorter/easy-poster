package com.augrain.easy.canvas.special;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.special.FivePointedStarElement;
import com.augrain.easy.canvas.geometry.PositionDirection;
import com.augrain.easy.canvas.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

/**
 * 圆形基本测试
 *
 * @author biaoy
 * @since 2025/03/17
 */
public class FivePointStarTest {

    @Test
    public void testBasic() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        FivePointedStarElement fivePointedStarElement = new FivePointedStarElement(100);
        fivePointedStarElement.setColor(Color.red);
        fivePointedStarElement.setBorderSize(10);
        fivePointedStarElement.setPosition(RelativePosition.of(PositionDirection.CENTER));

        canvas.addElement(fivePointedStarElement);

        canvas.asFile("png", "five_point_star.png");
    }

}
