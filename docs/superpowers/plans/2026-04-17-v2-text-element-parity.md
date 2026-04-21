# V2 Text Element Parity Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `com.bytefuture.easy.poster.element.v2.TextElement` reach practical feature parity with `com.bytefuture.easy.poster.element.basic.EnhanceTextElement` for public API, default behavior, and verification coverage.

**Architecture:** Keep the existing V2 split (`TextElementConfig` + `TextLayoutEngine` + `TextRenderer`) intact. Close parity gaps with the smallest possible surface changes first, then backfill behavior tests by porting the existing `EnhanceTextElementTest` matrix into V2-focused tests.

**Tech Stack:** Java 8, Maven, JUnit 4, existing poster text layout/rendering stack

---

## File Map

- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/README.md`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/V2Examples.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`
- Create if needed: `src/test/java/com/bytefuture/easy/poster/ui/v2/V2TextElementCompatibilityTest.java`

## Delivery Rules

- Preserve the current V2 architecture. Do not fold behavior back into `TextElement`.
- Prefer API additions over refactors.
- Keep parity work reversible and small.
- Add tests before behavior changes whenever the gap is already known and reproducible.
- After each task, run only the narrowest relevant test target first, then the broader V2 text suite.

### Task 1: Add explicit layout width parity

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Add a public V2 builder API for explicit width constraint, using one canonical name and one compatibility alias.
  Recommended shape:
  - `TextElementConfig.Builder.layoutWidth(int width)`
  - `TextElement.Builder.layoutWidth(int width)`
  - optional alias `maxTextWidth(int width)` only if needed for README compatibility

- [ ] Make the new API write into the existing `maxTextWidth` field instead of adding a second width field.

- [ ] Add failing tests that prove single-line width-constrained `ELLIPSIS` and `CLIP` work without enabling `autoWordWrap`.
  Cover:
  - constrained ellipsis width equals requested width
  - constrained clip width equals requested width
  - width-constrained line stays single-line when wrap is not enabled

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - V2 can express the V1 `setLayoutWidth(...)` use case directly.
  - No new width-related field duplication exists.

### Task 2: Restore default text alignment inference

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Replace the hard-coded default `TextAlign.LEFT` behavior with “unset unless explicitly provided”.

- [ ] Add a V2-side resolver matching V1 behavior:
  - `CENTER`, `TOP_CENTER`, `BOTTOM_CENTER` => `TextAlign.CENTER`
  - `TOP_RIGHT`, `RIGHT_CENTER`, `RIGHT_BOTTOM` => `TextAlign.RIGHT`
  - everything else => `TextAlign.LEFT`

- [ ] Ensure explicit `textAlign(...)` still overrides inferred alignment.

- [ ] Add failing tests for:
  - relative center infers center alignment
  - relative right-side position infers right alignment
  - explicit alignment overrides inferred alignment

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - Relative-position defaults match V1 behavior.
  - Existing explicit alignment tests still pass.

### Task 3: Expose missing builder methods already supported by config

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Add pass-through methods to `TextElement.Builder` for:
  - `shadow(TextShadow shadow)`
  - `stroke(TextStroke stroke)`
  - `textBackgroundArc(int arcWidth, int arcHeight)`
  - `textSpans(List<TextSpan> spans)`

- [ ] Add focused tests that inspect the built config and confirm values are preserved.

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - V2 builder no longer forces callers down to `TextElementConfig.Builder` for these capabilities.

### Task 4: Add four-side text padding parity

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Add:
  - `TextElementConfig.Builder.textPadding(int left, int top, int right, int bottom)`
  - `TextElement.Builder.textPadding(int left, int top, int right, int bottom)`

- [ ] Match V1 validation semantics for non-negative values.

- [ ] Add tests for:
  - four-side padding values are preserved in layout result
  - rendered/background bounds expand as expected
  - negative values fail fast

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - All V1 text padding entry points have an equivalent V2 API.

### Task 5: Support direct Font injection

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElementConfig.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Add optional `Font font` to `TextElementConfig`.

- [ ] Add:
  - `TextElementConfig.Builder.font(Font font)`
  - `TextElement.Builder.font(Font font)`

- [ ] Update font resolution precedence in `TextLayoutEngine`:
  1. explicit V2 `Font`
  2. explicit `fontName/fontStyle/fontSize`
  3. global config font

- [ ] Add tests for:
  - direct `Font` object controls size/style
  - `fontName/fontStyle/fontSize` still work
  - null `Font` rejects consistently

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - V2 covers the V1 `setFont(Font)` use case.

### Task 6: Align exception semantics for rich text restrictions

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextLayoutEngine.java`
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Replace `UnsupportedOperationException` for rich text `autoFitText` and `JUSTIFY` restrictions with the repo’s expected text-element exception style.

- [ ] Match V1 message wording unless there is a strong reason not to:
  - `rich text span does not support autoFitText yet`
  - `rich text span does not support justify yet`

- [ ] Add failing tests for both error cases before changing behavior.

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - Migration does not introduce exception-type surprises for known unsupported rich-text operations.

### Task 7: Make V2 compatible with RepeatElement

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/TextElement.java`
- Modify if needed: `src/main/java/com/bytefuture/easy/poster/element/advance/RepeatElement.java`
- Modify or Create: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`

- [ ] Preferred implementation: change `TextElement` to extend `AbstractRepeatableElement<TextElement>` instead of `AbstractElement<TextElement>`.

- [ ] Re-check for any compile fallout caused by the inheritance change.

- [ ] Add an integration-style test proving a `TextElement` can be wrapped by `RepeatElement` and rendered repeatedly without type errors.

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - Existing repeat pipeline accepts V2 text without special adapters.

### Task 8: Port the missing V1 test matrix into V2 tests

**Files:**
- Modify: `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java`
- Create if needed: `src/test/java/com/bytefuture/easy/poster/ui/v2/V2TextElementCompatibilityTest.java`

- [ ] Port the following V1 behavior tests into V2 equivalents:
  - wrapped text total height
  - auto-fit does not mutate original configured size
  - explicit newlines without wrap
  - compose layout respects multiline height
  - center-align inside measured block
  - justify wrapped intermediate line
  - max lines + ellipsis after wrap
  - width-constrained ellipsis
  - width-constrained clip
  - letter spacing expands width
  - long token wrapping under letter spacing
  - shadow + stroke expand bounds
  - text padding expands bounds
  - underline renders additional pixels
  - background color renders
  - shadow + stroke render
  - rich text color split render
  - rich text wrap across spans
  - rich text per-span font size
  - rich text explicit newlines
  - rich text ellipsis within width
  - rich text max lines after wrap
  - absolute position + baseline offsets
  - setter/builder validation

- [ ] Prefer one-for-one method names mirroring the V1 test intent so drift stays obvious.

- [ ] Run:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Acceptance:
  - V2 has a comparable behavioral safety net to V1.

### Task 9: Update examples and README to match real API

**Files:**
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/README.md`
- Modify: `src/main/java/com/bytefuture/easy/poster/element/v2/V2Examples.java`

- [ ] Remove API examples that do not exist.

- [ ] Update examples to prefer the finalized public builder surface after Tasks 1 to 5.

- [ ] Ensure README examples compile conceptually against the actual API.

- [ ] If `maxTextWidth(...)` is introduced only as an alias, document one preferred name and keep the other as compatibility-only.

- [ ] Acceptance:
  - V2 documentation no longer advertises unsupported methods.

### Task 10: Run the final parity verification pass

**Files:**
- No code changes unless a verification failure reveals a defect.

- [ ] Run the narrow suite first:
  `mvn -q "-Dtest=V2TextElementTest" test`

- [ ] Run the broader text verification suite:
  `mvn -q "-Dtest=EnhanceTextElementTest,V2TextElementTest" test`

- [ ] If a V2 UI compatibility test file exists, run it too:
  `mvn -q "-Dtest=V2TextElementCompatibilityTest,V2TextElementPngTest" test`

- [ ] Compare V1 and V2 feature matrix one final time and confirm all previously listed gaps are either closed or intentionally documented.

- [ ] Acceptance:
  - V2 has explicit coverage for all parity items except any intentionally deferred work.
  - Remaining deltas, if any, are documented in README and test comments.

## Suggested Commit Slices

- Commit 1: explicit width parity + tests
- Commit 2: alignment inference parity + tests
- Commit 3: builder surface parity
- Commit 4: padding + font object parity
- Commit 5: exception semantics + repeat compatibility
- Commit 6: migrated test matrix
- Commit 7: README/examples cleanup

## Risks

- Changing `TextElement` inheritance for repeat compatibility may expose assumptions in unrelated code.
- Alignment inference can silently change existing V2 behavior if callers relied on current default-left behavior.
- Adding aliases like `maxTextWidth(...)` can create API duplication if not documented clearly.

## Definition of Done

- V2 exposes all public entry points needed for practical V1 migration.
- V2 matches V1 for default alignment inference and constrained-width behavior.
- V2 is accepted by `RepeatElement`.
- V2 test coverage is expanded to cover the V1 feature matrix.
- V2 README/examples reflect the real API only.
