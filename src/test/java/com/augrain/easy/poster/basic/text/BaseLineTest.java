package com.augrain.easy.poster.basic.text;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.geometry.AbsolutePosition;
import com.augrain.easy.poster.geometry.Direction;
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

        poster.getConfig().setDebug(true);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        poster.addTextElement("中心对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(AbsolutePosition.of(Point.of(0, 250), Direction.LEFT_CENTER));

        poster.addTextElement("顶部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(Point.of(120, 250), Direction.LEFT_CENTER));

        poster.addTextElement("底部对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(AbsolutePosition.of(Point.of(240, 250), Direction.LEFT_CENTER));

        poster.addTextElement("基线对齐")
                .setFont(font)
                .setLineHeight(40)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(AbsolutePosition.of(Point.of(360, 250), Direction.LEFT_CENTER));

        poster.addLineElement(Point.of(0, 250), Point.of(500, 250))
                .setColor(Color.BLUE);

        poster.asFile("png", "out_text_absolute_base_line.png");
    }

    /**
     * 绝对定位，并自动换行
     */
    @Test
    public void testBaseLineAutoNewline() {
        EasyPoster poster = new EasyPoster(500, 500);

        poster.getConfig().setDebug(true);
        poster.getConfig().setFont(new Font("华文新魏", Font.PLAIN, 25));

        poster.addTextElement("测试顶部对齐并自动换行，这是一个全新的测试")
                .setLineHeight(40)
                .setAutoWrapText(320)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(Point.of(150, 250), Direction.LEFT_CENTER));

        poster.addLineElement(Point.of(0, 250), Point.of(500, 250))
                .setColor(Color.BLUE);

        poster.asFile("png", "out_text_absolute_base_line_auto_newline.png");
    }

}
