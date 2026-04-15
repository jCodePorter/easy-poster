package com.bytefuture.easy.poster.element.v2;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.geometry.Direction;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.RelativePosition;
import com.bytefuture.easy.poster.model.*;

import java.awt.*;

/**
 * V2 架构使用示例。
 * 展示全新设计的纯净架构如何使用。
 *
 * @author biaoy
 * @since 2025/04/15
 */
public class V2Examples {

    public static void main(String[] args) {
        example1_basic();
        example2_autoWrap();
        example3_autoFit();
        example4_richText();
        example5_complex();
        example6_poster();
    }

    /**
     * 示例1：基础文本
     */
    private static void example1_basic() {
        System.out.println("=== 示例1：基础文本 ===");

        // 方式1：工厂方法
        TextElement text1 = TextElement.of("Hello V2 Architecture!")
                .setPosition(RelativePosition.of(Direction.CENTER));

        // 方式2：Builder模式（推荐）
        TextElement text2 = TextElement.builder("Styled Text")
                .font("Microsoft YaHei", Font.BOLD, 32)
                .color(Color.RED)
                .textAlign(TextAlign.CENTER)
                .baseLine(BaseLine.CENTER)
                .shadow(Color.GRAY, 3, 3)
                .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 100, 0, 0)))
                .build();

        System.out.println("✓ 基础文本创建成功\n");
    }

    /**
     * 示例2：自动换行
     */
    private static void example2_autoWrap() {
        System.out.println("=== 示例2：自动换行 ===");

        String longText = "这是一段很长的文本内容，需要自动换行处理。" +
                "V2架构完全重新设计，不依赖V1的任何代码。" +
                "配置、布局、渲染三者完全分离，职责清晰。";

        TextElement text = TextElement.builder(longText)
                .font("Microsoft YaHei", Font.PLAIN, 18)
                .color(Color.BLACK)
                .autoWordWrap(400)  // 最大宽度400px
                .lineHeight(28)
                .textAlign(TextAlign.LEFT)
                .textBackground(new Color(255, 250, 240), 15)
                .textBackgroundArc(8)
                .textPadding(10, 15)
                .build();

        System.out.println("✓ 自动换行文本创建成功\n");
    }

    /**
     * 示例3：自适应字体
     */
    private static void example3_autoFit() {
        System.out.println("=== 示例3：自适应字体 ===");

        TextElement text = TextElement.builder("动态调整字号以适应宽度")
                .font("Microsoft YaHei", Font.BOLD, 48)
                .color(new Color(255, 0, 0))
                .autoFitText(300, 12)  // 目标宽度300px，最小字号12
                .textAlign(TextAlign.CENTER)
                .underline(true)
                .build();

        System.out.println("✓ 自适应字体创建成功\n");
    }

    /**
     * 示例4：富文本
     */
    private static void example4_richText() {
        System.out.println("=== 示例4：富文本 ===");

        TextElement richText = TextElement.rich(
                TextSpan.of("红色粗体").setFontStyle(Font.BOLD).setColor(Color.RED),
                TextSpan.of(" + "),
                TextSpan.of("蓝色斜体").setFontStyle(Font.ITALIC).setColor(Color.BLUE),
                TextSpan.of(" + "),
                TextSpan.of("绿色普通").setFontStyle(Font.PLAIN).setColor(Color.GREEN)
        ).setPosition(RelativePosition.of(Direction.CENTER));

        System.out.println("✓ 富文本创建成功\n");
    }

    /**
     * 示例5：复杂样式组合
     */
    private static void example5_complex() {
        System.out.println("=== 示例5：复杂样式组合 ===");

        TextElement text = TextElement.builder("复杂样式示例")
                // 字体
                .font("Microsoft YaHei", Font.BOLD, 36)
                // 颜色
                .color(new Color(50, 50, 50))
                // 对齐
                .textAlign(TextAlign.CENTER)
                .baseLine(BaseLine.CENTER)
                // 装饰
                .underline(true)
                .strikeThrough(false)
                .shadow(new Color(0, 0, 0, 100), 2, 2)
                .stroke(Color.WHITE, 1.5f)
                // 字间距
                .letterSpacing(5)
                // 背景
                .textBackground(new Color(255, 255, 255, 200), 20)
                .textBackgroundArc(10)
                // 位置
                .position(RelativePosition.of(Direction.CENTER))
                // 透明度
                .alpha(0.9f)
                .build();

        System.out.println("✓ 复杂样式创建成功\n");
    }

    /**
     * 示例6：在海报中使用
     */
    private static void example6_poster() {
        System.out.println("=== 示例6：海报集成 ===");

        EasyPoster poster = new EasyPoster(800, 600);

        // 标题
        TextElement title = TextElement.builder("海报标题")
                .font("Microsoft YaHei", Font.BOLD, 48)
                .color(Color.BLACK)
                .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 80, 0, 0)))
                .shadow(Color.GRAY, 3, 3)
                .build();

        // 正文
        TextElement content = TextElement.builder(
                "这是海报的正文内容，支持自动换行。\n" +
                        "V2架构完全独立设计，不依赖V1。"
        ).font("Microsoft YaHei", Font.PLAIN, 20)
                .color(new Color(80, 80, 80))
                .autoWordWrap(600)
                .lineHeight(32)
                .position(RelativePosition.of(Direction.CENTER))
                .build();

        // 底部
        TextElement footer = TextElement.builder("© 2025 ByteFuture")
                .fontName("Arial")
                .fontSize(14)
                .color(Color.GRAY)
                .position(RelativePosition.of(Direction.BOTTOM_CENTER, Margin.of(0, 0, 50, 0)))
                .build();

        // 添加元素
        poster.addElement(title);
        poster.addElement(content);
        poster.addElement(footer);

        System.out.println("✓ 海报集成成功\n");
        // EasyPoster V1 没有 getElements() 方法，注释掉
        // System.out.println("海报元素数量: " + poster.getElements().size());
    }
}
