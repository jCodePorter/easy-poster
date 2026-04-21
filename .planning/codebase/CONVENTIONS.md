# Coding Conventions

**Analysis Date:** 2026-04-21

## Naming Patterns

**Files:**
- Java class files follow standard Java naming: `PascalCase` for class names
- Test files follow pattern: `[ClassName]Test.java`
- Package structure: `com.bytefuture.easy.poster.[layer].[type]`

**Classes:**
- Main classes use nouns: `EasyPoster`, `TextElement`, `BarChartElement`
- Abstract classes: `AbstractElement`, `AbstractRepeatableElement`
- Interfaces: `IElement`, `ITextSplitter`

**Methods:**
- Builder pattern with fluent interfaces: `setFontSize()`, `setColor()`, `setPosition()`
- Action methods: `render()`, `calculateDimension()`, `doRender()`
- Getter methods use standard JavaBeans: `getAlpha()`, `getRotate()`

**Variables:**
- Private fields use camelCase: `posterWidth`, `renderedElements`, `autoWordWrap`
- Method parameters: `context`, `dimension`, `posterWidth`, `posterHeight`
- Constants not heavily used, configuration through instance fields

## Code Style

**Formatting:**
- Standard Java formatting with 4-space indentation
- Opening braces on same line as declaration
- Method chaining for fluent API: `.setFontSize(25).setColor(Color.red)`

**Linting:**
- No explicit linting configuration detected
- Code follows standard Java conventions

## Import Organization

**Order:**
1. External library imports (Lombok, Java AWT, etc.)
2. Internal project imports
3. Static imports not heavily used

**Example from `TextElement.java`:**
```java
import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
```

## Error Handling

**Patterns:**
- Custom exception: `PosterException` for validation errors
- Constructor validation: `throw new PosterException("alpha must be between 0 and 1")`
- Try-catch in main rendering: `asFile()` and `asBytes()` methods wrap exceptions
- Optional usage: `Optional.ofNullable()` for nullable configurations

**Exception Types:**
- `PosterException`: Main application exception class
- Standard Java exceptions for standard cases

## Comments

**Javadoc Usage:**
- Public classes have Javadoc with `@author` and `@since` tags
- Public methods documented with parameter and return tags
- Complex methods have detailed explanations

**Example:**
```java
/**
 * 文本元素，java中文本字符串在绘制时，按照字体排印学中原则，坐标点 y 值，即绘制文本的base line
 * <p>
 * 当定位为 AbsolutePosition 时，不支持 direction 属性
 * 同时当为 RelativePosition 时，不支持 baseline 属性
 *
 * @author biaoy
 * @since 2025/02/21
 */
```

**Inline Comments:**
- Used to explain complex logic, especially in rendering calculations
- Chinese comments common for business logic explanation

## Function Design

**Method Signatures:**
- Builder pattern returns `this` for method chaining
- Clear parameter naming
- Overloaded methods for different use cases

**Example:**
```java
public TextElement setFont(String fontName, int fontStyle, int fontSize) {
    this.fontName = fontName;
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
    return this;
}
```

**Size:**
- Methods generally focused and single-purpose
- Large classes exist (e.g., `TextElement.java` with 449 lines, `BarChartElement.java` with 1079 lines)

## Module Design

**Exports:**
- Main API through `EasyPoster` class
- Element classes in `element` package
- Geometry classes in `geometry` package
- Model classes in `model` package

**Barrel Files:**
- Not used in Java style
- Individual class imports required
- Main entry point: `EasyPoster` class with static factory methods

## Class Hierarchy

**Element Structure:**
```java
IElement (interface)
└── AbstractElement (abstract class)
    ├── AbstractRepeatableElement
    │   └── TextElement
    ├── CircleElement
    ├── RectangleElement
    └── ComposeElement
```

**Pattern Usage:**
- Template method pattern in `AbstractElement.render()`
- Strategy pattern for text splitting (`ITextSplitter`)
- Builder pattern for element configuration
- Factory methods: `TextElement.of()`, `Gradient.of()`

## Lombok Usage

**Annotations:**
- `@Getter` for field access
- `@Setter` for mutable fields
- `@Data` for utility classes
- `@Builder` pattern not heavily used, manual builder pattern preferred

## Testing Patterns

**Test Structure:**
- Test classes in `src/test/java` mirroring main structure
- One test method per feature/scenario
- Descriptive test method names: `testBasic()`, `getGlobal()`
- Output-based testing: generates PNG files for visual verification

**Assertions:**
- Minimal assertion usage detected
- Tests appear to be integration/visual tests rather than unit tests
- JUnit 4 framework used

---

*Convention analysis: 2026-04-21*