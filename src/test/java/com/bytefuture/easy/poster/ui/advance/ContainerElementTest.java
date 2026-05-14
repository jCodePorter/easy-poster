package com.bytefuture.easy.poster.ui.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.model.ContainerAlign;
import com.bytefuture.easy.poster.element.advance.ComposeElement;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.model.ContainerLayoutMode;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.LocalAbsolutePosition;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

/**
 * @author biaoy
 * @since 2026/05/01
 */
public class ContainerElementTest {

    @Test
    public void testRelativeLayout() {
        EasyPoster poster = new EasyPoster(600, 400);

        ContainerElement container = ContainerElement.of(320, 220)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setBackgroundColor(new Color(245, 245, 245))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(2);

        container.addChild(new RectangleElement(40, 40)
                .setColor(new Color(220, 70, 70))
                .setPosition(RelativePosition.of(Direction.TOP_LEFT)));
        container.addChild(new RectangleElement(40, 40)
                .setColor(new Color(70, 160, 220))
                .setPosition(RelativePosition.of(Direction.TOP_RIGHT)));
        container.addChild(new RectangleElement(40, 40)
                .setColor(new Color(90, 180, 100))
                .setPosition(RelativePosition.of(Direction.CENTER)));
        container.addChild(new RectangleElement(40, 40)
                .setColor(new Color(240, 180, 60))
                .setPosition(RelativePosition.of(Direction.RIGHT_BOTTOM)));

        poster.addElement(container);
        poster.asFile("png", "out_container_relative.png");
    }

    @Test
    public void testLocalAbsolutePosition() {
        EasyPoster poster = new EasyPoster(500, 300);

        ContainerElement container = ContainerElement.of(260, 160)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(16))
                .setBackgroundColor(new Color(250, 250, 250))
                .setBorderColor(new Color(90, 90, 90))
                .setBorderSize(1)
                .addChild(new RectangleElement(30, 30)
                        .setColor(new Color(230, 80, 80))
                        .setPosition(LocalAbsolutePosition.of(Point.of(12, 18))))
                .addChild(new TextElement("Container")
                        .setColor(new Color(40, 40, 40))
                        .setFontSize(20)
                        .setPosition(LocalAbsolutePosition.of(Point.of(70, 28))));

        poster.addElement(container);
        poster.asFile("png", "out_container_absolute.png");
    }

    @Test
    public void testContainerWithComposeElement() {
        EasyPoster poster = new EasyPoster(600, 400);

        ComposeElement badge = ComposeElement.of(new RectangleElement(100, 56)
                        .setColor(new Color(66, 135, 245))
                        .setPosition(RelativePosition.of(Direction.TOP_LEFT)))
                .in(new TextElement("VIP")
                        .setColor(Color.WHITE)
                        .setFontSize(20)
                        .setPosition(RelativePosition.of(Direction.CENTER)));

        ContainerElement container = ContainerElement.of(360, 220)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(24))
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(2)
                .addChild(badge.setPosition(LocalAbsolutePosition.of(Point.of(20, 24))))
                .addChild(new RectangleElement(260, 60)
                        .setColor(new Color(240, 240, 240))
                        .setPosition(LocalAbsolutePosition.of(Point.of(20, 110))));

        poster.addElement(container);
        poster.asFile("png", "out_container_compose.png");
    }

    @Test
    public void testRoundedContainerClip() {
        EasyPoster poster = new EasyPoster(520, 340);

        ContainerElement container = ContainerElement.of(280, 180)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(18))
                .setArc(48)
                .setClipContent(true)
                .setBackgroundColor(new Color(245, 248, 255))
                .setBorderColor(new Color(80, 110, 180))
                .setBorderSize(3)
                .addChild(new RectangleElement(320, 220)
                        .setColor(new Color(255, 120, 120))
                        .setPosition(RelativePosition.of(Direction.CENTER)))
                .addChild(new TextElement("Rounded Container")
                        .setColor(new Color(30, 50, 90))
                        .setFontSize(24)
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of().setMarginTop(16))));

        poster.addElement(container);
        poster.asFile("png", "out_container_rounded_clip.png");
    }

    @Test
    public void testVerticalFlowLayout() {
        EasyPoster poster = new EasyPoster(420, 420);

        ContainerElement container = ContainerElement.of(220, 0)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 30, 0, 0)))
                .setPadding(Margin.of(16))
                .setLayoutMode(ContainerLayoutMode.VERTICAL)
                .setGap(12)
                .setAlignItems(ContainerAlign.CENTER)
                .setBackgroundColor(new Color(248, 248, 248))
                .setBorderColor(new Color(100, 100, 100))
                .setBorderSize(1)
                .addChild(new RectangleElement(60, 30).setColor(new Color(220, 90, 90)))
                .addChild(new RectangleElement(100, 36).setColor(new Color(90, 160, 220)))
                .addChild(new RectangleElement(80, 24).setColor(new Color(90, 180, 100)));

        poster.addElement(container);
        poster.asFile("png", "out_container_vertical_flow.png");
    }

    @Test
    public void testHorizontalFlowLayout() {
        EasyPoster poster = new EasyPoster(560, 240);

        ContainerElement container = ContainerElement.of(0, 140)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(30, 40, 0, 0)))
                .setPadding(Margin.of(18))
                .setLayoutMode(ContainerLayoutMode.HORIZONTAL)
                .setGap(14)
                .setJustifyContent(ContainerAlign.CENTER)
                .setAlignItems(ContainerAlign.END)
                .setArc(28)
                .setBackgroundColor(new Color(250, 246, 238))
                .setBorderColor(new Color(150, 130, 100))
                .setBorderSize(2)
                .addChild(new RectangleElement(40, 30).setColor(new Color(230, 120, 120)))
                .addChild(new RectangleElement(70, 50).setColor(new Color(100, 170, 230)))
                .addChild(new RectangleElement(50, 40).setColor(new Color(120, 190, 120)));

        poster.addElement(container);
        poster.asFile("png", "out_container_horizontal_flow.png");
    }

    @Test
    public void testFlowLayoutWithChildMargin() {
        EasyPoster poster = new EasyPoster(520, 320);

        ContainerElement container = ContainerElement.of(320, 180)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(40, 40, 0, 0)))
                .setPadding(Margin.of(12))
                .setLayoutMode(ContainerLayoutMode.HORIZONTAL)
                .setGap(10)
                .setAlignItems(ContainerAlign.CENTER)
                .setBackgroundColor(new Color(246, 246, 246))
                .setBorderColor(new Color(120, 120, 120))
                .setBorderSize(1);

        container.addChild(new RectangleElement(40, 40).setColor(new Color(230, 100, 100)), Margin.of(6, 4, 12, 8));
        container.addChild(new RectangleElement(60, 50).setColor(new Color(90, 150, 220)), Margin.of(0, 12, 8, 0));
        container.addChild(new RectangleElement(36, 36).setColor(new Color(100, 180, 120)), Margin.of(10, 0, 0, 10));

        poster.addElement(container);
        poster.asFile("png", "out_container_flow_margin.png");
    }
}
