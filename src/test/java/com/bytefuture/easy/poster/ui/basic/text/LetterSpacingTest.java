package com.bytefuture.easy.poster.ui.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.basic.TextElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.Color;

/**
 * 字间距功能 UI 测试
 *
 * @author biaoy
 * @since 2026/04/27
 */
public class LetterSpacingTest {

    @Test
    public void testBlockLetterSpacing() {
        EasyPoster poster = new EasyPoster(400, 300);
        poster.addElement(
                TextElement.of("标题文字效果")
                        .setLetterSpacing(10)
                        .setColor(Color.BLACK)
                        .setFontSize(36)
                        .setPosition(AbsolutePosition.of(Point.of(50, 50)))
        );
        poster.asFile("png", "output/text_letter_spacing_block.png");
    }

    @Test
    public void testSpanLetterSpacing() {
        EasyPoster poster = new EasyPoster(400, 300);
        poster.addElement(
                TextElement.of(
                        TextSpan.of("正常").setColor(Color.BLACK).setFontSize(24),
                        TextSpan.of("稀疏").setLetterSpacing(8).setColor(Color.RED).setFontSize(24)
                ).setPosition(AbsolutePosition.of(Point.of(50, 100)))
        );
        poster.asFile("png", "output/text_letter_spacing_span.png");
    }

    @Test
    public void testLetterSpacingWithWrap() {
        EasyPoster poster = new EasyPoster(400, 300);
        poster.addElement(
                TextElement.of("这是一段带字间距的自动换行文本测试内容")
                        .setLetterSpacing(5)
                        .setColor(Color.BLACK)
                        .setFontSize(20)
                        .setMaxTextWidth(240)
                        .setTextAlign(TextAlign.LEFT)
                        .setPosition(AbsolutePosition.of(Point.of(50, 50)))
        );
        poster.asFile("png", "output/text_letter_spacing_wrap.png");
    }
}