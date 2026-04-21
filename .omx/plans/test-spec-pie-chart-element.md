# Test Specification - Pie Chart Element for easy-poster

## Scope
This test spec covers the new `PieChartElement` feature set defined in `.omx/specs/deep-interview-pie-chart-element.md` and PRD `.omx/plans/prd-pie-chart-element.md`.

## Test Principles
1. Verify public behavior before pixel-perfect aesthetics.
2. Cover each confirmed mode: pie, donut, rose.
3. Validate legend/label content configuration and color resolution behavior.
4. Keep tests compatible with current Maven + Java 8 setup.

## Test Matrix

### Unit / behavior-level checks
1. `PieChartSlice` normalizes numeric input and stores optional color.
2. `PieChartElement` accepts fluent configuration without breaking builder chaining.
3. Palette resolution returns slice custom color first, then palette fallback.
4. Legend formatter returns expected strings for each `LegendDisplayMode`.
5. Label formatter returns expected strings for each `LabelDisplayMode`.
6. Invalid config throws `PosterException` with clear messages:
   - invalid donut ratio
   - missing/empty slice data when rendering
   - unsupported/invalid rose scale parameters
7. Non-positive or zero-total data follows the documented v1 rule.

### UI/render tests
1. **Pie render basic**
   - create poster
   - add pie chart
   - set title, slices, default legend
   - export PNG successfully
2. **Donut render basic**
   - configure donut mode with inner radius
   - export PNG successfully
3. **Rose render basic**
   - configure rose mode and varying values
   - export PNG successfully
4. **Custom colors**
   - use explicit slice colors
   - verify rendered output contains color-like pixels close to those colors
5. **Palette fallback**
   - omit slice colors
   - verify output contains colors close to default palette values
6. **Legend content mode**
   - render with name-only and name+percent variants
   - verify no exception and expected layout path exercised
7. **Label content mode**
   - render with value / percent / name+percent variants
   - verify no exception and label formatter path exercised
8. **Legend wrapping**
   - many slices, narrow width
   - verify rendering succeeds and wrap path is exercised
9. **Label auto-hide**
   - create many small slices or narrow plot area
   - verify rendering succeeds without overlap-related failure and hidden-label path is exercised
10. **EasyPoster integration**
   - `addPieChartElement(int, int)` returns configured element and participates in render pipeline

## Suggested Test Files
- `src/test/java/com/bytefuture/easy/poster/ui/chart/PieChartBasicTest.java`
- optional helper assertions colocated in same test package to avoid unnecessary shared abstraction

## Test Data Sets
### Dataset A — standard pie
- slices: 渠道A 42, 渠道B 28, 渠道C 18, 其他 12

### Dataset B — donut
- slices: 已完成 68, 进行中 22, 风险 10

### Dataset C — rose
- slices: 华东 88, 华南 66, 华北 42, 西南 25, 东北 18

### Dataset D — wrap stress
- 8~10 slices with medium-length names

### Dataset E — small-slice stress
- 10+ tiny slices to force label auto-hide

## Verification Steps
1. Run targeted tests for chart package.
2. If test suite writes PNG outputs, confirm expected files are generated.
3. Inspect outputs for:
   - correct mode silhouette (pie vs donut vs rose)
   - legend presence and approximate placement
   - color consistency between slices and legend markers
4. Run broader Maven tests if targeted chart tests pass.
5. Review comments in new/modified files.

## Exit Criteria
- All targeted pie-chart tests pass.
- No existing chart tests regress.
- Acceptance criteria from PRD are matched by tests or explicit verification steps.
- Comments are present on newly added/modified variables, properties, and methods.

## Risks / Gaps
- Render correctness may not be fully assertable by unit tests alone; visual inspection of generated outputs remains part of verification.
- Label auto-hide is heuristic; tests should validate stable execution and path coverage rather than exact pixel placement.
