# v2 TextElement Block Gradient Design

## Background

`v2 TextElement` already exposes an element-level `gradient` entry, but the current rendering path does not provide a stable, unified block-gradient behavior across plain text, rich text, and vertical text. Rich and vertical rendering branches frequently override `Graphics2D` paint with fragment or glyph colors, which breaks block-level gradient continuity.

This design standardizes gradient semantics across all v2 text modes without expanding the styling surface.

## Goal

When `TextElement.gradient != null`, plain text, rich text, and vertical text should all render text fill using one continuous block-level gradient derived from the actual laid out text region.

The same semantic applies to all three modes:

- The gradient is generated once per text block.
- The gradient spans the actual text layout bounds, not the outer element box.
- Text fill uses the block gradient.
- Background, shadow, stroke, and decoration colors keep existing solid-color behavior.

## Non-Goals

- No `TextSpan.gradient` support.
- No gradient support for background, shadow, stroke, underline, or strike-through.
- No change to text layout rules, wrapping rules, rich span resolution rules, or vertical column rules.
- No new public API beyond the existing `TextElement.gradient(...)`.

## Functional Semantics

### 1. Plain Text

- If `gradient` is absent, behavior remains unchanged.
- If `gradient` is present, all plain text glyph fill uses the block gradient.
- The gradient is continuous across all wrapped lines inside the text block.

### 2. Rich Text

- If `gradient` is absent, existing rich text color behavior remains unchanged.
- If `gradient` is present, rich text fill uses the block gradient for all text fragments.
- `TextSpan.color` remains in the model and layout result, but it does not control text fill in gradient mode.
- `backgroundColor`, `shadow`, `stroke`, underline, and strike-through continue to use their current colors.

### 3. Vertical Text

- If `gradient` is absent, existing vertical text color behavior remains unchanged.
- If `gradient` is present, all vertical glyph fill uses the block gradient.
- Glyph-level color metadata remains available, but it does not control text fill in gradient mode.
- Background, shadow, stroke, and decoration logic remains unchanged.

## Layout Bounds Strategy

The gradient must be based on the actual text layout region so the color transition follows the visible text block instead of stretching across unused element space.

The layout region is defined as:

- `x`: the minimum actual drawing start x among rendered lines or columns
- `y`: the minimum actual drawing start y of the laid out text block
- `width`: the maximum occupied horizontal extent of rendered text
- `height`: the maximum occupied vertical extent of rendered text

Per mode:

- Plain text: use the real occupied area of all lines after alignment and wrapping.
- Rich text: use the real occupied area of all rich fragments after alignment and wrapping.
- Vertical text: use the real occupied area of all vertical glyph columns after direction and alignment are resolved.

The gradient coordinate system must be stable for the whole block, so line-by-line or fragment-by-fragment gradient recreation is not allowed.

## Implementation Shape

### Entry Layer

Keep the existing `TextElement.gradient(...)` API unchanged.

`TextElement.beforeRender(...)` may continue setting a default paint, but final text fill selection must be renderer-controlled because rich and vertical rendering branches override paint during drawing.

### Layout Layer

`TextLayoutEngine` keeps current layout semantics. If the renderer cannot already derive stable text block bounds from `TextLayoutResult`, add the smallest internal support needed to expose or compute those bounds from the layout result.

This support must remain internal and must not expand the public styling model.

### Render Layer

`TextRenderer` is the primary implementation point.

Responsibilities:

- Resolve whether block-gradient mode is active.
- Build one text-fill paint from the computed text block bounds.
- Reuse that paint for plain text, rich text, and vertical text fill drawing.
- Temporarily switch paint only for background, shadow, stroke, and decoration drawing, then restore the text-fill paint.

In gradient mode:

- Plain text fill uses the shared block paint.
- Rich text fill ignores fragment solid colors for fill.
- Vertical text fill ignores glyph solid colors for fill.

Outside gradient mode, current color behavior stays unchanged.

## Testing Strategy

### Functional Tests

Add or update tests in `V2TextElementTest` to verify:

- Plain text with gradient can complete layout and render flow.
- Rich text with gradient can complete layout and render flow.
- Vertical text with gradient can complete layout and render flow.
- Rich text still preserves span style metadata even when fill is gradient-driven.
- Gradient mode does not alter existing non-fill style behavior contracts.

### PNG UI Tests

Add visual tests covering:

- Plain text block gradient across wrapped lines.
- Rich text block gradient across multiple spans.
- Vertical text block gradient across multiple columns.

The PNG tests are used as regression evidence for rendering continuity and non-crashing behavior, not as pixel-perfect assertions.

## Compatibility

This design is backward compatible:

- Existing non-gradient text behavior remains unchanged.
- Existing element-level gradient API is reused.
- Existing rich text and vertical text models are preserved.

The only intentional precedence change is:

- In gradient mode, text fill is controlled by block gradient instead of fragment or glyph solid color.

## Risks

- If the text block bounds are computed from element bounds instead of actual layout occupancy, gradients may look visually stretched or offset.
- Rich and vertical rendering paths currently override paint in multiple places, so implementation must restore fill paint consistently after temporary paint changes.
- Decoration rendering must remain excluded from gradient fill to avoid accidental visual regressions.

## Recommended Rollout

1. Implement shared block-bound computation for the rendered text region.
2. Centralize text fill paint resolution in `TextRenderer`.
3. Update plain, rich, and vertical drawing branches to use the shared fill paint.
4. Add functional tests.
5. Add PNG UI regression tests.
