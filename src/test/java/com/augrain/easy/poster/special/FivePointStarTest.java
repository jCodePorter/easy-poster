package com.augrain.easy.poster.special;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.special.FivePointedStarElement;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.RelativePosition;
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
        EasyPoster poster = new EasyPoster(500, 500);

        FivePointedStarElement fivePointedStarElement = new FivePointedStarElement(100);
        fivePointedStarElement.setColor(Color.red);
        fivePointedStarElement.setBorderSize(10);
        fivePointedStarElement.setPosition(RelativePosition.of(Direction.CENTER));

        poster.addElement(fivePointedStarElement);

        poster.asFile("png", "out_five_point_star.png");
    }

}
