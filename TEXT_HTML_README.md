# V2 TextElement HTML 支持说明

本文档描述当前 `com.bytefuture.easy.poster.element.v2.TextElement` 的 HTML 解析能力，以及这些 HTML 如何复用 V2 富文本能力进行布局与渲染。

当前实现目标不是“完整 HTML/CSS 渲染器”，而是“把一小部分常见 HTML 富文本语义稳定地转换成 `TextSpan`，再复用现有富文本布局链路”。

## 1. 入口

当前提供两个 HTML 入口：

```java
TextElement element = TextElement.html("<strong>Hello</strong>");

TextElement element = TextElement.builderHtml("<strong>Hello</strong>")
    .font("Dialog", Font.PLAIN, 24)
    .position(AbsolutePosition.of(Point.of(40, 40), Direction.TOP_LEFT))
    .build();
```

对应源码：

- `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- `src/main/java/com/bytefuture/easy/poster/text/html/HtmlTextSpanParser.java`

## 2. 工作方式

HTML 渲染不是单独画一遍，而是走下面这条链路：

1. `builderHtml(...)` / `html(...)` 接收 HTML 字符串。
2. `HtmlTextSpanParser` 将 HTML 解析成 `List<TextSpan>`。
3. `TextElementConfig` 把这组 `TextSpan` 当作 V2 富文本输入保存。
4. `TextLayoutEngine` 按已有 rich text 流程布局。
5. `RichTextWrapper` 负责富文本分词、换行、省略、裁切。
6. `TextRenderer` 负责按已有 rich fragment 逻辑绘制。

这意味着：

- HTML 复用了当前 V2 富文本的换行、最大行数、省略、背景、阴影、描边、对齐、基线等能力。
- HTML 自身只负责“解析成 span”，不直接参与绘制。

## 3. 当前支持的 HTML 标签

### 3.1 行内样式标签

当前支持以下标签，并会映射到 `TextSpan` 可表达的样式：

| HTML 标签 | 含义 | 映射结果 |
| --- | --- | --- |
| `<b>` | 粗体 | `fontStyle |= Font.BOLD` |
| `<strong>` | 粗体 | `fontStyle |= Font.BOLD` |
| `<i>` | 斜体 | `fontStyle |= Font.ITALIC` |
| `<em>` | 斜体 | `fontStyle |= Font.ITALIC` |
| `<u>` | 下划线 | `underline = true` |
| `<s>` | 删除线 | `strikeThrough = true` |
| `<strike>` | 删除线 | `strikeThrough = true` |
| `<span>` | 行内容器 | 仅解析其 `style` |
| `<font>` | 老式字体标签 | 解析 `color` / `size` |

### 3.2 块级和换行标签

当前支持以下结构性标签：

| HTML 标签 | 当前行为 |
| --- | --- |
| `<br>` / `<br/>` | 插入一个显式换行 |
| `<p>` | 作为块级段落处理，段前/段后按需要插入换行 |
| `<div>` | 作为块级容器处理 |
| `<li>` | 作为块级容器处理，不自动补项目符号 |
| `<ul>` | 作为块级容器处理，不额外绘制列表样式 |
| `<ol>` | 作为块级容器处理，不自动编号 |
| `<h1>` ~ `<h6>` | 作为块级容器处理，不自动设置标题字号 |

注意：

- 这些块级标签当前只表达“换行边界”，不表达浏览器里的默认 margin / padding / 字号规则。
- `<li>` 不会自动补 `•`。
- `<ol>` 不会自动生成 `1.` `2.` `3.`。

## 4. 当前支持的 style / 属性

### 4.1 `style` 属性

当前只解析以下 CSS 属性：

| style 属性 | 支持情况 | 映射结果 |
| --- | --- | --- |
| `color` | 支持 | `TextSpan.color` |
| `font-size` | 支持 | `TextSpan.fontSize` |
| `font-weight` | 支持部分 | `bold` 或数值 `>= 600` 视为粗体 |
| `font-style` | 支持部分 | `italic` / `oblique` 视为斜体 |
| `text-decoration` | 支持部分 | `underline` / `line-through` |

示例：

```html
<span style="color:#ff0000">红色</span>
<span style="font-size:24px">24px</span>
<span style="font-weight:bold">粗体</span>
<span style="font-weight:700">粗体</span>
<span style="font-style:italic">斜体</span>
<span style="text-decoration:underline">下划线</span>
<span style="text-decoration:line-through">删除线</span>
<span style="text-decoration:underline line-through">下划线+删除线</span>
```

### 4.2 `<font>` 属性

当前支持：

| 标签属性 | 支持情况 | 说明 |
| --- | --- | --- |
| `color` | 支持 | 映射到 `TextSpan.color` |
| `size` | 支持 | 按 HTML 传统 1~7 映射为像素字号 |

当前 `size` 映射表如下：

| HTML size | 当前映射字号 |
| --- | --- |
| `1` | `10` |
| `2` | `13` |
| `3` | `16` |
| `4` | `18` |
| `5` | `24` |
| `6` | `32` |
| `7` | `48` |

如果 `size` 不是 1~7，而是其他正整数，则直接使用该数值。

## 5. 当前支持的颜色格式

当前 `color` 解析支持：

| 格式 | 示例 | 是否支持 |
| --- | --- | --- |
| 十六进制 | `#ff0000` | 支持 |
| 十六进制含 alpha | `#80ff0000` | 支持 |
| `rgb(r,g,b)` | `rgb(255,0,0)` | 支持 |
| Java `Color` 常量名 | `red` / `blue` / `black` | 支持部分 |
| grey/gray 变体 | `gray` / `grey` / `lightgray` / `darkgrey` | 支持 |

注意：

- 当前不支持 `rgba(...)`。
- 当前不支持 `hsl(...)` / `hsla(...)`。
- 当前不支持 CSS 变量，如 `var(--brand-color)`。

## 6. 空白与换行规则

HTML 解析阶段会对空白做“文本渲染友好”的归一化处理。

### 6.1 普通空白

连续空白字符会压缩成单个空格。

例如：

```html
<span>Hello      World</span>
```

最终等价于：

```text
Hello World
```

### 6.2 块级元素换行

块级元素在以下时机会插入换行：

- 块级标签开始时，如果前面已经有可见内容
- 块级标签结束时，如果前面已经有可见内容

例如：

```html
<p>hello</p><p>world</p>
```

最终会形成两行：

```text
hello
world
```

### 6.3 `<br>` 换行

`<br>` 会插入一个显式换行。

例如：

```html
hello<br/>world
```

最终会形成两行：

```text
hello
world
```

### 6.4 尾部换行修剪

解析结束后，如果最后一个 span 只因为块结束多出尾部 `\n`，会自动裁掉尾部换行，避免最终文本多出空白尾行。

## 7. 样式叠加规则

当前样式是可叠加的。

例如：

```html
<strong><span style="color:#ff0000;font-size:28px;text-decoration:underline">Demo</span></strong>
```

会组合为：

- 粗体
- 红色
- 28px
- 下划线

再例如：

```html
<span style="font-style:italic"><span style="font-weight:bold">Mix</span></span>
```

最终会得到粗斜体。

## 8. Span 合并规则

解析器在输出 `TextSpan` 时会尝试合并“相邻且样式完全相同”的文本片段，减少不必要的 span 数量。

相同的判断维度包括：

- `color`
- `fontStyle`
- `fontSize`
- `underline`
- `strikeThrough`

例如：

```html
<strong>Hello</strong><strong>World</strong>
```

当前会被合并成一个粗体 span：

```text
"HelloWorld"
```

如果样式不同，则不会合并。

## 9. 与 V2 富文本能力的关系

HTML 入口复用的是现有 rich text 能力，所以 HTML 解析后天然支持以下 V2 能力：

- `font(...)`
- `fontSize(...)`
- `fontName(...)`
- `textAlign(...)`
- `lineHeight(...)`
- `layoutWidth(...)`
- `autoWordWrap(...)`
- `maxLines(...)`
- `overflowStrategy(...)`
- `ellipsis(...)`
- `letterSpacing(...)`
- `shadow(...)`
- `stroke(...)`
- `textBackground(...)`
- `textPadding(...)`
- `textBackgroundArc(...)`
- `baseLine(...)`
- `position(...)`
- `alpha(...)`
- `gradient(...)`

注意其中有一个重要规则：

- HTML span 上声明的样式优先表达“局部富文本差异”
- `TextElement.builderHtml(...).font(...)` 这类 builder 配置仍然是整体默认样式
- 如果某个局部 span 没设置颜色/字号/字形，则会回退到整体默认样式

## 10. 示例

### 10.1 基础用法

```java
TextElement element = TextElement.builderHtml(
        "<strong>Hello</strong> <span style='color:#e74c3c'>Poster</span>")
    .font("Dialog", Font.PLAIN, 28)
    .position(AbsolutePosition.of(Point.of(40, 60), Direction.TOP_LEFT))
    .build();
```

### 10.2 HTML + 自动换行

```java
TextElement element = TextElement.builderHtml(
        "<span style='color:#c0392b'>Rich </span>"
            + "<strong>HTML </strong>"
            + "<span style='color:#2c3e50'>content should wrap with the existing V2 rich text engine.</span>")
    .font("Dialog", Font.PLAIN, 22)
    .autoWordWrap(260)
    .lineHeight(34)
    .textBackground(new Color(244, 248, 255), 14)
    .position(AbsolutePosition.of(Point.of(40, 44), Direction.TOP_LEFT))
    .build();
```

### 10.3 HTML + 段落/换行

```java
TextElement element = TextElement.builderHtml(
        "<p>第一段第一行<br/>第一段第二行</p><p>第二段</p>")
    .font("Dialog", Font.PLAIN, 20)
    .lineHeight(30)
    .position(AbsolutePosition.of(Point.of(40, 40), Direction.TOP_LEFT))
    .build();
```

### 10.4 HTML + 单行省略

```java
TextElement element = TextElement.builderHtml(
        "<span style='color:#e74c3c'>This rich html line </span>"
            + "<span style='color:#2980b9'>should be ellipsized.</span>")
    .font("Dialog", Font.PLAIN, 24)
    .layoutWidth(240)
    .overflowStrategy(TextOverflowStrategy.ELLIPSIS)
    .position(AbsolutePosition.of(Point.of(40, 78), Direction.TOP_LEFT))
    .build();
```

## 11. 当前不支持的能力

下面这些能力当前不会生效，或者会被忽略：

- `font-family`
- `background`, `background-color`
- `line-height`
- `letter-spacing`
- `margin`, `padding`
- `display`
- `text-align`
- `border`
- `href`
- 图片标签 `<img>`
- 表格 `<table>`
- 完整列表语义（项目符号/自动编号）
- 嵌套 CSS 选择器
- `<style>` 样式表
- 外链 CSS
- `class`
- `id`
- `rgba(...)`
- `hsl(...)`
- 实体布局能力，如真正的 DOM 盒模型

换句话说，当前实现是：

- 支持“文本内容 + 少量内联文本样式 + 换行边界”
- 不支持“完整网页布局”

## 12. 已知限制

### 12.1 不是浏览器一致性目标

当前不是按浏览器视觉完全对齐实现，因此以下默认行为不会照搬浏览器：

- 标题标签不会自动放大字号
- `<p>` 不会自动加浏览器默认上下 margin
- `<ul>/<ol>/<li>` 不会自动绘制列表前缀

### 12.2 解析粒度受 `TextSpan` 表达力限制

当前 `TextSpan` 只能表达：

- 文本
- 颜色
- 字形
- 字号
- 下划线
- 删除线

因此任何超出这个集合的 HTML/CSS 能力，哪怕语法被识别，也没有地方落到最终渲染模型中。

### 12.3 空白会被归一化

这更接近 HTML 普通文本节点的处理语义，但如果你希望保留“多个连续空格”的视觉效果，当前实现并不适合。

## 13. 测试覆盖

当前已经有功能测试验证：

- HTML 能转换成正确的 `TextSpan`
- `<br>` / `<p>` 能转换成正确的富文本行
- HTML 结果最终是通过 rich fragment 渲染出来的

对应测试：

- `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

本次还补充了 UI PNG 手工验证测试：

- `src/test/java/com/bytefuture/easy/poster/ui/v2/V2TextElementHtmlPngTest.java`

## 14. 建议使用方式

适合用 HTML 的场景：

- 业务侧已有简单富文本字符串
- 需要低成本表达粗体、斜体、颜色、字号、下划线、删除线
- 需要复用 V2 的富文本换行和省略能力

不适合用 HTML 的场景：

- 需要完整 CSS 布局
- 需要列表、表格、图片、超链接等复杂富文本元素
- 需要浏览器级一致渲染

如果只是后端拼接几段彩色文字，优先考虑：

```java
TextElement.builder(
    TextSpan.of("A").setColor(Color.RED),
    TextSpan.of("B").setColor(Color.BLUE)
)
```

如果业务已经天然产出 HTML，再考虑：

```java
TextElement.builderHtml("<span style='color:red'>A</span><span style='color:blue'>B</span>")
```

## 15. 后续可扩展方向

如果后面继续增强，建议按下面顺序推进：

1. 增加 `rgba(...)` / 更多颜色格式支持
2. 增加 `font-family` 映射能力
3. 增加 `<small>` / `<big>` / 标题标签的默认字号策略
4. 增加列表前缀生成能力
5. 增加 `background-color` 到 span 级背景表达能力
6. 评估是否需要引入更强的 HTML 解析器

当前阶段不建议直接把目标抬到“浏览器兼容渲染”，那会显著扩大系统复杂度，也偏离 V2 文本引擎的定位。
