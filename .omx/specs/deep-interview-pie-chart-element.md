# Deep Interview Spec - pie-chart-element

## Metadata
- Profile: standard
- Rounds: 5
- Final ambiguity: 0.18
- Threshold: 0.20
- Context type: brownfield
- Context snapshot: `.omx/context/pie-chart-element-20260413T015304Z.md`

## Clarity Breakdown
| Dimension | Score |
| --- | --- |
| Intent | 0.76 |
| Outcome | 0.78 |
| Scope | 0.86 |
| Constraints | 0.92 |
| Success | 0.78 |
| Context | 0.92 |

## Intent
在现有 Java 8 图像绘制工具中扩展图表能力，使海报/图片生成场景支持占比类数据展示，并尽量复用现有 `BarChartElement` 的 API 风格、图例风格、颜色策略与测试组织方式。

## Desired Outcome
在 `com.bytefuture.easy.poster.element.chart` 包下形成一个可扩展的饼图元素方案，支持普通饼图、环形图、玫瑰图/南丁格尔图三种模式，重点保证输入配置项、图例、颜色配置清晰一致，并能通过 `EasyPoster` 以与柱状图相似的方式接入。

## In Scope
- 新增 `PieChartElement`
- 新增切片数据模型，风格类似 `PieChartSlice(name, value, optional color)`
- 使用同一个 element 通过 mode 切换以下图表类型：
  - 普通饼图
  - 环形图
  - 玫瑰图 / 南丁格尔图
- 图例内容可配置
- 扇区文本内容可配置
- 单切片自定义颜色
- Element 级默认色板覆写
- 未指定切片颜色时按默认色板顺序回退
- 图例色块与扇区颜色一致
- `EasyPoster` 新增饼图入口（风格参照 `addBarChartElement`）
- 增加相应 chart 测试
- 实现代码中为变量、属性、方法补充注释

## Out of Scope / Non-goals
- 3D 效果
- 动画
- 交互能力
- 渐变色
- 纹理填充
- 透明度分层效果
- 自动同色系明暗变体
- 其他未明确提出的高级标签布局能力

## Decision Boundaries
实现阶段 OMX 可直接决定：
- 复用现有 `BarChartElement` 的命名与 builder 风格
- 采用单一 `PieChartElement` + mode，而非拆分多个独立 element
- 默认颜色回退策略对齐 `BarChartElement`
- 默认图例行为：开启、上方横向排列、放不下自动换行
- 默认扇区文本：开启；空间不足时允许自动隐藏，优先保证图例可读性

实现阶段 OMX 不应自行改变而无需再次确认：
- 不应把单 element 改成多个 element
- 不应加入超出非目标列表之外的视觉能力
- 不应移除“图例内容可配置”和“扇区文本内容可配置”这两个核心要求

## Constraints
- Java 8 兼容
- 需遵循当前 brownfield API/命名/测试组织模式
- 参考现有 chart 包结构与 `EasyPoster` 接入方式
- 颜色策略需与现有柱状图体验保持一致
- 代码需要添加注释：变量、属性、方法都应有注释

## Input Configuration (confirmed)
建议至少覆盖以下输入配置：
- `mode`：普通饼图 / 环形图 / 玫瑰图（南丁格尔）
- `title`
- `slices`：`name + value + optional color`
- `showLegend`
- `legendDisplayMode`：名称 / 数值 / 百分比 / 名称+百分比（如需更完整也可含 名称+数值）
- `labelDisplayMode`：名称 / 数值 / 百分比 / 名称+百分比
- `palette`：element 级默认色板覆写
- 图例样式：字体大小、item gap、marker size、文本颜色
- 基础布局参数：padding
- 普通饼图/环形图/玫瑰图所需的模式参数（如 inner radius、rose radius scale 等）

## Legend Rules (confirmed)
- 图例默认开启
- 默认位于图表上方
- 默认横向排列
- 放不下时自动换行
- 图例色块颜色与扇区颜色完全一致
- 图例显示内容可配置

## Color Rules (confirmed)
- 每个切片支持单独指定颜色
- 未指定时按默认色板顺序分配
- `PieChartElement` 支持整体覆写默认色板
- 首版不做渐变、纹理、透明度分层、自动同色系变体

## Label Rules (confirmed)
- 扇区文本默认开启
- 扇区文本显示内容可配置
- 空间不足时可自动隐藏部分或全部扇区文本
- 默认优先保证图例可读性与图形完整性

## Testable Acceptance Criteria
- 可以通过与 `addBarChartElement(int, int)` 类似的方式创建饼图 element
- 可添加多个切片，每个切片至少支持 `name/value/optional color`
- 同一 element 可切换为普通饼图、环形图、玫瑰图/南丁格尔图
- 图例可显示且默认置顶横排，超宽可换行
- 图例显示内容支持配置
- 扇区文本显示内容支持配置
- 切片颜色与图例颜色一致
- 未配置颜色时存在默认色板回退
- 可整体覆写默认色板
- 首版不包含非目标能力
- 新增或修改的相关变量、属性、方法都具备注释
- 至少补充对应 chart 测试覆盖基础渲染与模式差异

## Assumptions Exposed + Resolutions
- 假设 1：用户只要基础饼图。
  - 结论：不成立，需同时支持环形图和玫瑰图/南丁格尔图。
- 假设 2：图例仅需简单名称显示。
  - 结论：不成立，图例内容需可配置。
- 假设 3：颜色只需要切片级指定即可。
  - 结论：不成立，还需要 element 级色板覆写与默认回退策略。

## Pressure-pass Findings
- 对 scope 的追问把“新增饼图”明确成“三种模式 + 其他能力排除”。
- 对 color 的追问把“重点确认颜色”明确成“切片色、自定义色板、严格非目标列表”。

## Brownfield Evidence vs Inference
### Evidence
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartSeries.java`
- `src/main/java/com/bytefuture/easy/poster/EasyPoster.java`
- `src/test/java/com/bytefuture/easy/poster/ui/chart/BarChartBasicTest.java`

### Inference
- `PieChartElement` 的 API 风格与默认图例/色板命名最好参照 `BarChartElement`，这是基于现有代码一致性做出的实现建议，而不是用户逐字指定。

## Technical Context Findings
- chart 包当前只有柱状图
- `EasyPoster` 当前没有饼图快捷入口
- `BarChartElement` 已具备 legend + palette 的可复用设计参考
- 测试组织已存在 `ui/chart` 目录，可直接延续

## Handoff Recommendation
推荐下一步：`$ralplan`

建议调用：
`$plan --consensus --direct .omx/specs/deep-interview-pie-chart-element.md`
