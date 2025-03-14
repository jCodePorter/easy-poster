package com.augrain.easy.canvas.text;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.enums.BaseLine;
import com.augrain.easy.canvas.geometry.AbsolutePosition;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Positions;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 测试文本基线
 *
 * @author biaoy
 * @since 2025/03/14
 */
public class TestBaseLine {

    @Test
    public void testBaseLine() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        Font font = new Font("华文新魏", Font.PLAIN, 25);
        canvas.addTextElement("中心对齐")
                .setFont(font)
                .setBaseLine(BaseLine.CENTER)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(0, 250), Positions.LEFT_CENTER));

        canvas.addTextElement("顶部对齐")
                .setFont(font)
                .setBaseLine(BaseLine.TOP)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(120, 250), Positions.LEFT_CENTER));

        canvas.addTextElement("底部对齐")
                .setFont(font)
                .setBaseLine(BaseLine.BOTTOM)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(240, 250), Positions.LEFT_CENTER));

        canvas.addTextElement("基线对齐")
                .setFont(font)
                .setBaseLine(BaseLine.BASE_LINE)
                .setPosition(AbsolutePosition.of(CoordinatePoint.of(360, 250), Positions.LEFT_CENTER));

        BufferedImage combine = canvas.render();

        Graphics2D g = combine.createGraphics();
        g.setColor(Color.BLUE);
        g.drawLine(0, 250, 500, 250);
        g.dispose();
        ImageIO.write(combine, "png", new File("text_base_line.png"));
    }
}
