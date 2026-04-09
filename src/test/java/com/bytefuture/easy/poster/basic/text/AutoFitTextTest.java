package com.bytefuture.easy.poster.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import org.junit.Test;

import java.awt.*;

/**
 * 测试自适应调整文本大小
 *
 * @author biaoy
 * @since 2025/04/09
 */
public class AutoFitTextTest {

    @Test
    public void testAutoFitTextWithinLimit() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 文本可以适应目标宽度，字体大小会自动缩小
        poster.addTextElement("这是一段可以适应宽度的文本")
                .setFontSize(30)
                .setColor(Color.blue)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(200, 8);

        poster.asFile("png", "out_text_auto_fit_within_limit.png");
    }

    @Test
    public void testAutoFitTextAtMinLimit() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 文本很长，即使达到最小字体大小也无法单行显示，会启用自动换行
        poster.addTextElement("这是一段非常非常非常非常非常非常非常非常非常非常非常非常非常非常长的文本，即使最小字体也无法单行显示")
                .setFontSize(30)
                .setColor(Color.red)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(200, 20);

        poster.asFile("png", "out_text_auto_fit_min_limit.png");
    }

    @Test
    public void testAutoFitTextNoNeedToShrink() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 文本宽度小于目标宽度，不需要调整字体大小
        poster.addTextElement("短文本")
                .setFontSize(20)
                .setColor(Color.green)
                .setPosition(AbsolutePosition.of(Point.of(30, 50), Direction.TOP_LEFT))
                .setAutoFitText(200, 8);

        poster.asFile("png", "out_text_auto_fit_no_shrink.png");
    }

    @Test
    public void testAutoFitTextThenChangeFontStyle() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 先设置自适应文本
        poster.addTextElement("这是一段测试文本，用于验证修改字体样式后是否重新计算")
                .setFontSize(30)
                .setColor(Color.blue)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(300, 10)
                // 在设置自适应文本后，再修改字体样式
                .setFontStyle(Font.BOLD);

        poster.asFile("png", "out_text_autofit_then_change_style.png");
    }

    @Test
    public void testAutoFitTextThenChangeFontName() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 先设置自适应文本
        poster.addTextElement("这是一段测试文本，用于验证修改字体名称后是否重新计算")
                .setFontSize(30)
                .setColor(Color.red)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(300, 10)
                // 在设置自适应文本后，再修改字体名称
                .setFontName("宋体");

        poster.asFile("png", "out_text_autofit_then_change_fontname.png");
    }

    @Test
    public void testAutoFitTextWithBoldStyle() {
        EasyPoster poster = new EasyPoster(500, 200);

        // 先设置字体样式为加粗，再设置自适应文本
        poster.addTextElement("这是一段加粗的自适应文本")
                .setFontSize(30)
                .setFontStyle(Font.BOLD)
                .setColor(Color.green)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(250, 10);

        poster.asFile("png", "out_text_autofit_with_bold.png");
    }

    @Test
    public void testAutoFitTextOrderIndependence() {
        // 测试1：先设置字体样式，再设置自适应
        EasyPoster poster1 = new EasyPoster(500, 200);
        poster1.addTextElement("测试顺序无关性 - 先样式后自适应")
                .setFontSize(30)
                .setFontStyle(Font.BOLD | Font.ITALIC)
                .setColor(Color.BLUE)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(300, 10);
        poster1.asFile("png", "out_text_order_style_first.png");

        // 测试2：先设置自适应，再设置字体样式
        EasyPoster poster2 = new EasyPoster(500, 200);
        poster2.addTextElement("测试顺序无关性 - 先自适应后样式")
                .setFontSize(30)
                .setColor(Color.RED)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setAutoFitText(300, 10)
                .setFontStyle(Font.BOLD | Font.ITALIC);
        poster2.asFile("png", "out_text_order_autofit_first.png");
    }
}