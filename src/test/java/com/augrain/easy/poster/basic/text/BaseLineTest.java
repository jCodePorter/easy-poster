package com.augrain.easy.poster.basic.text;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.AbstractElement;
import com.augrain.easy.poster.element.basic.TextElement;
import com.augrain.easy.poster.geometry.*;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.model.BaseLine;
import org.junit.Test;

import java.awt.*;

/**
 * 测试文本基线
 *
 * @author biaoy
 * @since 2025/03/14
 */
public class BaseLineTest {

    @Test
    public void testAbsoluteBaseLine() {
        EasyPoster poster = new EasyPoster(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        poster.addTextElement("中心对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(AbsolutePosition.of(Point.of(0, 00), Direction.LEFT_CENTER));

        poster.addTextElement("顶部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(Point.of(120, 0), Direction.LEFT_CENTER));

        poster.addTextElement("底部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(AbsolutePosition.of(Point.of(240, 0), Direction.LEFT_CENTER));

        poster.addTextElement("基线对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(AbsolutePosition.of(Point.of(360, 0), Direction.LEFT_CENTER));

        poster.addLineElement(Point.of(0, 0), Point.of(500, 0))
                .setColor(Color.BLUE);

        poster.asFile("png", "text_absolute_base_line.png");
    }

    @Test
    public void testRelativeBaseLine() {
        EasyPoster poster = new EasyPoster(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        poster.addTextElement("中心对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of().setMarginLeft(0)));

        poster.addTextElement("顶部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.TOP)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of().setMarginLeft(120)));

        poster.addTextElement("底部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of().setMarginLeft(240)));

        poster.addTextElement("基线对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of().setMarginLeft(360)));

        poster.addLineElement(Point.of(0, 250), Point.of(500, 250))
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setColor(Color.BLUE);

        poster.getConfig().setDebug(true);
        poster.asFile("png", "text_relative_base_line.png");
    }

    @Test
    public void testRelativeBaseLine2() {
        EasyPoster poster = new EasyPoster(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);

        AbstractElement textElement = new TextElement("顶部对齐")
                .setFont(font)
                .setBaseLine(BaseLine.TOP)
                .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of().setMarginLeft(120)));

        poster.addElement(textElement);
        poster.getConfig().setDebug(true);
        poster.asFile("png", "text_relative_base_line2.png");
    }
}
