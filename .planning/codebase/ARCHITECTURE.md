# Architecture

**Analysis Date:** 2026-04-21

## Pattern Overview

**Overall:** Component-based rendering architecture with hierarchical element system

**Key Characteristics:**
- Canvas-based rendering system where `EasyPoster` acts as the main drawing surface
- Hierarchical element inheritance with abstract base classes providing common functionality
- Separation of geometry calculation from rendering logic
- Plugin-style chart system with specialized rendering pipelines

## Layers

**API Layer:**
- Purpose: Public interface for creating and rendering posters
- Location: `src/main/java/com/bytefuture/easy/poster/EasyPoster.java`
- Contains: Main canvas class with element factory methods
- Depends on: Element interfaces, model classes
- Used by: End users of the library

**Element Layer:**
- Purpose: All visual elements that can be rendered on the poster
- Location: `src/main/java/com/bytefuture/easy/poster/element/`
- Contains: Basic shapes, charts, text, advanced compositions
- Depends on: Geometry, model, text systems
- Used by: EasyPoster, ComposeElement

**Geometry Layer:**
- Purpose: Positioning, sizing, and spatial calculations
- Location: `src/main/java/com/bytefuture/easy/poster/geometry/`
- Contains: Point, Dimension, Position (Absolute/Relative), Margin
- Depends on: None (pure calculations)
- Used by: All elements for layout

**Model Layer:**
- Purpose: Configuration, context, and data transfer objects
- Location: `src/main/java/com/bytefuture/easy/poster/model/`
- Contains: Config, PosterContext, PosterListener, enums
- Depends on: Java AWT
- Used by: Elements, EasyPoster

**Text System:**
- Purpose: Advanced text layout and rendering
- Location: `src/main/java/com/bytefuture/easy/poster/text/`
- Contains: Layout engines, splitters, HTML parsing, metrics
- Depends on: AWT, model layer
- Used by: TextElement (v2), chart elements

**Chart System:**
- Purpose: Specialized chart rendering with layout calculators
- Location: `src/main/java/com/bytefuture/easy/poster/element/chart/`
- Contains: Bar, Line, Pie, Funnel charts with renderers
- Depends on: Element base classes, geometry, text
- Used by: EasyPoster, chart elements

**Utility Layer:**
- Purpose: Helper functions and common operations
- Location: `src/main/java/com/bytefuture/easy/poster/utils/`
- Contains: Image, QR code, rotation, color utilities
- Depends on: AWT
- Used by: Elements, charts

## Data Flow

**Rendering Pipeline:**

1. User creates `EasyPoster` instance with dimensions
2. Elements added via factory methods (e.g., `addTextElement`, `addBarChartElement`)
3. `EasyPoster.render()` creates graphics context and PosterContext
4. Each element's `render()` method called in sequence:
   - `calculateDimension()` computes size and position
   - `doRender()` performs actual drawing
5. Final BufferedImage returned

**Element Inheritance Hierarchy:**

```
IElement (interface)
└── AbstractElement<T> (base implementation)
    ├── AbstractRepeatableElement<T> (repeatable behavior)
    │   ├── AbstractDimensionElement<T> (width/height support)
    │   │   ├── AbstractChartElement<T> (chart base)
    │   │   │   ├── BarChartElement
    │   │   │   ├── LineChartElement
    │   │   │   ├── PieChartElement
    │   │   │   └── FunnelChartElement
    │   │   ├── ImageElement
    │   │   └── RectangleElement
    │   ├── TextElement (v1)
    │   ├── TextElement (v2)
    │   └── ComposeElement (composite pattern)
    └── CircleElement
    └── LineElement
```

## Key Abstractions

**IElement Interface:**
- Purpose: Contract for all renderable elements
- Examples: `src/main/java/com/bytefuture/easy/poster/element/IElement.java`
- Pattern: Strategy pattern - different element types implement same interface

**AbstractElement Base Class:**
- Purpose: Common properties (color, alpha, rotate, position)
- Examples: `src/main/java/com/bytefuture/easy/poster/element/AbstractElement.java`
- Pattern: Template method - defines render() workflow

**Position System:**
- Purpose: Flexible element positioning (absolute vs relative)
- Examples: `AbsolutePosition.java`, `RelativePosition.java`
- Pattern: Strategy pattern for coordinate calculation

**ComposeElement:**
- Purpose: Composite pattern for grouping elements
- Examples: `src/main/java/com/bytefuture/easy/poster/element/advance/ComposeElement.java`
- Pattern: Composite pattern with relative positioning

## Entry Points

**Main API:**
- Location: `src/main/java/com/bytefuture/easy/poster/EasyPoster.java`
- Triggers: User code instantiation
- Responsibilities: Canvas management, element creation, rendering orchestration

**Chart Elements:**
- Location: `src/main/java/com/bytefuture/easy/poster/element/chart/*`
- Triggers: `addBarChartElement()`, `addLineChartElement()` etc.
- Responsibilities: Specialized chart rendering with layout calculators

**Text V2:**
- Location: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Triggers: Builder pattern or static factory methods
- Responsibilities: Advanced text layout with HTML support

## Error Handling

**Strategy:** Checked exceptions wrapped in `PosterException`
- All rendering methods declare `throws Exception`
- Business validation throws `PosterException` with descriptive messages
- Pattern: Exception wrapping for clean API

## Cross-Cutting Concerns

**Logging/Debugging:** Config-driven debug mode draws element boundaries
**Validation:** Pre-render validation in chart elements
**Color Management:** Centralized color utilities with hex support
**Font Management:** Config-based default fonts with override support

---

*Architecture analysis: 2026-04-21*