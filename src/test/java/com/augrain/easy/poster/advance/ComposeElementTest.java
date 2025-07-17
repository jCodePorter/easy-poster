package com.augrain.easy.poster.advance;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.AbstractElement;
import com.augrain.easy.poster.element.advance.ComposeElement;
import com.augrain.easy.poster.element.advance.RepeatElement;
import com.augrain.easy.poster.element.basic.ImageElement;
import com.augrain.easy.poster.element.basic.TextElement;
import com.augrain.easy.poster.geometry.Margin;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.BaseLine;
import com.augrain.easy.poster.model.RelativeDirection;
import com.augrain.easy.poster.model.Scale;
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
        EasyPoster poster = new EasyPoster(500, 500);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Direction.CENTER));

        Margin margin = Margin.of().setMarginTop(10);
        TextElement textElement = new TextElement("叮叮智能")
                .setColor(Color.red)
                .setFontSize(18)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, margin));

        Margin margin2 = Margin.of().setMarginTop(40);
        TextElement textElement2 = new TextElement("郑州叮有鱼科技")
                .setColor(Color.red)
                .setFontSize(18)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, margin2));

        ComposeElement composeElement = new ComposeElement(imageElement)
                .next(textElement, RelativeDirection.BOTTOM, true)
                .next(textElement2, RelativeDirection.BOTTOM, false);

        poster.addElement(composeElement);
        poster.asFile("png", "out_compose_bottom.png");
    }

    @Test
    public void testTop() throws Exception {
        EasyPoster poster = new EasyPoster(500, 500);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Direction.CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Direction position : Direction.values()) {
            Margin margin = Margin.of(10);
            TextElement textElement = new TextElement("叮叮智能")
                    .setColor(Color.red)
                    .setFontSize(18)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.next(textElement, RelativeDirection.TOP, false);
        }
        poster.addElement(composeElement);
        poster.asFile("png", "out_compose_top.png");
    }

    @Test
    public void testLeft() throws Exception {
        EasyPoster poster = new EasyPoster(1200, 800);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(500))
                .setPosition(RelativePosition.of(Direction.RIGHT_CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Direction position : Direction.values()) {
            Margin margin = Margin.of(0);
            TextElement textElement = new TextElement("叮有鱼科技")
                    .setColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.next(textElement, RelativeDirection.LEFT, true);
        }
        poster.addElement(composeElement);

        poster.asFile("png", "out_compose_left.png");
    }

    @Test
    public void testRight() throws Exception {
        EasyPoster poster = new EasyPoster(1200, 800);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(500))
                .setPosition(RelativePosition.of(Direction.LEFT_CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Direction position : Direction.values()) {
            Margin margin = Margin.of(0);
            TextElement textElement = new TextElement("叮有鱼科技")
                    .setColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.next(textElement, RelativeDirection.RIGHT, false);
        }
        poster.addElement(composeElement);
        poster.asFile("png", "out_compose_right.png");
    }

    @Test
    public void testIn() throws Exception {
        EasyPoster poster = new EasyPoster(1200, 1200);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        ImageElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(700))
                .setPosition(RelativePosition.of(Direction.CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Direction position : Direction.values()) {
            Margin margin = Margin.of(0);
            TextElement textElement = new TextElement("叮有鱼科技")
                    .setColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.next(textElement, RelativeDirection.IN, false);
        }
        poster.addElement(composeElement);

        poster.asFile("png", "compose_in.png");
        poster.asFile("png", "out_compose_in.png");
    }

    @Test
    public void testComposeTile() throws Exception {
        EasyPoster poster = new EasyPoster(500, 500);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);

        ComposeElement composeElement = new ComposeElement(new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Direction.CENTER)))
                .bottom(new TextElement("叮叮智能")
                        .setColor(Color.red)
                        .setFontSize(18)
                        .setFontName("仿宋")
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of().setMarginTop(10))))
                .bottom(new TextElement("郑州叮有鱼科技")
                        .setColor(Color.red)
                        .setFontSize(18)
                        .setFontName("楷体")
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of().setMarginTop(40))));

        RepeatElement tileElement = new RepeatElement(composeElement)
                .setPadding(20, 20);
        poster.addElement(tileElement);

        poster.asFile("png", "out_compose_tile.png");
    }

    @Test
    public void testInTile() throws Exception {
        EasyPoster poster = new EasyPoster(3000, 3000);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);
        AbstractElement imageElement = new ImageElement(inputImg)
                .scale(Scale.byWidth(500))
                .setPosition(RelativePosition.of(Direction.CENTER));

        ComposeElement composeElement = new ComposeElement(imageElement);
        for (Direction position : Direction.values()) {
            Margin margin = Margin.of(30);
            AbstractElement textElement = new TextElement("叮有鱼科技")
                    .setColor(Color.red)
                    .setFontSize(25)
                    .setPosition(RelativePosition.of(position, margin));
            composeElement.next(textElement, RelativeDirection.IN, false);
        }

        RepeatElement tileElement = new RepeatElement(composeElement)
                .setPadding(100, 100);
        poster.addElement(tileElement);
        poster.asFile("png", "out_compose_in_tile.png");
    }

    @Test
    public void testFollow() {
        EasyPoster poster = new EasyPoster(1000, 200);
        BaseLine baseLine = BaseLine.CENTER;

        ComposeElement follow = TextElement.of("天了，需要好好休息休息了")
                .setFontSize(35)
                .setFontName("楷体")
                .setBaseLine(baseLine)
                .setPosition(RelativePosition.of(Direction.LEFT_CENTER))
                .follow(TextElement.of("这是一个小标题")
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER)), RelativeDirection.BOTTOM, true);

        poster.addElement(TextElement.of("您已经连续工作")
                .setPosition(RelativePosition.of(Direction.LEFT_CENTER))
                .setFontSize(18)
                .setFontName("楷体")
                .setBaseLine(baseLine)
                .follow(TextElement.of("6")
                        .setColor(Color.red)
                        .setFontSize(50)
                        .setFontName("仿宋")
                        .setBaseLine(baseLine)
                        .setPosition(RelativePosition.of(Direction.LEFT_CENTER)), RelativeDirection.RIGHT, true)
                .follow(follow, RelativeDirection.RIGHT, true));

        poster.asFile("png", "out_compose_follow.png");
    }
}
