# V2 TextElement Short-Term Expansion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expand `com.bytefuture.easy.poster.element.v2.TextElement` short-term capabilities so V2 can cover the next missing rich-text and typography scenarios without breaking the current V2 layering.

**Architecture:** Keep the existing V2 split intact: `TextElement` remains a thin entry/coordinator, `TextElementConfig` stores immutable options, `TextLayoutEngine` resolves layout, and `TextRenderer` performs drawing. Short-term work should prefer additive API and measurement/rendering extensions over broad refactors or new dependency introduction.

**Tech Stack:** Java 8, Maven, JUnit 4, AWT `Graphics2D`, existing V2 text layout/rendering stack

---

## File Map

**Primary files to modify**
- `src/main/java/com/bytefuture/easy/poster/model/TextSpan.java`
- `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- `src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java`
- `src/main/java/com/bytefuture/easy/poster/text/html/HtmlTextSpanParser.java`
- `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

**Likely supporting files to modify if the seams require it**
- `src/main/java/com/bytefuture/easy/poster/text/wrap/RichTextWrapper.java`
- `src/main/java/com/bytefuture/easy/poster/text/wrap/RichLine.java`
- `src/main/java/com/bytefuture/easy/poster/text/wrap/RichTextFragment.java`
- `src/main/java/com/bytefuture/easy/poster/text/layout/LayoutLine.java`
- `src/main/java/com/bytefuture/easy/poster/text/layout/VerticalGlyph.java`
- `src/main/java/com/bytefuture/easy/poster/text/metrics/TextMetricsService.java`
- `src/test/java/com/bytefuture/easy/poster/ui/v2/V2TextElementPngTest.java`
- `TEXT_README.md`
- `TEXT_HTML_README.md`

## Delivery Rules

- Preserve the current V2 architecture. Do not move behavior back into `TextElement`.
- Prefer additive behavior over redesign.
- Lock new behavior with focused tests before implementation whenever the current gap is already known.
- Run the narrowest relevant test first, then the broader V2 text suite.
- Keep short-term scope constrained to capabilities that fit the existing model cleanly.
- No new dependencies.

## Phase Order

1. Rich text layout parity gaps
2. `TextSpan` model expansion
3. Unicode-safe rendering/measuring
4. Vertical rich-text MVP
5. HTML parser catch-up
6. Documentation and regression pass

### Task 1: Add Rich Text Justify Support

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/wrap/RichTextWrapper.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/wrap/RichLine.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] **Step 1: Add failing tests for current rich-text justify restriction**

Add tests that replace the current rejection path with expected layout behavior:

```java
@Test
public void shouldJustifyWrappedRichIntermediateLine() {
    TextElement element = TextElement.builder(
            TextSpan.of("alpha "),
            TextSpan.of("beta ").setColor(Color.RED),
            TextSpan.of("gamma delta epsilon zeta")
    )
            .autoWordWrap(120)
            .textAlign(TextAlign.JUSTIFY)
            .build();

    TextLayoutResult layout = measureLayout(element, createContext(), 400, 300);

    assertTrue(layout.getLines().size() >= 2);
    assertTrue(layout.getLines().get(0).getRenderWidth() >= layout.getContentWidth());
}
```

- [ ] **Step 2: Run test to confirm the current failure mode**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest#shouldJustifyWrappedRichIntermediateLine" test
```

Expected:
```text
FAILED
```
with the current `PosterException` about rich text justify not being supported.

- [ ] **Step 3: Implement the minimal rich-text justify layout path**

Recommended direction:

```java
if (resolvedTextAlign == TextAlign.JUSTIFY) {
    // Only justify non-last wrapped lines.
    // Expand space-bearing fragments instead of stretching every glyph.
}
```

Rules:
- Only justify non-final lines.
- Only distribute width across visible spaces.
- Do not justify lines without spaces.
- Preserve fragment order and color/font metadata.

- [ ] **Step 4: Add regression tests for edge cases**

Cover:
- multi-span line with spaces crossing fragment boundaries
- last line stays non-justified
- single-line rich text with justify does not over-stretch

- [ ] **Step 5: Run the narrow test target**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java src/main/java/com/bytefuture/easy/poster/text/wrap/RichTextWrapper.java src/main/java/com/bytefuture/easy/poster/text/wrap/RichLine.java src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java
git commit -m "Unblock justified rich text in V2 text layout

Constraint: Must preserve current V2 layout layering
Rejected: Stretch every glyph in justified rich lines | produces unstable mixed-style spacing
Confidence: medium
Scope-risk: moderate
Directive: Keep justify limited to non-final lines with real spaces unless richer typography rules are added
Tested: V2TextElementTest
"
```

### Task 2: Add Rich Text Auto-Fit Support

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/metrics/TextMetricsService.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] **Step 1: Add failing tests for rich-text auto-fit**

Add tests for whole-block scaling rather than per-span independent scaling:

```java
@Test
public void shouldAutoFitRichTextByScalingAllSpanSizesProportionally() {
    TextElement element = TextElement.builder(
            TextSpan.of("BIG").setFontSize(30),
            TextSpan.of(" / ").setFontSize(18),
            TextSpan.of("small").setFontSize(12)
    )
            .autoFitText(100, 8)
            .build();

    TextLayoutResult layout = measureLayout(element, createContext(), 300, 200);

    assertTrue(layout.getContentWidth() <= 100);
}
```

- [ ] **Step 2: Run the focused failing test**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest#shouldAutoFitRichTextByScalingAllSpanSizesProportionally" test
```

Expected:
```text
FAILED
```
with the current `PosterException` about rich text auto-fit.

- [ ] **Step 3: Implement proportional rich-text scaling**

Recommended shape:

```java
private float resolveRichAutoFitScale(...) {
    // binary search or descending search over a scale factor
}
```

Rules:
- Scale all span font sizes proportionally.
- Respect the configured `autoFitMinFontSize`.
- Recompute line height and baseline after scale resolution.
- Keep relative span size ratios intact.

- [ ] **Step 4: Add tests for minimum size and ratio preservation**

Cover:
- target width reached or best effort at min size
- larger span remains larger after scaling
- line height and baseline remain stable enough for layout assertions

- [ ] **Step 5: Run the V2 text suite**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java src/main/java/com/bytefuture/easy/poster/text/metrics/TextMetricsService.java src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java
git commit -m "Allow proportional auto-fit for V2 rich text

Constraint: Span font-size ratios must remain stable during auto-fit
Rejected: Independent per-span shrink logic | produces unreadable relative emphasis shifts
Confidence: medium
Scope-risk: moderate
Directive: Keep rich auto-fit as whole-block scaling unless a future API explicitly opts into per-span scaling
Tested: V2TextElementTest
"
```

### Task 3: Expand TextSpan Style Surface

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/model/TextSpan.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/wrap/RichTextFragment.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] **Step 1: Add failing tests describing new span-level overrides**

Add tests for:
- span-level `fontName`
- span-level `backgroundColor`
- span-level `shadow`
- span-level `stroke`
- span-level `baselineShift`

Example assertion skeleton:

```java
@Test
public void shouldPreferSpanLevelStyleOverridesOverBlockDefaults() {
    TextElement element = TextElement.builder(
            TextSpan.of("up").setBaselineShift(-4).setColor(Color.RED),
            TextSpan.of("down").setBaselineShift(4).setColor(Color.BLUE)
    )
            .fontSize(20)
            .build();

    BufferedImage image = renderElement(element, 220, 120);

    assertNotNull(image);
}
```

- [ ] **Step 2: Extend `TextSpan` with additive fields and fluent setters**

Recommended fields:

```java
private String fontName;
private Color backgroundColor;
private TextShadow shadow;
private TextStroke stroke;
private Integer baselineShift;
```

Validation:
- reject null for object-based setters where null has no meaning
- reject negative/zero stroke width through existing `TextStroke`

- [ ] **Step 3: Thread the new span state through rich layout/render objects**

If needed, extend `RichTextFragment` so each fragment carries the resolved render state required by the renderer.

Rules:
- span-explicit value overrides block default
- absent span value inherits block default
- do not duplicate values into multiple structures unless the renderer truly needs them

- [ ] **Step 4: Implement span-level rendering for background, shadow, stroke, baseline shift**

Recommended shape:

```java
for (RichTextFragment fragment : fragments) {
    int fragmentBaselineY = startY + fragment.getBaselineShift();
    // draw fragment background -> shadow -> stroke -> fill -> decoration
}
```

- [ ] **Step 5: Run the focused and broader text tests**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/bytefuture/easy/poster/model/TextSpan.java src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java src/main/java/com/bytefuture/easy/poster/text/wrap/RichTextFragment.java src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java
git commit -m "Broaden V2 TextSpan styling for richer inline control

Constraint: New span styles must inherit cleanly from block-level defaults
Rejected: Separate parallel span-style config object | adds indirection without clear short-term payoff
Confidence: medium
Scope-risk: moderate
Directive: Keep span additions additive and render-focused; avoid turning TextSpan into a second full block config
Tested: V2TextElementTest
"
```

### Task 4: Make Letter-Spacing Rendering Unicode-Safe

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/metrics/TextMetricsService.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] **Step 1: Add failing tests for emoji and surrogate-pair-safe rendering**

Cover:
- emoji text with letter spacing
- combined character text with letter spacing
- stroke and shadow paths still render without fragmenting the character sequence

Example:

```java
@Test
public void shouldRenderEmojiWithoutBreakingSurrogatePairsWhenLetterSpacingIsEnabled() {
    TextElement element = TextElement.builder("A🙂B")
            .letterSpacing(6)
            .stroke(Color.BLACK, 1f)
            .build();

    BufferedImage image = renderElement(element, 220, 120);

    assertNotNull(image);
}
```

- [ ] **Step 2: Replace `char` iteration in renderer helpers**

Recommended shape:

```java
private List<String> splitRenderableUnits(String text) {
    List<String> units = new ArrayList<String>();
    for (int i = 0; i < text.length();) {
        int codePoint = text.codePointAt(i);
        units.add(new String(Character.toChars(codePoint)));
        i += Character.charCount(codePoint);
    }
    return units;
}
```

- [ ] **Step 3: Align measurement logic with render-unit splitting**

Any code path that adds spacing per glyph must use the same unit-splitting rule as the renderer.

- [ ] **Step 4: Run focused then broader tests**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java src/main/java/com/bytefuture/easy/poster/text/metrics/TextMetricsService.java src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java
git commit -m "Make V2 letter-spacing rendering safer for Unicode text

Constraint: Measuring and drawing must use the same render-unit logic
Rejected: Renderer-only surrogate fix | would leave width calculations inconsistent
Confidence: medium
Scope-risk: narrow
Directive: If future grapheme-cluster support is added, update both measure and draw paths together
Tested: V2TextElementTest
"
```

### Task 5: Add Vertical Rich Text MVP

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/layout/LayoutLine.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/text/layout/VerticalGlyph.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] **Step 1: Add failing tests for vertical rich-text scenarios**

Cover:
- single-column vertical rich text
- multi-column vertical rich text
- left-to-right and right-to-left column flow
- top/middle/bottom vertical align

Example:

```java
@Test
public void shouldLayoutVerticalRichTextColumns() {
    TextElement element = TextElement.builder(
            TextSpan.of("春").setColor(Color.RED),
            TextSpan.of("夏").setColor(Color.GREEN),
            TextSpan.of("秋冬").setColor(Color.BLUE)
    )
            .textLayoutMode(TextLayoutMode.VERTICAL)
            .layoutHeight(48)
            .columnSpacing(8)
            .build();

    TextLayoutResult layout = measureLayout(element, createContext(), 300, 300);

    assertTrue(layout.getLines().size() >= 1);
}
```

- [ ] **Step 2: Define the MVP rules before implementation**

Rules:
- support span-level color, font size, font style, underline, strike-through
- reuse existing vertical column model where possible
- do not add full span-level vertical background/shadow/stroke in this task
- keep unsupported combinations explicit if they remain out of scope

- [ ] **Step 3: Implement vertical rich layout data generation**

Recommended direction:
- flatten rich spans into vertical glyph units carrying resolved style
- reuse current column splitting and vertical direction logic
- resolve glyph widths using span-resolved fonts

- [ ] **Step 4: Implement vertical rich rendering**

Recommended direction:

```java
if (line.hasVerticalGlyphs()) {
    // draw per vertical glyph using glyph-resolved color/font
}
```

- [ ] **Step 5: Run functional tests**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 6: Add or update a PNG visual case**

Run:
```powershell
mvn -q "-Dtest=V2TextElementPngTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java src/main/java/com/bytefuture/easy/poster/element/v2/TextRenderer.java src/main/java/com/bytefuture/easy/poster/text/layout/LayoutLine.java src/main/java/com/bytefuture/easy/poster/text/layout/VerticalGlyph.java src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java src/test/java/com/bytefuture/easy/poster/ui/v2/V2TextElementPngTest.java
git commit -m "Introduce MVP vertical rich-text support in V2

Constraint: Must fit the existing vertical column model without a wide refactor
Rejected: Full vertical rich typography feature set in one pass | too broad for short-term delivery
Confidence: low
Scope-risk: moderate
Directive: Keep this task to minimum viable styled vertical glyph layout and rendering
Tested: V2TextElementTest, V2TextElementPngTest
"
```

### Task 6: Expand HTML Parser To Match New Span Capabilities

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/text/html/HtmlTextSpanParser.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/model/TextSpan.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`
- Modify if needed: `TEXT_HTML_README.md`

- [ ] **Step 1: Add failing parser tests**

Cover:
- `font-family`
- `background-color`
- `sup` and `sub`
- `small`
- `code`

Example:

```java
@Test
public void shouldParseHtmlBackgroundAndBaselineRelatedTagsIntoSpans() {
    TextElement element = TextElement.builderHtml(
            "<span style='background-color:#ffeeaa'>A</span><sup>2</sup><sub>i</sub>"
    ).build();

    TextLayoutResult layout = measureLayout(element, createContext(), 300, 200);

    assertTrue(layout.getLines().size() >= 1);
}
```

- [ ] **Step 2: Extend parser state only for features the model can represent**

Recommended additions:

```java
private final Color backgroundColor;
private final String fontName;
private final Integer baselineShift;
```

Tag mapping:
- `sup` => negative baseline shift + reduced font size
- `sub` => positive baseline shift + reduced font size
- `small` => reduced font size
- `code` => fixed-width font family where available

- [ ] **Step 3: Merge adjacent spans only when the expanded style set matches**

Do not keep the old merge rule if it ignores newly added span state.

- [ ] **Step 4: Run parser and broader V2 text tests**

Run:
```powershell
mvn -q "-Dtest=V2TextElementTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] **Step 5: Update HTML support documentation**

Document the newly supported tags/styles in:
- `TEXT_HTML_README.md`

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/bytefuture/easy/poster/text/html/HtmlTextSpanParser.java src/main/java/com/bytefuture/easy/poster/model/TextSpan.java src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java TEXT_HTML_README.md
git commit -m "Align V2 HTML parsing with expanded span styling

Constraint: Parser should only emit styles the V2 span/render model can actually draw
Rejected: Broad CSS parsing support | exceeds short-term scope and model shape
Confidence: medium
Scope-risk: narrow
Directive: Extend parser support only when there is a concrete render path and regression coverage
Tested: V2TextElementTest
"
```

## Final Verification Pass

**Files:**
- Modify as needed: `TEXT_README.md`
- Modify as needed: `TEXT_HTML_README.md`
- Verify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`
- Verify: `src/test/java/com/bytefuture/easy/poster/ui/v2/V2TextElementPngTest.java`

- [ ] Run the full short-term verification suite:

```powershell
mvn -q "-Dtest=V2TextElementTest,V2TextElementPngTest" test
```

Expected:
```text
BUILD SUCCESS
```

- [ ] Confirm the following are true:
  - rich text no longer rejects `JUSTIFY`
  - rich text no longer rejects `autoFitText`
  - span-level style overrides render correctly
  - Unicode-safe spacing paths do not break emoji or surrogate pairs
  - vertical rich-text MVP renders without regressing plain vertical text
  - HTML examples reflect supported output truthfully

- [ ] Update `TEXT_README.md` and `TEXT_HTML_README.md` only where API/examples changed materially.

- [ ] Create a final integration commit.

```bash
git add TEXT_README.md TEXT_HTML_README.md
git commit -m "Document short-term V2 text expansion capabilities

Constraint: Documentation must describe only verified capabilities
Confidence: high
Scope-risk: narrow
Directive: Keep README examples aligned with tested APIs and behaviors
Tested: V2TextElementTest, V2TextElementPngTest
"
```

## Acceptance Criteria

- V2 rich text supports `JUSTIFY`.
- V2 rich text supports proportional `autoFitText`.
- `TextSpan` can express the short-term style additions required by rendering and HTML parsing.
- Letter-spacing drawing no longer splits surrogate pairs.
- V2 supports a minimum viable vertical rich-text path.
- HTML parsing covers the newly supported short-term span styles.
- Functional and PNG regression coverage exists for every new short-term feature.

## Out Of Scope

- Full paragraph engine redesign
- Locale-aware line breaking
- Advanced East Asian vertical typography rules
- Grapheme-cluster-perfect segmentation for every Unicode edge case
- Path text / curved text / texture text
- Template serialization and editor integration
