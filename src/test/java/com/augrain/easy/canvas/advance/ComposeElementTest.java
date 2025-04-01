package com.augrain.easy.canvas.advance;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.advance.ComposeElement;
import com.augrain.easy.canvas.element.advance.RepeatElement;
import com.augrain.easy.canvas.element.basic.ImageElement;
import com.augrain.easy.canvas.element.basic.TextElement;
import com.augrain.easy.canvas.geometry.Margin;
import com.augrain.easy.canvas.geometry.Positions;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.model.BaseLine;
import com.augrain.easy.canvas.model.RelativeDirection;
import com.augrain.easy.canvas.model.Scale;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * @author biaoy
 * @since 2025/03/29
 */
public class ComposeElementTest {

    @Test
    public void testBottom() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Positions.CENTER));

        Margin margin = Margin.of().setMarginTop(10);
        TextElement textElement = new TextElement("叮叮智能")
                .setFontColor(Color.red)
                .setFontSize(18)
                .setPosition(RelativePosition.of(Positions.TOP_LEFT, margin));

        Margin margin2 = Margin.of().setMarginTop(40);
        TextElement textElement2 = new TextElement("郑州叮有鱼科技")
                .setFontColor(Color.red)
                .setFontSize(18)
                .setPosition(RelativePosition.of(Positions.TOP_LEFT, margin2));

        ComposeElement composeElement = new ComposeElement(imageElement)
                .add(textElement, RelativeDirection.BOTTOM, true)
                .add(textElement2, RelativeDirection.BOTTOM, false);

        canvas.addElement(composeElement);
        canvas.asFile("png", "compose_bottom.png");
    }

    @Test
    public void testTop() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Positions.CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Positions position : Positions.values()) {
            Margin margin = Margin.of(10);
            TextElement textElement = new TextElement("叮叮智能")
                    .setFontColor(Color.red)
                    .setFontSize(18)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.add(textElement, RelativeDirection.TOP, false);
        }
        canvas.addElement(composeElement);
        canvas.asFile("png", "compose_top.png");
    }

    @Test
    public void testLeft() throws Exception {
        EasyCanvas canvas = new EasyCanvas(1200, 800);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(500))
                .setPosition(RelativePosition.of(Positions.RIGHT_CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Positions position : Positions.values()) {
            Margin margin = Margin.of(0);
            TextElement textElement = new TextElement("叮有鱼科技")
                    .setFontColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.add(textElement, RelativeDirection.LEFT, true);
        }
        canvas.addElement(composeElement);

        canvas.asFile("png", "compose_left.png");
    }

    @Test
    public void testRight() throws Exception {
        EasyCanvas canvas = new EasyCanvas(1200, 800);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(500))
                .setPosition(RelativePosition.of(Positions.LEFT_CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Positions position : Positions.values()) {
            Margin margin = Margin.of(0);
            TextElement textElement = new TextElement("叮有鱼科技")
                    .setFontColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.add(textElement, RelativeDirection.RIGHT, false);
        }
        canvas.addElement(composeElement);
        canvas.asFile("png", "compose_right.png");
    }

    @Test
    public void testIn() throws Exception {
        EasyCanvas canvas = new EasyCanvas(1200, 1200);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(700))
                .setPosition(RelativePosition.of(Positions.CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Positions position : Positions.values()) {
            Margin margin = Margin.of(0);
            TextElement textElement = new TextElement("叮有鱼科技")
                    .setFontColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.add(textElement, RelativeDirection.IN, false);
        }
        canvas.addElement(composeElement);

        canvas.asFile("png", "compose_in.png");
    }

    @Test
    public void testComposeTile() throws Exception {
        EasyCanvas canvas = new EasyCanvas(500, 500);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);

        ComposeElement composeElement = new ComposeElement(new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Positions.CENTER)))
                .bottom(new TextElement("叮叮智能")
                        .setFontColor(Color.red)
                        .setFontSize(18)
                        .setFontName("仿宋")
                        .setPosition(RelativePosition.of(Positions.TOP_CENTER, Margin.of().setMarginTop(10))))
                .bottom(new TextElement("郑州叮有鱼科技")
                        .setFontColor(Color.red)
                        .setFontSize(18)
                        .setFontName("楷体")
                        .setPosition(RelativePosition.of(Positions.TOP_CENTER, Margin.of().setMarginTop(40))));

        RepeatElement tileElement = new RepeatElement(composeElement)
                .setPadding(20, 20);
        canvas.addElement(tileElement);

        canvas.asFile("png", "compose_tile.png");
    }

    @Test
    public void testInTile() throws Exception {
        EasyCanvas canvas = new EasyCanvas(3000, 3000);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        AbstractElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(500))
                .setPosition(RelativePosition.of(Positions.CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Positions position : Positions.values()) {
            Margin margin = Margin.of(30);
            AbstractElement textElement = new TextElement("叮有鱼科技")
                    .setFontColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.add(textElement, RelativeDirection.IN, false);
        }

        RepeatElement tileElement = new RepeatElement(composeElement)
                .setPadding(100, 100);
        canvas.addElement(tileElement);
        canvas.asFile("png", "compose_in_tile.png");
    }

    @Test
    public void testFollow() {
        EasyCanvas canvas = new EasyCanvas(1000, 200);

        BaseLine baseLine = BaseLine.CENTER;
        ComposeElement composeElement = new ComposeElement(new TextElement("您已经连续工作")
                .setPosition(RelativePosition.of(Positions.LEFT_CENTER))
                .setFontSize(18)
                .setFontName("楷体")
                .setBaseLine(baseLine)
        )
                .follow(new TextElement("6")
                        .setFontColor(Color.red)
                        .setFontSize(50)
                        .setFontName("仿宋")
                        .setBaseLine(baseLine)
                        .setPosition(RelativePosition.of(Positions.LEFT_CENTER)), RelativeDirection.RIGHT, true)
                .follow(new TextElement("天了，需要好好休息休息了")
                        .setFontSize(35)
                        .setFontName("楷体")
                        .setBaseLine(baseLine)
                        .setPosition(RelativePosition.of(Positions.LEFT_CENTER)), RelativeDirection.RIGHT, true);
        canvas.addElement(composeElement);
        canvas.asFile("png", "compose_follow.png");
    }
}
