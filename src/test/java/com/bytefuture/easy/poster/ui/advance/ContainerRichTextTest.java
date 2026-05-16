package com.bytefuture.easy.poster.ui.advance;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.element.advance.ContainerElement;
import com.bytefuture.easy.poster.element.basic.CircleElement;
import com.bytefuture.easy.poster.element.basic.RectangleElement;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.element.v2.text.style.TextOverflow;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.FloatType;
import com.bytefuture.easy.poster.model.TextAlign;
import com.bytefuture.easy.poster.model.TextSpan;
import org.junit.Test;

import java.awt.*;

/**
 * 容器 + v2.TextElement 富文本组合测试
 *
 * @author biaoy
 * @since 2026/05/15
 */
public class ContainerRichTextTest {

    /**
     * 左浮动图片区 + 右侧 TextSpan 多样式富文本描述
     * 演示：FloatType.LEFT + TextSpan 多段样式
     */
    @Test
    public void testFloatImageWithRichText() {
        EasyPoster poster = new EasyPoster(700, 400);

        ContainerElement container = ContainerElement.of(600, 300)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(24))
                .setGap(12)
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(2)
                .setArc(12);

        // 左浮动图片区（圆角矩形模拟图片）
        container.addChild(new RectangleElement(160, 200)
                .setColor(new Color(220, 235, 250))
                .setArc(16)
                .setBorderSize(2)
                .setFloatType(FloatType.LEFT));

        // NONE 块级标题（TextSpan 多样式）
        container.addChild(TextElement.of(
                        TextSpan.of("限时特惠").setBackgroundColor(new Color(255, 80, 80)).setBackgroundPadding(6).setBackgroundRadius(8).setColor(Color.WHITE).setFontSize(14),
                        TextSpan.of(" 全新上市").setColor(new Color(50, 50, 50)).setFontSize(20).setFontStyle(Font.BOLD))
                .setLetterSpacing(2));

        // NONE 块级描述文字
        container.addChild(TextElement.of(
                        TextSpan.of("这是一段富文本描述内容，").setColor(new Color(70, 70, 70)),
                        TextSpan.of("重点词汇").setColor(new Color(220, 80, 80)).setFontStyle(Font.BOLD),
                        TextSpan.of("用不同颜色和样式标记，让海报内容更生动。").setColor(new Color(70, 70, 70)))
                .setFontSize(15)
                .setLetterSpacing(1));

        poster.addElement(container);
        poster.asFile("png", "out_rich_float_image_with_text.png");
    }

    /**
     * 商品卡片：浮动图片 + TextSpan 高亮标签 + 价格
     * 演示：FloatType.LEFT + TextSpan backgroundColor 高亮标签 + 不同 fontSize
     */
    @Test
    public void testProductCardWithSpans() {
        EasyPoster poster = new EasyPoster(800, 360);

        ContainerElement container = ContainerElement.of(700, 280)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(250, 250, 250))
                .setBorderColor(new Color(180, 180, 180))
                .setBorderSize(1)
                .setArc(20);

        // 左浮动商品图
        container.addChild(new RectangleElement(140, 200)
                .setColor(new Color(245, 245, 230))
                .setArc(12)
                .setBorderSize(2)
                .setFloatType(FloatType.LEFT));

        // NONE 块级：品牌名 + 标签
        container.addChild(TextElement.of(
                        TextSpan.of("Apple").setColor(new Color(50, 50, 50)).setFontSize(22).setFontStyle(Font.BOLD),
                        TextSpan.of("  "),
                        TextSpan.of("官方授权").setBackgroundColor(new Color(66, 135, 245)).setBackgroundPadding(4).setBackgroundRadius(6).setColor(Color.WHITE).setFontSize(11),
                        TextSpan.of(" "),
                        TextSpan.of("新品").setBackgroundColor(new Color(255, 180, 60)).setBackgroundPadding(4).setBackgroundRadius(6).setColor(Color.WHITE).setFontSize(11))
                .setLetterSpacing(2));

        // NONE 块级：产品描述
        container.addChild(TextElement.of("MacBook Pro 16英寸 M3 Pro芯片 性能怪兽 超长续航")
                .setColor(new Color(80, 80, 80))
                .setFontSize(14)
                .setLetterSpacing(1));

        // NONE 块级：价格 + 删除线原价
        container.addChild(TextElement.of(
                        TextSpan.of("¥12,999").setColor(new Color(220, 60, 60)).setFontSize(24).setFontStyle(Font.BOLD),
                        TextSpan.of("  "),
                        TextSpan.of("¥16,999").setColor(new Color(160, 160, 160)).setFontSize(14).setStrikeThrough(true))
                .setLetterSpacing(2));

        poster.addElement(container);
        poster.asFile("png", "out_rich_product_card.png");
    }

    /**
     * 名片卡片：浮动头像圆 + 左侧纵向文字 + 右侧基本信息
     * 演示：FloatType.LEFT + CircleElement + v2.TextElement vertical() 纵向文字
     */
    @Test
    public void testProfileCard() {
        EasyPoster poster = new EasyPoster(600, 360);
        poster.getConfig().setFontName("仿宋");

        ContainerElement container = ContainerElement.of(480, 280)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(24))
                .setGap(14)
                .setBackgroundColor(new Color(40, 45, 60))
                .setBorderColor(new Color(80, 90, 120))
                .setBorderSize(2)
                .setArc(16);

        // 左浮动头像（圆形）
        container.addChild(new CircleElement(70)
                .setColor(new Color(100, 170, 230))
                .setBorderSize(3)
                .setFloatType(FloatType.LEFT));

        // NONE 块级：纵向名字
        container.addChild(TextElement.of("张伟\n设计总监")
                .setColor(new Color(240, 240, 240))
                .setFontSize(16)
                .vertical()
                .setLetterSpacing(4));

        // NONE 块级：联系方式（横向文字）
        container.addChild(TextElement.of(
                        TextSpan.of("联系方式").setColor(new Color(180, 190, 220)).setFontSize(12).setFontStyle(Font.BOLD),
                        TextSpan.of("\n"),
                        TextSpan.of("138-0000-1234").setColor(new Color(200, 210, 230)).setFontSize(14),
                        TextSpan.of("\n"),
                        TextSpan.of("zhangwei@design.com").setColor(new Color(160, 170, 200)).setFontSize(13).setUnderline(true))
                .setLetterSpacing(1));

        poster.addElement(container);
        poster.asFile("png", "out_rich_profile_card.png");
    }

    /**
     * 文本在浮动容器中的 maxWidth 自动换行效果
     * 演示：NONE text + maxTextWidth + auto-wrap + maxLines + TextOverflow.ELLIPSIS
     */
    @Test
    public void testTextWrapInFloat() {
        EasyPoster poster = new EasyPoster(700, 480);

        // 场景1：长文本自动换行
        ContainerElement container1 = ContainerElement.of(600, 0)
                .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(30, 30, 0, 0)))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(248, 248, 248))
                .setBorderColor(new Color(100, 100, 100))
                .setBorderSize(1);

        container1.addChild(new RectangleElement(100, 100)
                .setColor(new Color(100, 170, 230))
                .setArc(12)
                .setFloatType(FloatType.LEFT));

        container1.addChild(TextElement.of("这是一段长文本内容，用于演示在浮动布局中文字自动换行的效果。当左浮动元素占据部分空间时，右侧的文字块会自动适应剩余区域进行排版。文字会根据容器宽度自动换行显示。")
                .setColor(new Color(60, 60, 60))
                .setFontSize(15)
                .maxTextWidth(380)
                .setLetterSpacing(1));

        poster.addElement(container1);
        poster.asFile("png", "out_rich_text_wrap.png");

        // 场景2：maxLines + ELLIPSIS 截断
        EasyPoster poster2 = new EasyPoster(700, 300);
        ContainerElement container2 = ContainerElement.of(600, 220)
                .setPosition(RelativePosition.of(Direction.CENTER))
                .setPadding(Margin.of(20))
                .setGap(10)
                .setBackgroundColor(new Color(255, 252, 245))
                .setBorderColor(new Color(200, 180, 140))
                .setBorderSize(1);

        container2.addChild(new RectangleElement(80, 80)
                .setColor(new Color(220, 100, 100))
                .setArc(8)
                .setFloatType(FloatType.LEFT));

        container2.addChild(TextElement.of("这段文字设置了maxLines=2和TextOverflow.ELLIPSIS。超出限制行数的内容会被截断并显示省略号。适用于卡片式海报中需要控制文字显示区域的场景。超出的部分不会被展示。")
                .setColor(new Color(70, 70, 70))
                .setFontSize(14)
                .maxTextWidth(360)
                .setMaxLines(2)
                .setTextOverflow(TextOverflow.ELLIPSIS));

        poster2.addElement(container2);
        poster2.asFile("png", "out_rich_text_ellipsis.png");
    }
}