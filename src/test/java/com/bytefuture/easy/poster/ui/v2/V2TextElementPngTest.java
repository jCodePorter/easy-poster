package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.BaseLine;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;

/**
 * V2 text visual regression style test.
 * Run this test and inspect the generated png file.
 */
public class V2TextElementPngTest {

    @Test
    public void renderV2TextConfigPreviewToPng() {
        EasyPoster poster = new EasyPoster(1200, 900);

        addPanel(poster, 40, 40, 520, 260, new Color(248, 250, 255));
        addPanel(poster, 640, 40, 520, 260, new Color(250, 248, 243));
        addPanel(poster, 40, 340, 520, 240, new Color(244, 250, 246));
        addPanel(poster, 640, 340, 520, 240, new Color(250, 245, 248));

        poster.addElement(TextElement.builder("V2 基础样式")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(70, 80), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("阴影 + 描边 + 字距 + 背景")
                .font("Microsoft YaHei", Font.BOLD, 34)
                .color(new Color(32, 67, 136))
                .shadow(new Color(0, 0, 0, 80), 3, 3)
                .stroke(Color.WHITE, 1.5f)
                .letterSpacing(4)
                .underline(true)
                .textBackground(new Color(220, 234, 255), 18)
                .textBackgroundArc(18)
                .position(AbsolutePosition.of(Point.of(70, 150), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("V2 自动换行")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(670, 80), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder(
                        "这一段用于观察自动换行、行高、背景内边距和最大宽度的实际表现。"
                                + "如果参数生效，文本会稳定落在浅色卡片内，并且每行间距保持一致。")
                .font("Microsoft YaHei", Font.PLAIN, 22)
                .color(new Color(74, 63, 53))
                .autoWordWrap(420)
                .lineHeight(36)
                .textAlign(TextAlign.LEFT)
                .textBackground(new Color(255, 241, 219), 16)
                .textBackgroundArc(14)
                .position(AbsolutePosition.of(Point.of(670, 145), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("V2 自动缩放")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(70, 380), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("这一行初始字号很大，但会被压缩到 300px 宽度以内")
                .font("Microsoft YaHei", Font.BOLD, 42)
                .color(new Color(27, 110, 72))
                .autoFitText(300, 16)
                .textAlign(TextAlign.CENTER)
                .textBackground(new Color(220, 245, 231), 14)
                .textBackgroundArc(12)
                .position(AbsolutePosition.of(Point.of(300, 475), Direction.TOP_CENTER))
                .build());

        poster.addElement(TextElement.builder("锚点基线 = CENTER")
                .font("Microsoft YaHei", Font.PLAIN, 18)
                .color(new Color(88, 102, 94))
                .position(AbsolutePosition.of(Point.of(70, 535), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.builder("中心对齐")
                .font("Microsoft YaHei", Font.BOLD, 28)
                .color(new Color(27, 110, 72))
                .baseLine(BaseLine.CENTER)
                .position(AbsolutePosition.of(Point.of(300, 535), Direction.TOP_CENTER))
                .build());

        poster.addElement(TextElement.builder("V2 富文本")
                .font("Microsoft YaHei", Font.BOLD, 24)
                .color(new Color(33, 37, 41))
                .position(AbsolutePosition.of(Point.of(670, 380), Direction.TOP_LEFT))
                .build());

        poster.addElement(TextElement.rich(
                        TextSpan.of("加粗").setFontStyle(Font.BOLD).setColor(new Color(187, 38, 73)),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("斜体").setFontStyle(Font.ITALIC).setColor(new Color(74, 90, 224)),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("下划线").setUnderline(true).setColor(new Color(35, 124, 86)),
                        TextSpan.of(" / ").setColor(new Color(120, 120, 120)),
                        TextSpan.of("删除线").setStrikeThrough(true).setColor(new Color(125, 78, 168)))
                .setPosition(AbsolutePosition.of(Point.of(670, 470), Direction.TOP_LEFT))
                .setColor(new Color(60, 60, 60)));

        poster.addElement(TextElement.builder("输出文件: out_v2_text_config_preview.png")
                .font("Microsoft YaHei", Font.PLAIN, 16)
                .color(new Color(120, 120, 120))
                .position(AbsolutePosition.of(Point.of(40, 845), Direction.TOP_LEFT))
                .build());

        poster.asFile("png", "out_v2_text_config_preview.png");
    }

    private void addPanel(EasyPoster poster, int x, int y, int width, int height, Color backgroundColor) {
        poster.addRectangleElement(width, height)
                .setArc(24)
                .setColor(backgroundColor)
                .setPosition(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT));

        poster.addRectangleElement(width, height)
                .setArc(24)
                .setBorderSize(2)
                .setColor(new Color(220, 225, 232))
                .setPosition(AbsolutePosition.of(Point.of(x, y), Direction.TOP_LEFT));
    }
}
