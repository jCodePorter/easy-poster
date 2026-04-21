package com.bytefuture.easy.poster.text.html;

import com.bytefuture.easy.poster.model.TextSpan;
import com.bytefuture.easy.poster.utils.HexUtils;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/**
 * Minimal HTML-to-TextSpan parser for the styles already supported by V2 rich text.
 */
public final class HtmlTextSpanParser {

    public List<TextSpan> parse(String html) {
        if (html == null || html.trim().isEmpty()) {
            return Collections.emptyList();
        }

        final ParseContext context = new ParseContext();
        context.states.push(HtmlStyleState.empty());

        try {
            new ParserDelegator().parse(new StringReader(wrapHtml(html)), new HTMLEditorKit.ParserCallback() {
                @Override
                public void handleText(char[] data, int pos) {
                    context.appendNormalizedText(new String(data));
                }

                @Override
                public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributeSet, int pos) {
                    context.flushPendingText();
                    if (isBlockTag(tag) && context.hasVisibleOutput()) {
                        context.ensureNewLine();
                    }
                    context.states.push(context.states.peek().derive(tag, attributeSet));
                }

                @Override
                public void handleEndTag(HTML.Tag tag, int pos) {
                    context.flushPendingText();
                    if (context.states.size() > 1) {
                        context.states.pop();
                    }
                    if (isBlockTag(tag) && context.hasVisibleOutput()) {
                        context.ensureNewLine();
                    }
                }

                @Override
                public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributeSet, int pos) {
                    if (tag == HTML.Tag.BR) {
                        context.flushPendingText();
                        context.ensureNewLine();
                        return;
                    }
                    handleStartTag(tag, attributeSet, pos);
                    handleEndTag(tag, pos);
                }
            }, true);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse html", ex);
        }

        context.flushPendingText();
        context.trimTrailingNewLines();
        return context.spans;
    }

    private String wrapHtml(String html) {
        return "<html><body>" + html + "</body></html>";
    }

    private static boolean isBlockTag(HTML.Tag tag) {
        return tag == HTML.Tag.P
                || tag == HTML.Tag.DIV
                || tag == HTML.Tag.LI
                || tag == HTML.Tag.UL
                || tag == HTML.Tag.OL
                || tag == HTML.Tag.H1
                || tag == HTML.Tag.H2
                || tag == HTML.Tag.H3
                || tag == HTML.Tag.H4
                || tag == HTML.Tag.H5
                || tag == HTML.Tag.H6;
    }

    private static final class ParseContext {
        private final List<TextSpan> spans = new ArrayList<TextSpan>();
        private final Deque<HtmlStyleState> states = new ArrayDeque<HtmlStyleState>();
        private final StringBuilder pendingText = new StringBuilder();

        private void appendNormalizedText(String rawText) {
            for (int i = 0; i < rawText.length(); i++) {
                char current = rawText.charAt(i);
                if (Character.isWhitespace(current)) {
                    char lastChar = lastOutputChar();
                    if (lastChar == 0 || lastChar == ' ' || lastChar == '\n') {
                        continue;
                    }
                    this.pendingText.append(' ');
                    continue;
                }
                this.pendingText.append(current);
            }
        }

        private void ensureNewLine() {
            char lastChar = lastOutputChar();
            if (lastChar == 0 || lastChar == '\n') {
                return;
            }
            this.pendingText.append('\n');
            flushPendingText();
        }

        private boolean hasVisibleOutput() {
            if (this.pendingText.length() > 0) {
                for (int i = 0; i < this.pendingText.length(); i++) {
                    if (!Character.isWhitespace(this.pendingText.charAt(i))) {
                        return true;
                    }
                }
            }
            for (TextSpan span : this.spans) {
                if (span.getText() != null && !span.getText().trim().isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        private void flushPendingText() {
            if (this.pendingText.length() == 0) {
                return;
            }

            String text = this.pendingText.toString();
            this.pendingText.setLength(0);
            if (text.isEmpty()) {
                return;
            }

            HtmlStyleState state = this.states.peek();
            TextSpan span = TextSpan.of(text);
            if (state.getColor() != null) {
                span.setColor(state.getColor());
            }
            if (state.getFontStyle() != null) {
                span.setFontStyle(state.getFontStyle());
            }
            if (state.getFontSize() != null) {
                span.setFontSize(state.getFontSize());
            }
            if (Boolean.TRUE.equals(state.getUnderline())) {
                span.setUnderline(true);
            }
            if (Boolean.TRUE.equals(state.getStrikeThrough())) {
                span.setStrikeThrough(true);
            }

            if (!this.spans.isEmpty() && hasSameStyle(this.spans.get(this.spans.size() - 1), span)) {
                TextSpan merged = TextSpan.of(this.spans.get(this.spans.size() - 1).getText() + text);
                copyStyle(this.spans.get(this.spans.size() - 1), merged);
                this.spans.set(this.spans.size() - 1, merged);
                return;
            }
            this.spans.add(span);
        }

        private void trimTrailingNewLines() {
            if (this.spans.isEmpty()) {
                return;
            }
            TextSpan last = this.spans.get(this.spans.size() - 1);
            String text = last.getText();
            int end = text.length();
            while (end > 0 && text.charAt(end - 1) == '\n') {
                end--;
            }
            if (end == text.length()) {
                return;
            }
            if (end == 0) {
                this.spans.remove(this.spans.size() - 1);
                return;
            }

            TextSpan trimmed = TextSpan.of(text.substring(0, end));
            copyStyle(last, trimmed);
            this.spans.set(this.spans.size() - 1, trimmed);
        }

        private char lastOutputChar() {
            if (this.pendingText.length() > 0) {
                return this.pendingText.charAt(this.pendingText.length() - 1);
            }
            if (this.spans.isEmpty()) {
                return 0;
            }
            String text = this.spans.get(this.spans.size() - 1).getText();
            return text.isEmpty() ? 0 : text.charAt(text.length() - 1);
        }

        private boolean hasSameStyle(TextSpan left, TextSpan right) {
            return sameColor(left.getColor(), right.getColor())
                    && sameInteger(left.getFontStyle(), right.getFontStyle())
                    && sameInteger(left.getFontSize(), right.getFontSize())
                    && sameBoolean(left.getUnderline(), right.getUnderline())
                    && sameBoolean(left.getStrikeThrough(), right.getStrikeThrough());
        }

        private boolean sameColor(Color left, Color right) {
            return left == null ? right == null : left.equals(right);
        }

        private boolean sameInteger(Integer left, Integer right) {
            return left == null ? right == null : left.equals(right);
        }

        private boolean sameBoolean(Boolean left, Boolean right) {
            return left == null ? right == null : left.equals(right);
        }

        private void copyStyle(TextSpan source, TextSpan target) {
            if (source.getColor() != null) {
                target.setColor(source.getColor());
            }
            if (source.getFontStyle() != null) {
                target.setFontStyle(source.getFontStyle());
            }
            if (source.getFontSize() != null) {
                target.setFontSize(source.getFontSize());
            }
            if (source.getUnderline() != null) {
                target.setUnderline(source.getUnderline());
            }
            if (source.getStrikeThrough() != null) {
                target.setStrikeThrough(source.getStrikeThrough());
            }
        }
    }

    private static final class HtmlStyleState {
        private final Color color;
        private final Integer fontStyle;
        private final Integer fontSize;
        private final Boolean underline;
        private final Boolean strikeThrough;

        private HtmlStyleState(Color color, Integer fontStyle, Integer fontSize,
                               Boolean underline, Boolean strikeThrough) {
            this.color = color;
            this.fontStyle = fontStyle;
            this.fontSize = fontSize;
            this.underline = underline;
            this.strikeThrough = strikeThrough;
        }

        public static HtmlStyleState empty() {
            return new HtmlStyleState(null, null, null, null, null);
        }

        public HtmlStyleState derive(HTML.Tag tag, MutableAttributeSet attributeSet) {
            Color nextColor = this.color;
            Integer nextFontStyle = this.fontStyle;
            Integer nextFontSize = this.fontSize;
            Boolean nextUnderline = this.underline;
            Boolean nextStrikeThrough = this.strikeThrough;

            if (tag == HTML.Tag.B || tag == HTML.Tag.STRONG) {
                nextFontStyle = appendFontStyle(nextFontStyle, Font.BOLD);
            } else if (tag == HTML.Tag.I || tag == HTML.Tag.EM) {
                nextFontStyle = appendFontStyle(nextFontStyle, Font.ITALIC);
            } else if (tag == HTML.Tag.U) {
                nextUnderline = Boolean.TRUE;
            } else if (tag == HTML.Tag.S || tag == HTML.Tag.STRIKE) {
                nextStrikeThrough = Boolean.TRUE;
            } else if (tag == HTML.Tag.FONT) {
                Object colorAttr = attributeSet.getAttribute(HTML.Attribute.COLOR);
                if (colorAttr != null) {
                    Color parsedColor = parseColor(colorAttr.toString());
                    if (parsedColor != null) {
                        nextColor = parsedColor;
                    }
                }
                Object sizeAttr = attributeSet.getAttribute(HTML.Attribute.SIZE);
                if (sizeAttr != null) {
                    Integer parsedSize = parseHtmlFontSize(sizeAttr.toString());
                    if (parsedSize != null) {
                        nextFontSize = parsedSize;
                    }
                }
            }

            Object styleAttr = attributeSet.getAttribute(HTML.Attribute.STYLE);
            if (styleAttr != null) {
                String[] styleRules = styleAttr.toString().split(";");
                for (String rule : styleRules) {
                    String[] kv = rule.split(":", 2);
                    if (kv.length != 2) {
                        continue;
                    }
                    String key = kv[0].trim().toLowerCase(Locale.ROOT);
                    String value = kv[1].trim().toLowerCase(Locale.ROOT);
                    if ("color".equals(key)) {
                        Color parsedColor = parseColor(value);
                        if (parsedColor != null) {
                            nextColor = parsedColor;
                        }
                    } else if ("font-size".equals(key)) {
                        Integer parsedSize = parseCssFontSize(value);
                        if (parsedSize != null) {
                            nextFontSize = parsedSize;
                        }
                    } else if ("font-weight".equals(key)) {
                        if (value.contains("bold")) {
                            nextFontStyle = appendFontStyle(nextFontStyle, Font.BOLD);
                        } else {
                            try {
                                int weight = Integer.parseInt(value.replaceAll("[^0-9]", ""));
                                if (weight >= 600) {
                                    nextFontStyle = appendFontStyle(nextFontStyle, Font.BOLD);
                                }
                            } catch (NumberFormatException ignore) {
                                // Ignore unsupported weight syntax.
                            }
                        }
                    } else if ("font-style".equals(key)) {
                        if (value.contains("italic") || value.contains("oblique")) {
                            nextFontStyle = appendFontStyle(nextFontStyle, Font.ITALIC);
                        }
                    } else if ("text-decoration".equals(key)) {
                        if (value.contains("underline")) {
                            nextUnderline = Boolean.TRUE;
                        }
                        if (value.contains("line-through")) {
                            nextStrikeThrough = Boolean.TRUE;
                        }
                    }
                }
            }

            return new HtmlStyleState(nextColor, nextFontStyle, nextFontSize, nextUnderline, nextStrikeThrough);
        }

        public Color getColor() {
            return this.color;
        }

        public Integer getFontStyle() {
            return this.fontStyle;
        }

        public Integer getFontSize() {
            return this.fontSize;
        }

        public Boolean getUnderline() {
            return this.underline;
        }

        public Boolean getStrikeThrough() {
            return this.strikeThrough;
        }

        private Integer appendFontStyle(Integer currentStyle, int styleFlag) {
            return Integer.valueOf((currentStyle == null ? Font.PLAIN : currentStyle.intValue()) | styleFlag);
        }

        private Color parseColor(String value) {
            String normalized = value.trim();
            if (normalized.isEmpty()) {
                return null;
            }
            if (normalized.startsWith("#")) {
                return HexUtils.hexToColor(normalized);
            }
            if (normalized.startsWith("rgb(") && normalized.endsWith(")")) {
                String[] parts = normalized.substring(4, normalized.length() - 1).split(",");
                if (parts.length == 3) {
                    try {
                        return new Color(
                                Integer.parseInt(parts[0].trim()),
                                Integer.parseInt(parts[1].trim()),
                                Integer.parseInt(parts[2].trim())
                        );
                    } catch (NumberFormatException ignore) {
                        return null;
                    }
                }
            }
            try {
                Field colorField = Color.class.getField(normalized);
                return (Color) colorField.get(null);
            } catch (NoSuchFieldException ignore) {
                return parseNamedColor(normalized);
            } catch (IllegalAccessException ignore) {
                return null;
            }
        }

        private Color parseNamedColor(String value) {
            if ("lightgray".equals(value) || "lightgrey".equals(value)) {
                return Color.lightGray;
            }
            if ("darkgray".equals(value) || "darkgrey".equals(value)) {
                return Color.darkGray;
            }
            if ("gray".equals(value) || "grey".equals(value)) {
                return Color.gray;
            }
            return null;
        }

        private Integer parseCssFontSize(String value) {
            String digits = value.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) {
                return null;
            }
            return Integer.valueOf(Integer.parseInt(digits));
        }

        private Integer parseHtmlFontSize(String value) {
            try {
                int size = Integer.parseInt(value.trim());
                switch (size) {
                    case 1:
                        return Integer.valueOf(10);
                    case 2:
                        return Integer.valueOf(13);
                    case 3:
                        return Integer.valueOf(16);
                    case 4:
                        return Integer.valueOf(18);
                    case 5:
                        return Integer.valueOf(24);
                    case 6:
                        return Integer.valueOf(32);
                    case 7:
                        return Integer.valueOf(48);
                    default:
                        return size > 0 ? Integer.valueOf(size) : null;
                }
            } catch (NumberFormatException ignore) {
                return null;
            }
        }
    }
}
