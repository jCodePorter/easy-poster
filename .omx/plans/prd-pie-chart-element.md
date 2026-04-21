# PRD - Pie Chart Element for easy-poster

## Metadata
- Date: 2026-04-13
- Source spec: `.omx/specs/deep-interview-pie-chart-element.md`
- Planning mode: ralplan consensus (local fallback due subagent channel unavailability)
- Scope risk: moderate

## RALPLAN-DR Summary

### Principles
1. Reuse existing `BarChartElement` API and builder conventions before inventing new abstractions.
2. Keep v1 focused on confirmed requirements: pie, donut, and rose modes; legend/content/color configurability.
3. Make defaults readable: usable out-of-box legend/layout/color behavior without extra configuration.
4. Preserve Java 8 compatibility and current test organization.
5. Add comments to variables, properties, and methods in newly introduced or modified implementation code.

### Decision Drivers
1. API consistency with current chart entry points and fluent setters.
2. Minimal-diff extensibility for future chart variants without fragmenting the public API.
3. Testability of rendering behavior for mode switching, legend behavior, and color fallback.

### Viable Options

#### Option A — Single `PieChartElement` + `PieChartSlice` + mode enum (**Chosen**)
- Pros:
  - Closest to current `BarChartElement` usage style.
  - Keeps legend/color/title/layout logic in one place.
  - Lowest public API surface while still supporting three visual modes.
- Cons:
  - One class will hold multiple rendering branches.
  - Requires care to avoid overly long render logic.

#### Option B — Separate `PieChartElement`, `DonutChartElement`, `RoseChartElement`
- Pros:
  - Simpler per-class rendering branches.
  - Easier class-level mental model for each chart type.
- Cons:
  - Duplicates legend/layout/color handling.
  - Expands public API and diverges from the confirmed user preference.

#### Option C — Generic polar-chart base abstraction first
- Pros:
  - Could reduce long-term duplication if many polar charts are planned.
  - Provides a formal extension point.
- Cons:
  - Premature abstraction for a repo that currently has only one chart family.
  - Adds complexity and delivery risk with little v1 value.

### Chosen Option
Choose **Option A**. It best matches the confirmed requirement for one element with mode switching and minimizes unnecessary abstraction.

### Invalidated Alternatives
- Option B invalidated because the user explicitly confirmed one element with mode switching.
- Option C invalidated because the repo does not yet justify a new abstraction layer.

## ADR
- **Decision:** Implement pie support as `PieChartElement` plus `PieChartSlice`, with a `PieChartMode` enum (`PIE`, `DONUT`, `ROSE`) and `EasyPoster.addPieChartElement(int width, int height)`.
- **Drivers:** API consistency, controlled scope, maintainable rendering structure, verifiable defaults.
- **Alternatives considered:** separate element classes; generic polar-chart base abstraction.
- **Why chosen:** Lowest API churn, aligns with clarified decision boundaries, keeps legend/color logic centralized.
- **Consequences:** `PieChartElement` must be internally well-factored with helper methods to avoid monolithic rendering logic.
- **Follow-ups:** If more polar chart variants appear later, extract shared polar layout helpers based on real duplication.

## Product Outcome
Allow poster generation code to render proportion-oriented charts with fluent Java APIs, including standard pie, donut, and rose charts, while preserving configurable legend and label content plus predictable color behavior.

## User Stories
1. As a poster builder, I can create a pie chart element from `EasyPoster` the same way I create a bar chart element.
2. As a caller, I can add named slices with values and optional custom colors.
3. As a caller, I can switch between pie, donut, and rose modes using one element.
4. As a caller, I can configure legend and label display content independently.
5. As a caller, I can rely on default palette fallback when slice colors are not provided.

## In Scope
- `PieChartElement`
- `PieChartSlice`
- `PieChartMode` enum
- Legend display mode and label display mode enums
- Palette override and per-slice custom color
- Top legend with wrap behavior
- Label auto-hide under insufficient space
- EasyPoster entry point
- Tests for the above
- Comments on variables/properties/methods

## Out of Scope
- 3D, animation, interaction
- gradient/texture/transparency layering/color derivation
- advanced smart label routing beyond simple auto-hide
- cross-chart abstraction extraction beyond what v1 requires

## Implementation Plan

### Phase 1 — Model and API surface
**Files**
- `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartElement.java` (new)
- `src/main/java/com/bytefuture/easy/poster/element/chart/PieChartSlice.java` (new)
- `src/main/java/com/bytefuture/easy/poster/EasyPoster.java` (modify)

**Work**
- Add `EasyPoster.addPieChartElement(int width, int height)`.
- Introduce `PieChartSlice` with `name`, normalized `value`, optional `color`, fluent setter for color, static factory.
- Introduce `PieChartElement` with width/height constructor and fluent setters consistent with `BarChartElement` naming.
- Add nested or top-level enums for:
  - `PieChartMode` (`PIE`, `DONUT`, `ROSE`)
  - `LegendDisplayMode`
  - `LabelDisplayMode`
- Confirm minimum configuration surface:
  - `title`
  - `padding`
  - `showLegend`
  - `showTitle`
  - `showLabel`
  - `mode`
  - `palette`
  - legend sizing/text color settings
  - donut inner-radius ratio / rose radius scaling knobs
  - slice collection mutators (`setSlices`, `addSlice` overloads)

### Phase 2 — Layout and rendering pipeline
**Work**
- Reuse `BarChartElement` palette values as the initial default palette for visual consistency.
- Structure rendering into helper methods inside `PieChartElement`:
  - `render()` orchestration
  - title layout
  - legend layout and draw
  - drawable plot bounds calculation
  - slice angle/radius calculation
  - mode-specific geometry for pie/donut/rose
  - label content formatting
  - label draw / auto-hide checks
  - color resolution
- Default legend behavior:
  - enabled
  - top-aligned
  - horizontal flow
  - wrap on overflow
- Default label behavior:
  - enabled
  - draw inside slice when readable
  - auto-hide when angle/radius/space are below threshold
- Ensure legend marker color always uses the resolved slice color.

### Phase 3 — Validation and edge handling
**Work**
- Validate empty slices / null names / null values / zero-total data behavior.
- Decide and document how non-positive values are treated:
  - recommended v1 rule: ignore non-positive slices with validation or skip strategy documented in comments/tests.
- Guard mode-specific invalid config (e.g. donut inner radius ratio bounds).
- Keep error messages consistent with existing `PosterException` style.

### Phase 4 — Tests and examples
**Files**
- `src/test/java/com/bytefuture/easy/poster/ui/chart/PieChartBasicTest.java` (new)
- optionally `src/test/java/com/bytefuture/easy/poster/func/chart/...` if lower-level behavior tests are warranted

**Tests**
- basic pie chart renders
- basic donut chart renders
- basic rose chart renders
- custom slice colors override palette
- palette fallback works when colors absent
- legend content mode changes output content path
- label content mode changes output content path
- legend wrap path for many slices
- label auto-hide path for tight geometry
- EasyPoster entry point composes correctly

### Phase 5 — Polish and verification
**Work**
- Verify new code is commented as required.
- Run targeted tests, then full relevant Maven test subset if affordable.
- Review public API names for parity with existing chart naming.

## Technical Design Notes
- Prefer local helper methods over introducing a new abstract polar chart base in v1.
- If `PieChartElement` grows too large, extract private static helper value objects only when it reduces complexity without widening API.
- Keep display-mode formatting centralized to avoid divergence between legend and labels.
- Reuse `DecimalFormat` style similar to `BarChartElement` for values/percentages.

## Risks and Mitigations
1. **Risk:** One element class becomes too complex.
   - **Mitigation:** Pre-split render flow into cohesive private helpers from the start.
2. **Risk:** Label placement becomes fragile for small slices.
   - **Mitigation:** Prefer deterministic auto-hide thresholds rather than clever routing in v1.
3. **Risk:** Rose chart semantics drift from pie/donut assumptions.
   - **Mitigation:** Isolate radius calculation as a mode-specific step and keep shared legend/title/layout separate.
4. **Risk:** Visual regressions are hard to assert.
   - **Mitigation:** Keep tests focused on rendered output generation plus targeted pixel/color heuristics where practical.

## Concrete Acceptance Criteria
- `EasyPoster` exposes `addPieChartElement(int width, int height)`.
- Callers can create slices with `name`, `value`, and optional `color`.
- One element supports `PIE`, `DONUT`, and `ROSE` modes.
- Legend content and slice-label content are independently configurable.
- Legend defaults to top horizontal layout with wrapping.
- Resolved slice color equals legend marker color.
- Palette fallback works deterministically.
- Labels may auto-hide when insufficient space exists.
- New/modified variables, properties, and methods are commented.
- Added tests cover the three modes plus color/legend/label behavior.

## Verification Plan
- Compile/test through Maven (`mvn test` or targeted chart tests).
- Inspect generated output files from chart UI tests for the three modes.
- Confirm no existing bar-chart API behavior is changed.
- Review code comments coverage in new/modified files.

## Staffing Guidance
### Available agent types roster
- `executor`: implementation
- `architect`: design review
- `critic`: plan quality challenge
- `test-engineer`: test strategy/refinement
- `verifier`: completion evidence
- `code-reviewer`: final review

### If executing with `$ralph`
- Suggested lanes:
  - leader/executor: high
  - verifier: medium/high
- Sequence:
  1. API/model files
  2. render pipeline
  3. tests
  4. verification

### If executing with `$team`
- Suggested staffing:
  - Lane 1 (`executor`, high): `PieChartElement` rendering/layout
  - Lane 2 (`executor`, medium/high): `PieChartSlice`, enums, `EasyPoster` entry point
  - Lane 3 (`test-engineer`, medium): chart tests and output verification helpers
  - Final verification (`verifier`, high): run tests and review acceptance criteria
- Shared-file caution:
  - `PieChartElement.java` should have single-owner write control.

## Launch Hints
- Ralph handoff: `$ralph .omx/specs/deep-interview-pie-chart-element.md`
- Team handoff: `$team .omx/specs/deep-interview-pie-chart-element.md`
- Team verification path:
  1. run chart tests
  2. inspect generated chart outputs
  3. confirm comments added
  4. confirm no non-goal features slipped in

## Open Implementation Decisions (safe for executor)
- Whether enums live nested inside `PieChartElement` or as sibling types.
- Exact threshold constants for label auto-hide.
- Exact field names for rose radius scaling, as long as semantics remain documented and builder-style.
