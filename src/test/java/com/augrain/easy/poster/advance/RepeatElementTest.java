package com.augrain.easy.poster.advance;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.AbstractElement;
import com.augrain.easy.poster.element.advance.ComposeElement;
import com.augrain.easy.poster.element.advance.RepeatElement;
import com.augrain.easy.poster.element.basic.ImageElement;
import com.augrain.easy.poster.element.basic.TextElement;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.Margin;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.RelativeDirection;
import com.augrain.easy.poster.model.Scale;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class RepeatElementTest {

    @Test
    public void testTextInTile() throws Exception {
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
        poster.asFile("png", "out_text_in_tile.png");
    }

    @Test
    public void testTextFollowTile() throws Exception {
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

        poster.asFile("png", "out_text_follow_tile.png");
    }


    @Test
    public void testTextFollowWrapTile() throws Exception {
        EasyPoster poster = new EasyPoster(800, 800);

        InputStream inputStream = ComposeElementTest.class.getClassLoader().getResourceAsStream("logo.png");
        BufferedImage inputImg = ImageIO.read(inputStream);

        ComposeElement composeElement = new ComposeElement(new ImageElement(inputImg)
                .scale(Scale.byWidth(50))
                .setPosition(RelativePosition.of(Direction.CENTER)))
                .bottom(new TextElement("叮有鱼科技有限公司专注于无人自助场景")
                        .setColor(Color.red)
                        .setFontSize(18)
                        .setAutoWrapText(200)
                        .setFontName("仿宋")
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of().setMarginTop(10))));

        RepeatElement tileElement = new RepeatElement(composeElement)
                .setPadding(20, 20);
        poster.addElement(tileElement);

        poster.asFile("png", "out_text_follow_wrap_tile.png");
    }
}
