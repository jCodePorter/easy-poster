# Testing Patterns

**Analysis Date:** 2026-04-21

## Test Framework

**Runner:**
- JUnit 4.12
- Config: Maven Surefire plugin (standard Maven test execution)

**Assertion Library:**
- JUnit standard assertions
- Minimal assertion usage observed

**Run Commands:**
```bash
mvn test              # Run all tests
mvn test -Dtest=TestClassName  # Run specific test
```

## Test File Organization

**Location:**
- Separate test directory: `src/test/java`
- Mirrors main source structure: `com.bytefuture.easy.poster.[layer]`

**Naming:**
- Pattern: `[ClassName]Test.java`
- Examples: `TextBasicTest.java`, `BarChartBasicTest.java`, `ImageBasicTest.java`

**Structure:**
```
src/test/java/
├── com/bytefuture/easy/poster/
│   ├── ui/
│   │   ├── basic/          # Basic element tests
│   │   │   ├── text/
│   │   │   ├── image/
│   │   │   └── ...
│   │   ├── chart/          # Chart element tests
│   │   ├── advance/        # Advanced element tests
│   │   └── v2/             # V2 text tests
│   ├── func/               # Function tests
│   └── verify/             # Verification tests
```

## Test Structure

**Suite Organization:**
```java
package com.bytefuture.easy.poster.ui.basic.text;

import com.bytefuture.easy.poster.EasyPoster;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Point;
import org.junit.Test;

import java.awt.*;

/**
 * 文本渲染测试
 *
 * @author biaoy
 * @since 2025/03/12
 */
public class TextBasicTest {

    @Test
    public void testBasic() {
        EasyPoster poster = new EasyPoster(500, 300);
        
        poster.addTextElement("正常文字")
                .setFontSize(25)
                .setColor(Color.red)
                .setPosition(AbsolutePosition.of(Point.of(30, 50)));
        
        poster.asFile("png", "out_text_basic.png");
    }
}
```

**Patterns:**
- Each test method creates an `EasyPoster` instance
- Configures elements with various properties
- Outputs PNG files for visual verification
- No explicit assertions in most tests

## Mocking

**Framework:** None detected
- Tests use real implementations
- No mocking frameworks (Mockito, etc.) in dependencies

**Patterns:**
- Integration/visual testing approach
- Tests generate actual output files

## Test Data

**Fixtures:**
- Inline test data in test methods
- No external fixture files detected
- Reusable test dimensions: 500x300, 500x500 common

**Test Outputs:**
- PNG files generated to project root
- Naming convention: `out_[testclass]_[feature].png`

## Coverage

**Requirements:** Not enforced
- No coverage tools configured (JaCoCo, etc.)

**View Coverage:**
```bash
# No built-in coverage command
# Tests are visual/integration focused
```

## Test Types

**Unit Tests:**
- Limited true unit tests
- `TextSplitterSimpleImplTest.java` appears to be a unit test
- Most tests are integration/visual tests

**Integration Tests:**
- Primary test type
- Tests full rendering pipeline
- Visual output verification
- Examples: `TextBasicTest`, `BarChartBasicTest`, `ImageBasicTest`

**E2E Tests:**
- Not applicable - library project, not application
- Tests focus on individual element rendering

## Common Patterns

**Test Setup:**
```java
@Before
public void setUp() {
    // No common setup detected
    // Each test creates its own EasyPoster instance
}
```

**Async Testing:**
- Not applicable - all operations are synchronous

**Error Testing:**
- Limited error case testing
- Mainly positive path testing

**Visual Testing Pattern:**
```java
@Test
public void testFeature() {
    // 1. Create poster with dimensions
    EasyPoster poster = new EasyPoster(500, 300);
    
    // 2. Add elements with configuration
    poster.addTextElement("Text")
            .setFontSize(25)
            .setColor(Color.red)
            .setPosition(...);
    
    // 3. Render to file for visual inspection
    poster.asFile("png", "output.png");
}
```

**Element-Specific Testing:**
- Each element type has its own test class
- Tests cover basic usage, positioning, styling
- Tests cover special features (rotation, gradients, etc.)

## Test Coverage Areas

**Well-Tested:**
- Basic element rendering (text, images, shapes)
- Positioning (Absolute and Relative)
- Styling (colors, fonts, sizes)
- Charts (Bar, Line, Pie, Funnel)
- Advanced features (gradients, rotation, transparency)

**Limited Testing:**
- Edge cases
- Error conditions
- Performance scenarios
- Invalid inputs

## Test Output Examples

**Generated Files:**
- `out_text_basic.png` - Text rendering tests
- `out_text_global.png` - Global configuration tests
- `BarChartBasicTest` - Chart rendering tests
- `V2TextElementPngTest` - V2 text feature tests

---

*Testing analysis: 2026-04-21*