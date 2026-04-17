# 文本渲染系统 V2 - 全新架构

## 🎯 设计理念

**完全独立、纯净架构、零兼容负担**

V2 架构从零开始重新设计，完全不依赖 V1 的任何代码，实现了真正的职责分离和关注点隔离。

---

## 📦 核心组件

### 1. TextElementConfig - 纯配置载体
```
位置: element/v2/TextElementConfig.java
职责: 仅存储文本样式配置
特性:
  ✓ 完全不可变（Immutable）
  ✓ 线程安全
  ✓ Builder 模式构建
  ✓ 不继承任何类
  ✓ 不包含业务逻辑
```

### 2. TextLayoutEngine - 完整布局引擎
```
位置: element/v2/TextLayoutEngine.java
职责: 接收配置 → 计算布局 → 返回结果
特性:
  ✓ 完全独立的布局计算
  ✓ 不循环调用 Element
  ✓ 内置缓存机制
  ✓ 支持纯文本和富文本
  ✓ 自动字体适配
```

### 3. TextRenderer - 独立渲染器
```
位置: element/v2/TextRenderer.java
职责: 接收布局结果 → 执行绘制
特性:
  ✓ 完全不依赖 V1 Painter
  ✓ 独立处理背景、装饰、文本
  ✓ 支持阴影、描边、字间距
  ✓ 自动状态管理和恢复
```

### 4. TextElement - 轻量级入口
```
位置: element/v2/TextElement.java
职责: 实现 IElement 接口，作为系统入口
特性:
  ✓ 仅持有配置和位置
  ✓ 委托 Engine 和 Renderer
  ✓ 不包含布局/渲染逻辑
  ✓ Builder 模式简化构建
```

---

## 🏗️ 架构对比

### V1 架构（已废弃）
```
┌─────────────────────────────────┐
│   EnhanceTextElement (V1)       │
│   - 配置存储                     │
│   - 布局计算 ← 职责混乱          │
│   - 字体计算 ← 职责混乱          │
│   - 6个静态单例 ← 难以测试       │
└─────────────────────────────────┘
         ↓ 循环依赖
┌─────────────────────────────────┐
│   TextLayoutEngine              │
│   - 仅做缓存代理                 │
│   - 委托回 Element               │
└─────────────────────────────────┘
```

**问题：**
- ❌ 循环依赖
- ❌ 上帝类（God Class）
- ❌ 静态单例滥用
- ❌ 职责混乱
- ❌ 难以测试

### V2 架构（全新设计）
```
┌─────────────────────────────────┐
│   TextElement (入口)             │
│   - 持有 Config + Position       │
│   - 实现 IElement 接口           │
└─────────────────────────────────┘
         ↓
    ┌────┴─────┐
    ↓          ↓
┌───────┐  ┌────────┐
│Engine │  │Renderer│
│       │  │        │
│配置 →  │  │布局 →  │
│布局   │  │绘制    │
└───────┘  └────────┘
    ↓
┌─────────────────────────────────┐
│   TextElementConfig (配置)       │
│   - 不可变                       │
│   - Builder 构建                 │
└─────────────────────────────────┘
```

**优势：**
- ✅ 单向依赖，无循环
- ✅ 职责清晰分离
- ✅ 无静态单例
- ✅ 完全可测试
- ✅ 易于扩展

---

## 📖 使用指南

### 基础用法

```java
// 方式1：工厂方法（适合简单场景）
TextElement text = TextElement.of("Hello World")
    .setPosition(RelativePosition.of(Direction.CENTER));

// 方式2：Builder模式（推荐，适合复杂场景）
TextElement text = TextElement.builder("Hello V2")
    .font("Microsoft YaHei", Font.BOLD, 32)
    .color(Color.RED)
    .textAlign(TextAlign.CENTER)
    .shadow(Color.GRAY, 2, 2)
    .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 100, 0, 0)))
    .build();
```

### 自动换行

```java
String longText = "这是一段很长的文本内容，需要自动换行处理。" +
                  "V2架构完全重新设计，不依赖V1的任何代码。";

TextElement text = TextElement.builder(longText)
    .font("Microsoft YaHei", Font.PLAIN, 18)
    .autoWordWrap(400)  // 最大宽度400px
    .lineHeight(28)
    .textAlign(TextAlign.LEFT)
    .textBackground(Color.YELLOW, 15)
    .build();
```

### 自适应字体

```java
// 字体会自动缩小以适应目标宽度
TextElement text = TextElement.builder("动态字号")
    .font("Microsoft YaHei", Font.BOLD, 48)
    .autoFitText(300, 12)  // 目标300px，最小12号
    .textAlign(TextAlign.CENTER)
    .build();
```

### 富文本

```java
// 不同片段可以有不同的样式
TextElement richText = TextElement.rich(
    TextSpan.of("红色粗体").setFontStyle(Font.BOLD).setColor(Color.RED),
    TextSpan.of(" + "),
    TextSpan.of("蓝色斜体").setFontStyle(Font.ITALIC).setColor(Color.BLUE),
    TextSpan.of(" + "),
    TextSpan.of("绿色普通").setFontStyle(Font.PLAIN).setColor(Color.GREEN)
).setPosition(RelativePosition.of(Direction.CENTER));
```

### 复杂样式

```java
TextElement text = TextElement.builder("复杂样式")
    .font("Microsoft YaHei", Font.BOLD, 36)
    .color(new Color(50, 50, 50))
    .textAlign(TextAlign.CENTER)
    .underline(true)
    .shadow(Color.GRAY, 2, 2)
    .stroke(Color.WHITE, 1.5f)
    .letterSpacing(5)
    .textBackground(Color.WHITE, 20)
    .textBackgroundArc(10)
    .position(RelativePosition.of(Direction.CENTER))
    .alpha(0.9f)
    .build();
```

---

## 🎨 实战场景示例

### 场景1：电商海报

```java
// 商品标题
TextElement title = TextElement.builder("夏季清仓大促")
    .font("Microsoft YaHei", Font.BOLD, 56)
    .color(new Color(255, 50, 50))
    .textAlign(TextAlign.CENTER)
    .shadow(new Color(0, 0, 0, 150), 4, 4)
    .stroke(Color.WHITE, 2.0f)
    .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 100, 0, 0)))
    .build();

// 价格文本
TextElement price = TextElement.builder("¥99.9")
    .font("Arial", Font.BOLD, 72)
    .color(new Color(255, 0, 0))
    .textAlign(TextAlign.CENTER)
    .position(RelativePosition.of(Direction.CENTER))
    .build();

// 原价（删除线）
TextElement originalPrice = TextElement.builder("原价: ¥299")
    .font("Microsoft YaHei", Font.PLAIN, 24)
    .color(Color.GRAY)
    .strikeThrough(true)
    .textAlign(TextAlign.CENTER)
    .position(RelativePosition.of(Direction.BOTTOM_CENTER, Margin.of(0, 0, 150, 0)))
    .build();

poster.addElement(title).addElement(price).addElement(originalPrice);
```

### 场景2：邀请函/名片

```java
// 姓名
TextElement name = TextElement.builder("张三")
    .font("Microsoft YaHei", Font.BOLD, 36)
    .color(new Color(30, 30, 30))
    .textAlign(TextAlign.LEFT)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 100, 0, 0)))
    .build();

// 职位
TextElement jobTitle = TextElement.builder("高级软件工程师")
    .font("Microsoft YaHei", Font.PLAIN, 20)
    .color(new Color(100, 100, 100))
    .textAlign(TextAlign.LEFT)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 150, 0, 0)))
    .build();

// 联系方式（富文本）
TextElement contact = TextElement.rich(
    TextSpan.of("📞 ").setFontStyle(Font.PLAIN).setColor(new Color(80, 80, 80)),
    TextSpan.of("138-0000-0000").setFontStyle(Font.PLAIN).setColor(Color.BLACK),
    TextSpan.of("\n"),
    TextSpan.of("📧 ").setFontStyle(Font.PLAIN).setColor(new Color(80, 80, 80)),
    TextSpan.of("zhangsan@example.com").setFontStyle(Font.PLAIN).setColor(new Color(0, 100, 200))
).setPosition(RelativePosition.of(Direction.BOTTOM_LEFT, Margin.of(80, 0, 0, 100)));

poster.addElement(name).addElement(jobTitle).addElement(contact);
```

### 场景3：数据报表标题

```java
// 主标题
TextElement mainTitle = TextElement.builder("2025年度销售报告")
    .font("Microsoft YaHei", Font.BOLD, 42)
    .color(new Color(40, 40, 40))
    .textAlign(TextAlign.CENTER)
    .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 60, 0, 0)))
    .build();

// 副标题
TextElement subtitle = TextElement.builder("数据周期: 2025.01.01 - 2025.12.31")
    .font("Microsoft YaHei", Font.PLAIN, 18)
    .color(new Color(120, 120, 120))
    .textAlign(TextAlign.CENTER)
    .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 120, 0, 0)))
    .build();

// 数据摘要（带背景）
TextElement summary = TextElement.builder(
    "总销售额: ¥1,234,567 | 增长率: +25.8% | 订单数: 8,901"
).font("Microsoft YaHei", Font.BOLD, 20)
    .color(new Color(0, 120, 0))
    .textAlign(TextAlign.CENTER)
    .autoWordWrap(700)
    .lineHeight(32)
    .textBackground(new Color(230, 255, 230), Margin.of(15, 10, 15, 10))
    .textBackgroundArc(8)
    .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 180, 0, 0)))
    .build();

poster.addElement(mainTitle).addElement(subtitle).addElement(summary);
```

### 场景4：社交媒体卡片

```java
// 用户名
TextElement username = TextElement.builder("@tech_blogger")
    .font("Arial", Font.BOLD, 22)
    .color(new Color(29, 161, 242))
    .textAlign(TextAlign.LEFT)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 80, 0, 0)))
    .build();

// 内容（自动换行）
TextElement content = TextElement.builder(
    "今天发布了新的文章《深入理解Java虚拟机》, " +
    "详细介绍了JVM的内存模型、垃圾回收机制和性能调优技巧。" +
    "欢迎大家阅读和讨论！#Java #JVM #性能优化"
).font("Microsoft YaHei", Font.PLAIN, 18)
    .color(Color.BLACK)
    .autoWordWrap(600)
    .lineHeight(28)
    .textAlign(TextAlign.LEFT)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 120, 0, 0)))
    .build();

// 时间戳
TextElement timestamp = TextElement.of("2小时前 · 来自 iPhone")
    .setFontName("Microsoft YaHei")
    .setFontSize(14)
    .setColor(new Color(140, 140, 140))
    .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 280, 0, 0)));

// 互动数据（富文本）
TextElement stats = TextElement.rich(
    TextSpan.of("💬 128 ").setFontStyle(Font.PLAIN).setColor(new Color(100, 100, 100)),
    TextSpan.of("  ").setFontStyle(Font.PLAIN).setColor(Color.GRAY),
    TextSpan.of("🔄 56 ").setFontStyle(Font.PLAIN).setColor(new Color(100, 100, 100)),
    TextSpan.of("  ").setFontStyle(Font.PLAIN).setColor(Color.GRAY),
    TextSpan.of("❤️ 892").setFontStyle(Font.PLAIN).setColor(new Color(224, 36, 94))
).setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 320, 0, 0)));

poster.addElement(username).addElement(content)
       .addElement(timestamp).addElement(stats);
```

### 场景5：菜单/价目表

```java
// 餐厅菜单示例
TextElement menuTitle = TextElement.builder("今日菜单")
    .font("Microsoft YaHei", Font.BOLD, 48)
    .color(new Color(139, 69, 19))
    .textAlign(TextAlign.CENTER)
    .position(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 50, 0, 0)))
    .build();

// 菜单项1
TextElement item1 = TextElement.rich(
    TextSpan.of("宫保鸡丁").setFontStyle(Font.BOLD).setFontSize(24).setColor(Color.BLACK),
    TextSpan.of(" .......................... ").setFontStyle(Font.PLAIN).setColor(Color.GRAY),
    TextSpan.of("¥38").setFontStyle(Font.BOLD).setFontSize(24).setColor(new Color(200, 0, 0))
).setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 150, 0, 0)));

// 菜单项2
TextElement item2 = TextElement.rich(
    TextSpan.of("麻婆豆腐").setFontStyle(Font.BOLD).setFontSize(24).setColor(Color.BLACK),
    TextSpan.of(" .......................... ").setFontStyle(Font.PLAIN).setColor(Color.GRAY),
    TextSpan.of("¥28").setFontStyle(Font.BOLD).setFontSize(24).setColor(new Color(200, 0, 0))
).setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 200, 0, 0)));

// 特价提示
TextElement special = TextElement.builder("⭐ 今日特价: 鱼香肉丝 ¥25")
    .font("Microsoft YaHei", Font.BOLD, 22)
    .color(new Color(255, 140, 0))
    .textAlign(TextAlign.CENTER)
    .textBackground(new Color(255, 250, 205), Margin.of(15, 10, 15, 10))
    .textBackgroundArc(10)
    .position(RelativePosition.of(Direction.BOTTOM_CENTER, Margin.of(0, 0, 80, 0)))
    .build();

poster.addElement(menuTitle).addElement(item1)
       .addElement(item2).addElement(special);
```

### 场景6：代码片段展示

```java
// 代码标题
TextElement codeTitle = TextElement.builder("Java 示例代码")
    .font("Microsoft YaHei", Font.BOLD, 24)
    .color(new Color(200, 200, 200))
    .textAlign(TextAlign.LEFT)
    .textBackground(new Color(60, 60, 60), Margin.of(20, 10, 20, 10))
    .textBackgroundArc(8, 8)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 100, 0, 0)))
    .build();

// 代码内容（等宽字体）
TextElement code = TextElement.builder(
    "public class HelloWorld {\n" +
    "    public static void main(String[] args) {\n" +
    "        System.out.println(\"Hello, V2!\");\n" +
    "    }\n" +
    "}"
).font("Consolas", Font.PLAIN, 16)
    .color(new Color(200, 220, 200))
    .autoWordWrap(600)
    .lineHeight(24)
    .textAlign(TextAlign.LEFT)
    .textBackground(new Color(30, 30, 30), Margin.of(20, 15, 20, 15))
    .textBackgroundArc(8, 8)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, 150, 0, 0)))
    .build();

poster.addElement(codeTitle).addElement(code);
```

### 场景7：引用/名言

```java
// 左引号
TextElement openQuote = TextElement.of(""")
    .setFontName("Georgia")
    .setFontSize(72)
    .setColor(new Color(200, 200, 200))
    .setPosition(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 100, 0, 0)));

// 引用内容
TextElement quote = TextElement.builder(
    "生活就像一盒巧克力，\n你永远不知道下一颗是什么味道。"
).font("Microsoft YaHei", Font.PLAIN, 28)
    .color(new Color(60, 60, 60))
    .autoWordWrap(500)
    .lineHeight(42)
    .textAlign(TextAlign.LEFT)
    .letterSpacing(2)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(160, 120, 0, 0)))
    .build();

// 作者
TextElement author = TextElement.builder("—— 阿甘正传")
    .font("Microsoft YaHei", Font.ITALIC, 20)
    .color(new Color(120, 120, 120))
    .textAlign(TextAlign.RIGHT)
    .position(RelativePosition.of(Direction.BOTTOM_RIGHT, Margin.of(0, 0, 120, 100)))
    .build();

poster.addElement(openQuote).addElement(quote).addElement(author);
```

### 场景8：标签/徽章

```java
// 折扣标签
TextElement discount = TextElement.builder("50% OFF")
    .font("Arial", Font.BOLD, 28)
    .color(Color.WHITE)
    .textAlign(TextAlign.CENTER)
    .textBackground(new Color(255, 0, 0), Margin.of(15, 10, 15, 10))
    .textBackgroundArc(20)
    .position(RelativePosition.of(Direction.TOP_RIGHT, Margin.of(0, 50, 50, 0)))
    .rotate(-15)  // 旋转15度
    .build();

// 新品标签
TextElement newBadge = TextElement.builder("NEW")
    .font("Arial", Font.BOLD, 20)
    .color(Color.WHITE)
    .textAlign(TextAlign.CENTER)
    .textBackground(new Color(0, 150, 255), Margin.of(12, 8, 12, 8))
    .textBackgroundArc(15)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(80, 80, 0, 0)))
    .build();

// 热卖标签
TextElement hotBadge = TextElement.builder("🔥 HOT")
    .font("Microsoft YaHei", Font.BOLD, 18)
    .color(Color.WHITE)
    .textAlign(TextAlign.CENTER)
    .textBackground(new Color(255, 100, 0), Margin.of(12, 8, 12, 8))
    .textBackgroundArc(15)
    .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(200, 80, 0, 0)))
    .build();

poster.addElement(discount).addElement(newBadge).addElement(hotBadge);
```

---

## 🔧 扩展指南

### 自定义布局策略

```java
// 继承 TextLayoutEngine 并重写方法
public class CustomLayoutEngine extends TextLayoutEngine {
    @Override
    protected TextLayoutResult computePlainLayout(...) {
        // 自定义布局逻辑
        // 例如：添加特殊的缩进、段落间距等
    }
}

// 在 TextElement 中使用
public class CustomTextElement extends TextElement {
    private final CustomLayoutEngine customEngine;
    
    @Override
    public Dimension calculateDimension(...) {
        return customEngine.layout(...);
    }
}
```

### 自定义渲染策略

```java
// 继承 TextRenderer 并重写方法
public class CustomRenderer extends TextRenderer {
    @Override
    protected void drawPlainLine(...) {
        // 自定义绘制逻辑
        // 例如：添加特殊效果、渐变文本等
    }
}
```

### 单元测试

```java
@Test
public void testLayout() {
    // 可以直接测试 Engine
    TextLayoutEngine engine = new TextLayoutEngine();
    
    TextElementConfig config = TextElementConfig.builder("Test")
        .fontSize(16)
        .build();
    
    TextLayoutResult result = engine.layout(
        config, null, 0, context, 800, 600
    );
    
    assertNotNull(result);
    assertTrue(result.getWidth() > 0);
}
```

---

## 💡 高级技巧

### 技巧1：复用配置对象

```java
// 创建通用样式配置
TextElementConfig titleStyle = TextElementConfig.builder("")
    .font("Microsoft YaHei", Font.BOLD, 32)
    .textAlign(TextAlign.CENTER)
    .build();

// 多个文本复用同一配置
TextElement title1 = new TextElement(
    TextElementConfig.builder("第一章").build()
);
TextElement title2 = new TextElement(
    TextElementConfig.builder("第二章").build()
);
```

### 技巧2：动态生成文本

```java
// 根据数据动态生成文本
List<String> items = Arrays.asList("苹果", "香蕉", "橙子");

int yOffset = 100;
for (String item : items) {
    TextElement text = TextElement.builder("• " + item)
        .font("Microsoft YaHei", Font.PLAIN, 20)
        .color(Color.BLACK)
        .position(RelativePosition.of(Direction.TOP_LEFT, Margin.of(100, yOffset, 0, 0)))
        .build();
    poster.addElement(text);
    yOffset += 40;
}
```

### 技巧3：文本对齐与基线

```java
// 不同基线的效果
TextElement topAlign = TextElement.builder("TOP基线")
    .baseLine(BaseLine.TOP)  // 以文本顶部为锚点
    .position(AbsolutePosition.of(Point.of(100, 100)))
    .build();

TextElement centerAlign = TextElement.builder("CENTER基线")
    .baseLine(BaseLine.CENTER)  // 以文本中心为锚点
    .position(AbsolutePosition.of(Point.of(100, 200)))
    .build();

TextElement baselineAlign = TextElement.builder("BASELINE基线")
    .baseLine(BaseLine.BASE_LINE)  // 以字体基线为锚点（默认）
    .position(AbsolutePosition.of(Point.of(100, 300)))
    .build();
```

### 技巧4：精确控制内边距

```java
// 不同方向的内边距
TextElement text = TextElement.builder("自定义内边距")
    .textBackground(Color.YELLOW, Margin.of(
        20,  // 左
        10,  // 上
        30,  // 右
        15   // 下
    ))
    .build();
```

### 技巧5：组合使用透明度和渐变

```java
// 半透明背景
TextElement semiTransparent = TextElement.builder("半透明效果")
    .font("Microsoft YaHei", Font.BOLD, 36)
    .color(Color.WHITE)
    .textBackground(new Color(0, 0, 0, 128), 20)  // 50%透明黑色背景
    .textBackgroundArc(10)
    .build();

// 渐变文本（需要配合gradient）
Gradient gradient = Gradient.linear(
    new Point(0, 0),
    new Point(200, 0),
    Color.RED,
    Color.BLUE
);

TextElement gradientText = TextElement.builder("渐变文本")
    .font("Microsoft YaHei", Font.BOLD, 48)
    .gradient(gradient)
    .build();
```

### 技巧6：处理长文本省略

```java
// 单行省略号
TextElement ellipsis = TextElement.builder(
    "这是一段很长的文本，超过宽度会显示省略号..."
).font("Microsoft YaHei", Font.PLAIN, 18)
    .layoutWidth(300)  // 设置最大宽度
    .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
    .maxLines(1)  // 最多1行
    .ellipsis("...")  // 自定义省略符
    .build();

// 多行截断
TextElement multiLine = TextElement.builder(longText)
    .autoWordWrap(400)
    .maxLines(3)  // 最多3行，第3行末尾加省略号
    .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
    .build();
```

### 技巧7：文本裁剪模式

```java
// 超出部分直接裁剪（不显示省略号）
TextElement clipped = TextElement.builder(longText)
    .autoWordWrap(400)
    .maxLines(2)
    .overflowStrategy(TextOverflowStrategy.CLIP)  // 裁剪模式
    .build();
```

### 技巧8：自定义文本拆分器

```java
// 使用自定义拆分器（例如按词拆分英文）
ITextSplitter customSplitter = new ITextSplitter() {
    @Override
    public TextSplitResult split(TextSplitRequest request) {
        // 自定义拆分逻辑
        // 例如：优先在空格、标点处换行
    }
};

TextElement text = TextElement.builder(englishText)
    .autoWordWrap(400)
    .textSplitter(customSplitter)
    .build();
```

---

## 📊 设计原则

### 1. 单一职责（SRP）
- `TextElementConfig` - 只存储配置
- `TextLayoutEngine` - 只做布局
- `TextRenderer` - 只做渲染
- `TextElement` - 只做入口协调

### 2. 依赖倒置（DIP）
- 高层模块不依赖低层模块
- 所有依赖通过构造函数注入
- 无静态单例（除了工具类）

### 3. 开闭原则（OCP）
- 对扩展开放：可继承 Engine/Renderer
- 对修改封闭：Config 不可变

### 4. 接口隔离（ISP）
- 每个组件只提供必要接口
- 不暴露内部实现细节

### 5. 不可变性（Immutability）
- `TextElementConfig` 完全不可变
- 线程安全，无副作用

---

## ⚡ 性能特性

### 缓存机制
- 布局结果自动缓存（WeakHashMap）
- 相同配置不重复计算
- 内存友好，自动回收

### 延迟计算
- 仅在需要时计算布局
- 避免不必要的性能开销

### 对象复用
- 字体对象缓存
- 测量结果复用
- 减少GC压力

---

## 🚀 迁移指南

### 从 V1 迁移到 V2

**V1 代码：**
```java
EnhanceTextElement text = new EnhanceTextElement("Hello")
    .setFontSize(24)
    .setColor(Color.RED);
```

**V2 等价代码：**
```java
TextElement text = TextElement.builder("Hello")
    .fontSize(24)
    .color(Color.RED)
    .build();
```

### 注意事项
1. V1 和 V2 可以共存，互不影响
2. 建议新代码使用 V2
3. V1 代码可逐步迁移

---

## 📝 API 参考

### TextElementConfig.Builder

| 方法 | 说明 | 示例 |
|------|------|------|
| `font()` | 设置字体 | `.font("Arial", Font.BOLD, 16)` |
| `fontSize()` | 设置字号 | `.fontSize(24)` |
| `textAlign()` | 对齐方式 | `.textAlign(TextAlign.CENTER)` |
| `autoWordWrap()` | 自动换行 | `.autoWordWrap(400)` |
| `autoFitText()` | 自适应字体 | `.autoFitText(300, 12)` |
| `textBackground()` | 背景色 | `.textBackground(Color.YELLOW, 10)` |
| `shadow()` | 阴影 | `.shadow(Color.GRAY, 2, 2)` |
| `stroke()` | 描边 | `.stroke(Color.WHITE, 1.5f)` |

### TextElement.Builder

继承所有 Config 方法，并增加：

| 方法 | 说明 | 示例 |
|------|------|------|
| `position()` | 位置 | `.position(RelativePosition.of(Direction.CENTER))` |
| `alpha()` | 透明度 | `.alpha(0.8f)` |
| `rotate()` | 旋转角度 | `.rotate(45)` |
| `gradient()` | 渐变 | `.gradient(gradient)` |

---

## 🎓 最佳实践

### 1. 使用 Builder 模式
```java
// ✅ 推荐
TextElement text = TextElement.builder("Hello")
    .fontSize(24)
    .color(Color.RED)
    .build();

// ❌ 不推荐
TextElementConfig config = new TextElementConfig(...); // 需要大量参数
```

### 2. 复用 Config
```java
// ✅ 相同样式复用
TextElementConfig style = TextElementConfig.builder("")
    .fontSize(16)
    .build();

TextElement t1 = new TextElement(style);
TextElement t2 = new TextElement(style);
```

### 3. 合理设置宽度
```java
// ✅ 明确指定换行宽度
.autoWordWrap(400)

// ✅ 或自适应宽度
.autoFitText(300, 12)

// ❌ 不设置可能导致文本溢出
```

---

## 🔮 未来规划

- [ ] 支持垂直文本布局
- [ ] 支持文本路径排列
- [ ] 支持渐变文本
- [ ] 支持文本动画
- [ ] 支持HTML解析
- [ ] 支持Markdown渲染

---

## 📄 License

Same as V1 - MIT License

---

**作者:** bytefuture  
**版本:** 2.0.0  
**日期:** 2025/04/15
