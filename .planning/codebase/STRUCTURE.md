# Codebase Structure

**Analysis Date:** 2026-04-21

## Directory Layout

```
easy-poster/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/bytefuture/easy/poster/
│   │           ├── element/          # Visual elements (shapes, charts, text)
│   │           │   ├── advance/      # Advanced elements (compose, repeat)
│   │           │   ├── basic/        # Basic shapes
│   │           │   ├── chart/        # Chart elements
│   │           │   │   ├── bar/      # Bar chart specific
│   │           │   │   ├── line/     # Line chart specific
│   │           │   │   └── base/     # Chart base classes
│   │           │   ├── special/      # Special elements (QR, stars)
│   │           │   └── v2/          # Text V2 implementation
│   │           ├── geometry/        # Positioning and dimensions
│   │           ├── model/           # Configuration and context
│   │           ├── text/            # Text layout system
│   │           │   ├── html/        # HTML parsing
│   │           │   ├── layout/      # Layout engines
│   │           │   ├── metrics/     # Text measurement
│   │           │   └── split/       # Text splitting
│   │           ├── utils/           # Utility classes
│   │           ├── EasyPoster.java  # Main API entry point
│   │           └── exception/       # Custom exceptions
│   └── test/
│       └── java/                    # Test sources (empty structure)
├── docs/                           # Documentation
├── .claude/                        # Claude configuration
├── .planning/                      # Planning documents
├── target/                         # Build output
├── pom.xml                         # Maven configuration
├── LICENSE
└── README.md
```

## Directory Purposes

**element/**:
- Purpose: All visual elements that can be rendered
- Contains: Basic shapes, charts, text, compositions
- Key files: `IElement.java`, `AbstractElement.java`, `ComposeElement.java`

**element/basic/**:
- Purpose: Fundamental geometric shapes
- Contains: `TextElement.java` (v1), `ImageElement.java`, `RectangleElement.java`, `CircleElement.java`, `LineElement.java`

**element/chart/**:
- Purpose: Chart rendering system
- Contains: `BarChartElement.java`, `LineChartElement.java`, `PieChartElement.java`, `FunnelChartElement.java`
- Key pattern: Separate layout calculators and renderers for each chart type

**element/advance/**:
- Purpose: Complex element compositions
- Contains: `ComposeElement.java` (composite pattern), `RepeatElement.java`

**element/v2/**:
- Purpose: Next-generation text element
- Contains: `TextElement.java`, `TextElementConfig.java`, `TextLayoutEngine.java`, `TextRenderer.java`

**geometry/**:
- Purpose: Spatial calculations and positioning
- Contains: `Point.java`, `Dimension.java`, `Position.java`, `AbsolutePosition.java`, `RelativePosition.java`, `Margin.java`

**model/**:
- Purpose: Configuration and data objects
- Contains: `Config.java`, `PosterContext.java`, `PosterListener.java`, enums (`BaseLine.java`, `RelativeDirection.java`)

**text/**:
- Purpose: Advanced text processing
- Contains: Layout engines, HTML parsers, text splitters, metrics calculators
- Key files: `text/layout/TextLayoutResult.java`, `text/split/TextSplitterSimpleImpl.java`

**utils/**:
- Purpose: Helper utilities
- Contains: `ImageUtils.java`, `QrCodeUtil.java`, `RotateUtils.java`, `PointUtils.java`, `HexUtils.java`, `StringUtils.java`

## Key File Locations

**Entry Points:**
- `src/main/java/com/bytefuture/easy/poster/EasyPoster.java`: Main API class

**Core Abstractions:**
- `src/main/java/com/bytefuture/easy/poster/element/IElement.java`: Element interface
- `src/main/java/com/bytefuture/easy/poster/element/AbstractElement.java`: Base element class
- `src/main/java/com/bytefuture/easy/poster/geometry/Position.java`: Positioning strategy

**Configuration:**
- `src/main/java/com/bytefuture/easy/poster/model/Config.java`: Global configuration
- `src/main/java/com/bytefuture/easy/poster/model/PosterContext.java`: Rendering context

**Chart Specialization:**
- `src/main/java/com/bytefuture/easy/poster/element/chart/base/AbstractChartElement.java`: Chart base class
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java`: Bar chart implementation

## Naming Conventions

**Files:**
- Elements: `[Type]Element.java` (e.g., `TextElement.java`, `BarChartElement.java`)
- Base Classes: `Abstract[Concept].java` (e.g., `AbstractElement.java`)
- Interfaces: `I[Concept].java` (e.g., `IElement.java`)
- Utilities: `[Concept]Utils.java` (e.g., `ImageUtils.java`)
- Models: `[Concept].java` (e.g., `Config.java`)

**Directories:**
- Grouped by feature/domain (element, geometry, text, model)
- Subdirectories for specialization (chart/bar, chart/line, text/layout)

## Where to Add New Code

**New Basic Element:**
- Implementation: `src/main/java/com/bytefuture/easy/poster/element/basic/`
- Extend: `AbstractDimensionElement` or `AbstractElement`
- Register: Add factory method in `EasyPoster.java`

**New Chart Type:**
- Implementation: `src/main/java/com/bytefuture/easy/poster/element/chart/`
- Extend: `AbstractChartElement`
- Add layout calculator in subdirectory (e.g., `chart/[type]/`)

**New Text Feature:**
- Implementation: `src/main/java/com/bytefuture/easy/poster/text/`
- Follow existing patterns in layout/metrics/split packages

**New Utility:**
- Implementation: `src/main/java/com/bytefuture/easy/poster/utils/`
- Naming: `[Feature]Utils.java`

**Configuration Properties:**
- Implementation: `src/main/java/com/bytefuture/easy/poster/model/Config.java`
- Add getter/setter for new property

## Special Directories

**.claude/**:
- Purpose: Claude AI configuration and skills
- Generated: Yes (by Claude tooling)
- Committed: Yes

**docs/**:
- Purpose: Project documentation
- Generated: Manual
- Committed: Yes

**target/**:
- Purpose: Maven build output
- Generated: Yes (by Maven)
- Committed: No (gitignored)

---

*Structure analysis: 2026-04-21*
