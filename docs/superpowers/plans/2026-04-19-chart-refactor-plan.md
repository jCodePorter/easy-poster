# Chart Package Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Reduce duplication and extension cost in `element.chart` without changing public chart behavior or the existing `EasyPoster` API.

**Architecture:** Refactor in small phases. First lock current behavior with calculation-focused regression tests, then extract shared chart infrastructure, then migrate `pie`/`funnel`, and finally split the oversized `bar` implementation into focused collaborators. Keep public constructors and fluent setter APIs stable.

**Tech Stack:** Java 8, JUnit 4, AWT `Graphics2D`, existing `EasyPoster` rendering pipeline

---

## Target File Structure

**Existing files to modify**
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/LineChartElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/FunnelChartElement.java`
- `src/test/java/com/bytefuture/easy/poster/ui/chart/BarChartBasicTest.java`
- `src/test/java/com/bytefuture/easy/poster/ui/chart/LineChartBasicTest.java`
- `src/test/java/com/bytefuture/easy/poster/ui/chart/PieChartBasicTest.java`
- `src/test/java/com/bytefuture/easy/poster/ui/chart/FunnelChartBasicTest.java`

**New files expected**
- `src/main/java/com/bytefuture/easy/poster/element/chart/AbstractChartElement.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/ChartStyle.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/ChartTextSupport.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/ChartLegendRenderer.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/ChartLayoutBox.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/ChartValueRange.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/ChartDataPoint.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/NamedColorValue.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartLayoutCalculator.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartRangeResolver.java`
- `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartLabelRenderer.java`

**Deliberately out of scope for this refactor**
- No new chart types
- No public API redesign in `EasyPoster`
- No dependency changes
- No visual redesign beyond parity-safe bug fixes

---

### Task 1: Lock Current Behavior With Stable Regression Tests

**Files:**
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/BarChartBasicTest.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/LineChartBasicTest.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/PieChartBasicTest.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/FunnelChartBasicTest.java`

- [x] Add focused tests for non-visual calculations that the refactor will move out of element classes.
- [x] Prefer asserting numeric outcomes and exception messages over image snapshots.
- [x] Cover at least these cases:
  - Bar chart value range: grouped, stacked, percent-stacked, mixed positive/negative.
  - Line chart axis baseline: all positive, all negative, mixed values.
  - Pie chart filtering: null slice, zero/negative slice, palette fallback, percentage calculation.
  - Funnel chart percentage calculation, max stage width basis, external label trigger threshold.
- [x] Where logic is currently private, first extract package-private helpers in later tasks only after the tests identify the exact calculation seams to preserve.
- [x] Run targeted chart tests.

Run:
```powershell
./mvnw.cmd -q -Dtest=BarChartBasicTest,LineChartBasicTest,PieChartBasicTest,FunnelChartBasicTest test
```

Expected:
```text
BUILD SUCCESS
```

- [x] Commit after tests are in place.

Suggested commit message:
```text
Protect chart refactor with calculation-focused regression tests

Constraint: Refactor must preserve existing chart rendering behavior
Confidence: high
Scope-risk: narrow
Directive: Extend numeric/layout tests before moving more private chart logic
Tested: Chart unit tests
```

### Task 2: Extract Shared Chart Base Infrastructure

**Files:**
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/AbstractChartElement.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/ChartStyle.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/ChartLayoutBox.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/ChartTextSupport.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/LineChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/FunnelChartElement.java`

- [x] Introduce `AbstractChartElement<T>` under the existing `AbstractDimensionElement<T>` hierarchy.
- [x] Move only the truly shared pieces first:
  - base font resolution from `PosterContext`
  - background fill helper
  - default padding
  - standard label/title/legend colors
  - brightness-based readable text color helper
  - common inner box creation
- [x] Keep subclass hooks explicit. Minimum hooks should look like:
```java
protected abstract void validateChartData();
protected abstract void renderChart(Graphics2D g, PosterContext context, ChartLayoutBox box);
```
- [x] Do not move axis-specific or polar-specific behavior into the base class in this step.
- [x] Update all four chart elements to extend the new base class while preserving current public setters.
- [x] Re-run all chart tests.

Run:
```powershell
./mvnw.cmd -q -Dtest=BarChartBasicTest,LineChartBasicTest,PieChartBasicTest,FunnelChartBasicTest test
```

Expected:
```text
BUILD SUCCESS
```

- [x] Commit after shared base extraction is green.

Suggested commit message:
```text
Centralize shared chart element infrastructure

Constraint: Public chart fluent API must remain unchanged
Rejected: Move all chart layout rules into the base class | would over-generalize too early
Confidence: medium
Scope-risk: moderate
Directive: Keep the base class limited to proven cross-chart behavior
Tested: Chart unit tests
```

### Task 3: Extract Shared Legend And Text Formatting Support

**Files:**
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/ChartLegendRenderer.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/NamedColorValue.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/FunnelChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/LineChartElement.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/PieChartBasicTest.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/FunnelChartBasicTest.java`

- [x] Extract repeated title/legend row measurement and wrapping logic into `ChartLegendRenderer`.
- [x] Represent legend items with a tiny shared value object:
```java
public class NamedColorValue {
    private final String name;
    private final Color color;
    private final String displayText;
}
```
- [x] Keep pie/funnel display mode enums local for now, but route their final legend text through the shared renderer.
- [x] Migrate bar/line legend drawing only if it remains a net deletion. If migration increases adapter code, leave them for a later pass.
- [x] Add or update tests that assert long legend content still renders without exceptions after extraction.
- [x] Re-run pie/funnel tests, then full chart tests.

Run:
```powershell
./mvnw.cmd -q -Dtest=PieChartBasicTest,FunnelChartBasicTest test
./mvnw.cmd -q -Dtest=BarChartBasicTest,LineChartBasicTest,PieChartBasicTest,FunnelChartBasicTest test
```

Expected:
```text
BUILD SUCCESS
```

- [x] Commit after legend extraction stabilizes.

Suggested commit message:
```text
Remove duplicated chart legend rendering logic

Constraint: Legend layout must preserve current wrapping behavior
Confidence: medium
Scope-risk: moderate
Directive: Do not force chart-specific display modes into a single enum
Tested: Pie/funnel and full chart tests
```

### Task 4: Normalize Simple Chart Data Objects

**Files:**
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/ChartDataPoint.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartSlice.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/FunnelChartStage.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/FunnelChartElement.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/PieChartBasicTest.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/FunnelChartBasicTest.java`

- [x] Introduce a small shared base object for `name + numeric value + optional color`.
- [x] Make `PieChartSlice` and `FunnelChartStage` extend or delegate to that shared object without changing current static factory methods.
- [x] Do not merge bar/line series into this type. They are list-based, not point-based.
- [x] Preserve current constructors and `of(...)` helpers for source compatibility.
- [x] Re-run pie/funnel tests.

Run:
```powershell
./mvnw.cmd -q -Dtest=PieChartBasicTest,FunnelChartBasicTest test
```

Expected:
```text
BUILD SUCCESS
```

- [x] Commit after data object cleanup.

Suggested commit message:
```text
Deduplicate simple chart point models

Constraint: Keep existing chart factory methods source-compatible
Confidence: medium
Scope-risk: narrow
Directive: Do not collapse series-based and point-based chart models into one abstraction
Tested: Pie/funnel tests
```

### Task 5: Split Bar Chart Calculation And Label Responsibilities

**Files:**
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/ChartValueRange.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartRangeResolver.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartLayoutCalculator.java`
- Create: `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartLabelRenderer.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/BarChartBasicTest.java`

- [x] Extract bar chart value range resolution from `BarChartElement` into `BarChartRangeResolver`.
- [x] Extract bar width/category gap/bar gap computation into `BarChartLayoutCalculator`.
- [x] Extract stacked label and external label placement into `BarChartLabelRenderer`.
- [x] Reduce `BarChartElement` to orchestration:
  - validate
  - create plot area
  - ask collaborators for range/layout/labels
  - draw bars
- [x] Keep bar-specific drawing in the element class for this iteration. Do not create a renderer swarm of tiny classes.
- [x] Add focused tests for moved calculations before deleting old code paths.
- [x] Re-run bar chart tests.

Run:
```powershell
./mvnw.cmd -q -Dtest=BarChartBasicTest test
```

Expected:
```text
BUILD SUCCESS
```

- [x] Commit once bar responsibilities are split and tests pass.

Suggested commit message:
```text
Shrink bar chart element by extracting calculation collaborators

Constraint: Bar chart supports grouped, stacked, and percent-stacked modes in one API
Rejected: Full renderer pipeline rewrite | too much risk for one step
Confidence: medium
Scope-risk: moderate
Directive: Keep BarChartElement as the public entrypoint and orchestration layer
Tested: Bar chart tests
```

### Task 6: Align Line Chart With Shared Range/Layout Types

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/LineChartElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/LinePathBuilderFactory.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/LineChartBasicTest.java`

- [x] Reuse `ChartValueRange` in line chart so value-domain logic is not duplicated with bar chart.
- [x] Keep `LinePathBuilder` strategy design. It is already the best-structured part of the package.
- [x] If useful, simplify `LinePathBuilderFactory.resolve(...)` to depend on a clearer input contract, but do not change public chart setters.
- [x] Confirm zero-tension fallback and monotone/bezier behavior still pass.
- [x] Re-run line chart tests.

Run:
```powershell
./mvnw.cmd -q -Dtest=LineChartBasicTest test
```

Expected:
```text
BUILD SUCCESS
```

- [x] Commit after line chart alignment.

Suggested commit message:
```text
Align line chart calculations with shared chart primitives

Constraint: Preserve existing smoothing behavior and API
Confidence: high
Scope-risk: narrow
Directive: Keep path-building strategy separate from chart orchestration
Tested: Line chart tests
```

### Task 7: Final Cleanup And Full Verification

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/chart/*.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/ui/chart/*.java`
- Modify: `docs/superpowers/plans/2026-04-19-chart-refactor-plan.md`

- [x] Remove dead private helpers replaced by shared classes.
- [x] Normalize naming so the package reads consistently:
  - `validateData` vs `validateConfig`
  - `LayoutBox` vs `ChartLayoutBox`
  - `ValueRange` vs `ChartValueRange`
- [x] Ensure no moved helper duplicates remain in two places.
- [x] Run the full project verification sequence required by workspace guidance.

Run:
```powershell
./mvnw.cmd test
```

Expected:
```text
BUILD SUCCESS
```

- [x] If the project has additional static checks, run them as well and record the result in the final report.
- [x] Update this plan with any scope decisions made during implementation.
- [x] Commit final cleanup.

Suggested commit message:
```text
Complete chart package refactor with shared infrastructure

Constraint: Refactor must stay behavior-preserving and dependency-free
Confidence: medium
Scope-risk: moderate
Directive: Add new chart types on top of the shared base instead of copying an existing element
Tested: Full Maven test suite
Not-tested: Manual visual diff across all generated sample images
```

---

## Risks And Guardrails

- Biggest risk: abstracting too early and pushing chart-specific behavior into the shared base.
- Second risk: relying only on image-generation smoke tests; keep numeric/layout tests in front.
- Third risk: mixing public API cleanup with internal restructuring. Defer API redesign.

## Implementation Notes

- During execution, `BarChartElement` proved encoding-fragile, so bar-chart cleanup was kept to small reversible edits instead of broad regex-style rewrites.
- `PieChartElement` and `FunnelChartElement` now standardize on `ChartLayoutBox`; their old private `LayoutBox` tail classes were removed as dead code rather than adapted.

## Completion Criteria

- Shared chart base exists and is used by all four chart types.
- Legend/title/text support is no longer duplicated in multiple classes.
- `BarChartElement` is materially smaller and delegates range/layout/label logic.
- Existing chart tests pass.
- Full Maven test suite passes.
- No public `EasyPoster` chart factory method changes.
