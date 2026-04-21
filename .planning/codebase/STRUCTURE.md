# Code Structure & Organization

## Directory Structure

### Maven Standard Layout
```
easy-poster/
├── src/
│   ├── main/
│   │   └── java/com/bytefuture/easy/poster/
│   │       ├── element/          # Visual elements
│   │       │   ├── advance/      # Advanced elements (Compose, Repeat)
│   │       │   ├── basic/        # Basic elements (Text, Image, Shapes)
│   │       │   ├── chart/        # Chart elements
│   │       │   │   ├── bar/      # Bar chart implementation
│   │       │   │   ├── base/     # Base chart classes
│   │       │   │   └── line/     # Line chart implementation
│   │       │   └── chart/        # Additional charts (Pie, Funnel)
│   │       ├── exception/        # Custom exceptions
│   │       ├── geometry/         # Geometric primitives
│   │       ├── model/            # Data models and configuration
│   │       └── EasyPoster.java   # Main canvas class
│   └── test/
│       └── java/                 # Test code
├── target/                       # Build output
├── docs/                         # Documentation
└── pom.xml                       # Maven configuration
```

## Class Hierarchy

### Element Class Hierarchy
```
IElement (Interface)
├── AbstractElement (Abstract Class)
│   ├── AbstractDimensionElement (Abstract Class)
│   │   ├── TextElement
│   │   ├── ImageElement
│   │   ├── RectangleElement
│   │   ├── CircleElement
│   │   └── AbstractChartElement
│   │       ├── BarChartElement
│   │       ├── LineChartElement
│   │       ├── PieChartElement
│   │       └── FunnelChartElement
│   ├── LineElement
│   └── AbstractRepeatableElement (Abstract Class)
│       ├── ComposeElement
│       └── RepeatElement
```

### Chart Specialization Hierarchy
```
AbstractChartElement
├── BarChartElement
├── LineChartElement
├── PieChartElement
└── FunnelChartElement
```

## File Count by Package
- **Root Package**: 1 file (EasyPoster.java)
- **Element Package**: Multiple sub-packages with ~20+ element classes
- **Chart Package**: ~15+ chart-related classes
- **Model Package**: ~5+ configuration and context classes
- **Exception Package**: 1 exception class
- **Geometry Package**: 1 geometry class

## Core Classes and Responsibilities

### 1. Main Classes
- `EasyPoster.java` - Main canvas class (246 lines)
- `PosterContext.java` - Rendering context holder
- `Config.java` - Global configuration

### 2. Element Base Classes
- `IElement.java` - Element interface defining render contract
- `AbstractElement.java` - Base implementation with common functionality
- `AbstractDimensionElement.java` - For elements with dimensions
- `AbstractRepeatableElement.java` - For repeatable elements

### 3. Basic Elements
- `TextElement.java` - Text rendering with font support
- `ImageElement.java` - Image embedding and scaling
- `RectangleElement.java` - Rectangle drawing
- `CircleElement.java` - Circle and oval drawing
- `LineElement.java` - Line drawing

### 4. Chart Elements
- `BarChartElement.java` - Bar chart implementation
- `LineChartElement.java` - Line chart with smoothing
- `PieChartElement.java` - Pie chart rendering
- `FunnelChartElement.java` - Funnel chart visualization

### 5. Chart Support Classes
- `BarChartLayoutCalculator.java` - Bar chart positioning logic
- `BarChartRangeResolver.java` - Value range calculation
- `BarChartLabelRenderer.java` - Axis label rendering
- `LinePathBuilder.java` - Line path construction interface
- `LinePathBuilderFactory.java` - Path builder creation
- `MonotoneSmoothLinePathBuilder.java` - Monotone smoothing algorithm
- `SmoothLinePathBuilder.java` - Standard smoothing algorithm
- `ChartDataPoint.java` - Chart data representation
- `ChartLayoutBox.java` - Layout calculations
- `ChartLegendRenderer.java` - Legend rendering
- `ChartStyle.java` - Visual styling
- `ChartTextSupport.java` - Text utilities for charts
- `ChartValueRange.java` - Value range handling
- `NamedColorValue.java` - Named color values

### 6. Advance Elements
- `ComposeElement.java` - Element composition
- `RepeatElement.java` - Element repetition

### 7. Support Classes
- `PosterException.java` - Custom exception
- `PosterListener.java` - Listener interface
- `Point.java` - Coordinate representation
- `Config.java` - Configuration settings
- `PosterContext.java` - Rendering context

## Code Organization Principles

### 1. Package-by-Feature
- Elements grouped by functionality (basic, chart, advance)
- Charts further divided by type (bar, line, base)
- Clear separation of concerns

### 2. Interface-Driven Design
- `IElement` interface defines contract for all visual elements
- Allows for polymorphic rendering
- Enables easy extension

### 3. Abstract Base Classes
- Common functionality extracted to abstract classes
- Reduces code duplication
- Provides template methods

### 4. Specialized Packages
- Chart-specific logic isolated in sub-packages
- Clear boundaries between different element types
- Easy to add new chart types

## Class Dependencies

### EasyPoster Dependencies
- All element types (IElement implementations)
- PosterContext and Config models
- PosterListener interface
- Geometry classes (Point)
- Exception classes

### Element Dependencies
- PosterContext for rendering
- Geometry classes for positioning
- Chart elements depend on chart-specific support classes

### Chart Dependencies
- Chart data structures (ChartDataPoint, ChartLayoutBox)
- Chart styling (ChartStyle)
- Layout calculators and renderers
- Base chart functionality

## Build Artifacts
- **Main JAR**: Contains all compiled classes
- **Source JAR**: Generated from source code
- **Javadoc JAR**: Generated documentation
- **Signed Artifacts**: GPG-signed for Maven Central