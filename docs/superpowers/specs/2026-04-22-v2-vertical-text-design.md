# V2 TextElement Vertical Text Design

## Goal

Add vertical plain-text layout support to `com.bytefuture.easy.poster.element.v2.TextElement` without breaking the current V2 architecture.

The feature must support:

- Long text input through `vertical(String text)`, with internal automatic splitting into columns
- Pre-split column input through `vertical(List<String> columns)`
- Column draw direction: left-to-right and right-to-left
- In-column vertical alignment: top, middle, bottom, justify

## Scope

In scope:

- `TextElement` V2 plain-text vertical layout
- Builder/config API additions required for vertical mode
- Layout and renderer changes for vertical columns
- Regression tests for vertical behavior and non-vertical compatibility

Out of scope:

- Rich text / `TextSpan` vertical layout
- HTML vertical layout
- Public custom splitter API

## API Design

Add a vertical layout mode and overloads on the builder/config surface.

### New builder methods

- `vertical(String text)`
  - Enables vertical layout mode
  - Stores the source text for internal auto splitting into columns
- `vertical(List<String> columns)`
  - Enables vertical layout mode
  - Uses explicit column content and bypasses internal auto splitting

### New config fields

- `textLayoutMode`: `HORIZONTAL | VERTICAL`
- `verticalSourceText`: source text when vertical content is provided as one string
- `verticalColumns`: explicit columns when provided by caller
- `verticalDirection`: `LEFT_TO_RIGHT | RIGHT_TO_LEFT`
- `verticalAlign`: `TOP | MIDDLE | BOTTOM | JUSTIFY`
- `verticalLayoutHeight`: height constraint for column distribution and total block sizing
- `columnSpacing`: spacing between columns

### Precedence rules

1. `vertical(List<String>)` has highest priority
2. `vertical(String)` is used when explicit columns are absent
3. Existing horizontal `text` flow remains unchanged unless vertical mode is enabled

## Layout Rules

### Column generation

When `vertical(List<String>)` is used:

- Each list entry is one rendered column
- Empty strings are allowed and produce an empty column with zero glyph count

When `vertical(String)` is used:

- The engine normalizes line breaks
- Internal splitting converts the input into columns
- No public custom splitter hook is exposed in this iteration

### Direction

- `LEFT_TO_RIGHT`: first column placed on the left, following columns expand to the right
- `RIGHT_TO_LEFT`: first column placed on the right, following columns expand to the left

### In-column alignment

Given a resolved column height:

- `TOP`: first glyph starts at the top
- `MIDDLE`: glyph stack is vertically centered
- `BOTTOM`: last glyph sits on the bottom edge
- `JUSTIFY`: first glyph sits on the top edge, last glyph sits on the bottom edge, remaining glyphs are evenly distributed

### Metrics

- Column width is the max glyph width within that column
- Column height uses the configured height constraint if present; otherwise it is the natural glyph-stack height
- Overall width is the sum of column widths plus inter-column spacing
- Overall height is the max resolved column height

## Architecture

Keep the current V2 split:

- `TextElementConfig`: stores vertical layout options
- `TextLayoutEngine`: resolves vertical columns and computes glyph positions
- `TextRenderer`: draws vertical glyphs column by column

Do not collapse vertical behavior back into `TextElement`.

## Rendering Strategy

Reuse the existing plain-text drawing code where possible, but add a vertical render path:

- Layout produces per-column glyph positions
- Renderer draws one glyph at a time for vertical columns
- Existing background, shadow, stroke, underline, and clipping behavior remains horizontal-only unless already compatible

For this iteration:

- Vertical mode supports plain text rendering
- Background, shadow, and stroke should continue to work when applied to the full text block
- Underline and strike-through are not introduced as new vertical-specific behaviors; existing behavior should remain safe and non-crashing

## Validation and Errors

- Reject `null` for `vertical(String)` and `vertical(List<String>)`
- Reject `null` list items in `vertical(List<String>)`
- Reject invalid `verticalLayoutHeight <= 0` if provided
- Reject invalid `columnSpacing < 0`

## Testing Plan

Add failing tests first in `src/test/java/com/bytefuture/easy/poster/func/text/V2TextElementTest.java` for:

- explicit columns override auto-generated columns
- `vertical(String)` generates multiple columns for long content
- `LEFT_TO_RIGHT` column order
- `RIGHT_TO_LEFT` column order
- `TOP` alignment
- `MIDDLE` alignment
- `BOTTOM` alignment
- `JUSTIFY` alignment
- empty column handling
- horizontal layout regression coverage

## Risks

- Existing `LayoutLine` may not be expressive enough for per-glyph vertical coordinates
- Decoration metrics were written for horizontal text and may need guarded behavior in vertical mode
- Automatic split heuristics for `vertical(String)` may need follow-up tuning after initial release

## Recommendation

Implement vertical layout only for plain text in V2, with two public entry points:

- `vertical(String)`
- `vertical(List<String>)`

This keeps the API small, avoids exposing unstable splitter semantics, and fits the current V2 layering with a reviewable diff.
