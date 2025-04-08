package com.augrain.easy.canvas.basic.text;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.basic.TextElement;
import com.augrain.easy.canvas.geometry.*;
import com.augrain.easy.canvas.model.BaseLine;
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
        EasyCanvas canvas = new EasyCanvas(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        canvas.addTextElement("中心对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(0, 00), PositionDirection.LEFT_CENTER));

        canvas.addTextElement("顶部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(120, 0), PositionDirection.LEFT_CENTER));

        canvas.addTextElement("底部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(240, 0), PositionDirection.LEFT_CENTER));

        canvas.addTextElement("基线对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(360, 0), PositionDirection.LEFT_CENTER));

        canvas.addLineElement(CoordinatePoint.of(0, 0), CoordinatePoint.of(500, 0))
                .setColor(Color.BLUE);

        canvas.asFile("png", "text_absolute_base_line.png");
    }

    @Test
    public void testRelativeBaseLine() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        canvas.addTextElement("中心对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(RelativePosition.of(PositionDirection.TOP_LEFT, Margin.of().setMarginLeft(0)));

        canvas.addTextElement("顶部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.TOP)
                .setPosition(RelativePosition.of(PositionDirection.TOP_LEFT, Margin.of().setMarginLeft(120)));

        canvas.addTextElement("底部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(RelativePosition.of(PositionDirection.TOP_LEFT, Margin.of().setMarginLeft(240)));

        canvas.addTextElement("基线对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(RelativePosition.of(PositionDirection.TOP_LEFT, Margin.of().setMarginLeft(360)));

        canvas.addLineElement(CoordinatePoint.of(0, 250), CoordinatePoint.of(500, 250))
                .setPosition(RelativePosition.of(PositionDirection.CENTER))
                .setColor(Color.BLUE);

        canvas.getConfig().setDebug(true);
        canvas.asFile("png", "text_relative_base_line.png");
    }

    @Test
    public void testRelativeBaseLine2() {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);

        AbstractElement textElement = new TextElement("顶部对齐")
                .setFont(font)
                .setBaseLine(BaseLine.TOP)
                .setPosition(RelativePosition.of(PositionDirection.TOP_CENTER, Margin.of().setMarginLeft(120)));

        canvas.addElement(textElement);
        canvas.getConfig().setDebug(true);
        canvas.asFile("png", "text_relative_base_line2.png");
    }
}
